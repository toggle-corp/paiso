package com.togglecorp.paiso.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.togglecorp.paiso.helpers.PhoneUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Contact extends SerializableRemoteModel {
    public Integer contactId = null;
    public Integer linkedUser = null;

    public String displayName;
    public String email;
    public String phone;
    public String photoUrl;


    public Contact() {}

    public Contact(Integer contactId, Integer linkedUser, String displayName, String email, String phone, String photoUrl) {
        this.contactId = contactId;
        this.linkedUser = linkedUser;
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }

    public List<PaisoTransaction> getAllTransactions(DbHelper dbHelper) {
        return PaisoTransaction.query(PaisoTransaction.class, dbHelper, "contact = ? and deleted = 0",
                new String[]{contactId+""});
    }

    public long getLatestTransactionTime(DbHelper dbHelper) {
        List<PaisoTransaction> transactions = getAllTransactions(dbHelper);
        if (transactions.size() == 0) {
            return -1;
        }

        long maxTime = -1;
        for (int i=0; i<transactions.size(); i++) {
            TransactionData data = transactions.get(i).getLatest(dbHelper);
            if (data != null && data.timestamp > maxTime) {
                maxTime = data.timestamp;
            }
        }

        return maxTime;
    }

    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        try {
            object.put("contactId", contactId==null?JSONObject.NULL:contactId);
            object.put("linkedUserId", linkedUser==null?JSONObject.NULL:linkedUser);

            object.put("displayName", displayName);
            object.put("email", email==null?JSONObject.NULL:email);
            object.put("phone", phone==null?JSONObject.NULL:phone);
            object.put("photoUrl", photoUrl==null?JSONObject.NULL:photoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public void fromJson(JSONObject json) {
        if (json == null) {
            return;
        }

        contactId = optInteger(json, "contactId");
        linkedUser = optInteger(json, "linkedUserId");
        displayName = json.optString("displayName");
        email = optString(json, "email");
        phone = optString(json, "phone");
        photoUrl = optString(json, "photoUrl");
    }


    public static void readContacts(DbHelper dbHelper) {
        ContentResolver contentResolver = dbHelper.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String photoUrl = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String email = null;
                String phone = null;

                Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (emails != null) {
                    if (emails.moveToNext()) {
                        email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        if (email != null && email.length() == 0) {
                            email = null;
                        }
                    }
                    emails.close();
                }

                Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
                if (phones != null) {
                    if (phones.moveToNext()) {
                        // Phone number is always formatted using E164
                        phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phone != null && phone.length() == 0) {
                            phone = null;
                        } else if (phone != null) {
                            phone = PhoneUtils.getNormalizedPhone(dbHelper.getContext(), phone);
                        }
                    }
                    phones.close();
                }

                if (displayName == null || displayName.length() == 0 || (email == null && phone == null)) {
                    continue;
                }

                Contact contact;
                if (email != null) {
                    contact = Contact.get(Contact.class, dbHelper, "email = ?", new String[]{email});
                }
                else {
                    contact = Contact.get(Contact.class, dbHelper, "phone = ?", new String[]{phone});
                }

                if (contact == null) {
                    contact = new Contact();
                }
                else if (isSame(contact.displayName, displayName) &&
                        isSame(contact.email, email) &&
                        isSame(contact.phone, phone) &&
                        isSame(contact.photoUrl, photoUrl)) {
                    continue;
                }

                contact.displayName = displayName;
                contact.phone = phone;
                contact.email = email;
                contact.photoUrl = photoUrl;
                contact.modified = true;
                contact.save(dbHelper);
            }

            cursor.close();
        }
    }

    private static boolean isSame(String a, String b) {
        return a == null && b == null || !(a == null || b == null) && a.equals(b);

    }
}
