package com.togglecorp.paiso;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static final String TAG = "Database";

    public String selfId;
    public User self;

    private Database() {
        // Initialize firebase database
        mRef = getDatabase();
    }
    private static Database mDatabase;

    public static Database get() {
        if (mDatabase == null)
            mDatabase = new Database();
        return mDatabase;
    }

    public HashMap<String, User> users = new HashMap<>();
    public HashMap<String, Contact> contacts = new HashMap<>();

    public HashMap<String, String> customUsers = new HashMap<>();
    public HashMap<String, Transaction> transactions;

    // Make sure every activity/fragment removes listener at or before onStop
    public List<RefreshListener> mRefreshListeners = new ArrayList<>();

    public void refresh() {
        for (RefreshListener listener: mRefreshListeners)
            if (listener != null)
                listener.refresh();
    }


    // Firebase stuffs
    private static FirebaseDatabase mFbDb;
    private DatabaseReference mRef;

    public static DatabaseReference getDatabase() {
        if (mFbDb == null) {
            mFbDb = FirebaseDatabase.getInstance();
            mFbDb.setPersistenceEnabled(true);
        }
        return mFbDb.getReference();
    }


    // Firebase data listener
    private Map<Query, ValueEventListener> mListeners = new HashMap<>();
    private void addListener(Query ref, ValueEventListener listener) {
        if (mListeners.containsKey(ref))
            return;

        Log.d(TAG, "Adding listener for: " + ref.toString());

        ref.addValueEventListener(listener);
        mListeners.put(ref, listener);
    }

    public void startSync(Context context) {
        // First store self information
        DatabaseReference selfRef = mRef.child("users").child(selfId);
        selfRef.child("displayName").setValue(self.displayName);
        selfRef.child("email").setValue(self.email);
        selfRef.child("photoUrl").setValue(self.photoUrl);

        // Now get all transactions of self
        ValueEventListener transactionsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                // dataSnapshot is a list of transaction ids
                List<String> transactions =
                        dataSnapshot.getValue(new GenericTypeIndicator<List<String>>() {});

                // listener for each transaction
                for (String t: transactions)
                    listenForTransaction(t);

                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mRef.child("user_transactions").child(selfId), transactionsListener);

        // Get custom added users
        ValueEventListener customUsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                // Store each user
                for (DataSnapshot user: dataSnapshot.getChildren()) {
                    if (user == null || !user.exists())
                        continue;

                    customUsers.put(user.getKey(), user.getValue(String.class));
                }

                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mRef.child("custom_users").child(selfId), customUsersListener);

        // Get all contacts who are also in Paiso
        fetchContactUsers(context);
    }

    private void listenForTransaction(String transactionId) {
        ValueEventListener transactionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                // Store the transaction
                Transaction transaction = dataSnapshot.getValue(Transaction.class);
                transactions.put(dataSnapshot.getKey(), transaction);

                // Get the users as long as they are not custom added users
                if (!transaction.customUser) {
                    if (!transaction.to.equals(selfId))
                        listenForUser(transaction.to);
                    if (!transaction.by.equals(selfId))
                        listenForUser(transaction.by);
                }

                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mRef.child("transactions").child(transactionId), transactionListener);
    }

    private void listenForUser(String userId) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                // Store the user
                users.put(dataSnapshot.getKey(), dataSnapshot.getValue(User.class));
                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mRef.child("users").child(userId), userListener);
    }

    private void fetchContactUsers(Context context) {
        // Make sure we have permission to read the contacts
        if (!context.getSharedPreferences("com.toggle.paiso.defaults", 0)
                .getBoolean("CAN_READ_CONTACTS", false))
            return;

        contacts.clear();

        // Fetch all contacts who have EMAIL
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[] {
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Email.DATA,
                        ContactsContract.CommonDataKinds.Email.PHOTO_URI
                },
                null, null, null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    // Get details of each contact
                    String id = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String displayName = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String email = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String photoUrl = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.PHOTO_URI));

                    // Check if a user with this email exists in server
                    // TODO: improve this to only check for those users that user selects to search for; or any other improvement possible.
                    checkAndFetchContact(id, new Contact(displayName, email, photoUrl));
                }
            }
            finally {
                cursor.close();

            }
        }
    }

    private void checkAndFetchContact(final String id, final Contact contact) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                if (dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot user = dataSnapshot.getChildren().iterator().next();

                    // Store the contact
                    contact.userId = user.getKey();
                    contacts.put(id, contact);

                    // Also store the user
                    users.put(user.getKey(), user.getValue(User.class));
                    refresh();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mRef.child("users").orderByChild("email").equalTo(contact.email), userListener);
    }


    public void stopSync() {
        for (Map.Entry<Query, ValueEventListener> listener: mListeners.entrySet()) {
            Log.d(TAG, "Removing listener for: " + listener.getKey());
            listener.getKey().removeEventListener(listener.getValue());
        }
        mListeners.clear();

    }

}
