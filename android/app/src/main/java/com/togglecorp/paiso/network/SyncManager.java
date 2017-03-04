package com.togglecorp.paiso.network;

import android.content.Context;

import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.SerializableModel;
import com.togglecorp.paiso.db.Transaction;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.db.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
* Central class to handle all synchronization between local and server data.
*
* The idea is to start by collecting all items that are modified by the app.
* Then postSerializable each modifications and mark them unmodified.
* On completion of all posts, start to fetch all data from server and update local.
* Since some earlier postSerializable may fail, replace local data only if it isn't modified.
*
* */
public class SyncManager {


    private Context mContext;
    private DbHelper mDbHelper;
    private List<SyncListener> mListeners = new ArrayList<>();

    private String mUserId;

    public SyncManager(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(context);
    }

    // Request synchronization to be performed
    public void requestSync() {
        // Making sure two sync requests are handled one after another
        synchronized (SyncManager.class) {
            sync();
        }

        // Sync complete, handle the listeners
        for (SyncListener listener: mListeners) {
            listener.onSync();
        }
    }


    public void addListener(SyncListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(SyncListener listener) {
        mListeners.remove(listener);
    }


    // Actual synchronization logic begins here

    private void sync() {
        // Step 1: Post modified objects
        postSerializable(User.class, "/api/v1/user", "userId");

        // Step 2: Post modified contacts
        postSerializable(Contact.class, "/api/v1/contact", "contactId");

        // Step 3: Post modified transactions
        postSerializable(Transaction.class, "/api/v1/transaction", "transactionId");
        postSerializable(TransactionData.class, "/api/v1/transaction-data", "dataId");

        // Step 4: Get from server, but only keep objects if they are unmodified locally
        getParties();
    }

    private <T extends SerializableModel> void postSerializable(final Class<T> c, final String apiUrl, final String idField) {
        // Set to unmodified on successful postSerializable
        JsonRequestListener callback = new JsonRequestListener() {
            @Override
            public void onRequestComplete(JsonRequest request) {
                JSONObject data = request.getSuccessDataObject();
                if (data != null && data.has("userId")) {
                    T object = T.get(c, mDbHelper, idField + " = ?",
                            new String[] {request.getResult().optString(idField, "")});

                    if (object != null) {
                        object.modified = false;
                        object.save(mDbHelper);
                    }
                }
            }
        };

        // Now the actual postSerializable for all modified data
        List<T> objects = T.query(c, mDbHelper, "modified = 1", null);
        for (T object: objects) {
            try {
                JSONObject jsObj = object.toJson();
                jsObj.put("userId", mUserId);
                new JsonRequest(mContext)
                        .setData(jsObj)
                        .post(apiUrl,  callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getParties() {
        // Get all parties (user/contact) we are involved in and their transactions
        // Note to delete all unmodified data before reading new values
        // and not to override unmodified data

        new JsonRequest(mContext).get("/api/v1/party/?userId=" + mUserId, new JsonRequestListener() {
            @Override
            public void onRequestComplete(JsonRequest request) {
                JSONArray data = request.getSuccessDataArray();
                if (data != null) {

                    // Successful response
                    // Delete all unmodified transactions and all users except us
                    // We keep the contacts
                    TransactionData.delete(Transaction.class, mDbHelper, "modified = 0", null);
                    Transaction.delete(Transaction.class, mDbHelper, "modified = 0", null);
                    User.delete(User.class, mDbHelper, "userId != ?", new String[]{mUserId});

                    // Now go through each user and save their transactions
                    for (int i=0; i<data.length(); i++) {
                        JSONObject dataObj = data.optJSONObject(i);
                        if (dataObj != null) {

                            // First save the user/contact just in case it's not in our local db
                            String userId = null;
                            Long contactId = null;

                            if (dataObj.optBoolean("unregistered")) {
                                contactId = dataObj.optLong("id", -1);
                                Contact contact = Contact.get(Contact.class, mDbHelper, "contactId = ?", new String[]{contactId+""});

                                boolean create = true;
                                if (contact != null) {
                                    if (!contact.modified) {
                                        contact.delete(mDbHelper);
                                    } else {
                                        create = false;
                                    }
                                }

                                if (create) {
                                    new Contact(contactId,
                                            dataObj.optString("displayName"),
                                            (String)dataObj.opt("email"),
                                            (String)dataObj.opt("phone"),
                                            (String)dataObj.opt("photoUrl")).save(mDbHelper);
                                }
                            }
                            else {
                                userId = dataObj.optString("id", "");
                                User user = User.get(User.class, mDbHelper, "userId = ?", new String[]{userId});

                                if (user == null) {
                                    new User(userId,
                                            dataObj.optString("displayName"),
                                            (String)dataObj.opt("email"),
                                            (String)dataObj.opt("phone"),
                                            (String)dataObj.opt("photoUrl")).save(mDbHelper);
                                }
                            }


                            // Next save the transactions
                            JSONArray transactions = dataObj.optJSONArray("transactions");
                            if (transactions != null) {
                                for (int j=0; j<transactions.length(); j++) {

                                    JSONObject transactionObj = transactions.optJSONObject(j);
                                    if (transactionObj != null) {
                                        Long tid = transactionObj.optLong("transactionId", -1);
                                        Transaction transaction = Transaction.get(Transaction.class, mDbHelper, "transactionId = ?", new String[]{tid+""});
                                        boolean byMe = transactionObj.optBoolean("by");

                                        if (transaction == null) {
                                            new Transaction(
                                                    tid,
                                                    byMe ? mUserId : userId,
                                                    byMe ? userId : mUserId,
                                                    contactId,
                                                    transactionObj.optBoolean("isOwner") ? mUserId : userId
                                            ).save(mDbHelper);
                                        }

                                        // The history of this transaction
                                        JSONArray history = transactionObj.optJSONArray("history");
                                        if (history != null) {
                                            for (int k=0; k<history.length(); k++) {
                                                JSONObject item = history.optJSONObject(j);
                                                if (item != null) {
                                                    Long dataId = item.optLong("dataId", -1);
                                                    TransactionData td = TransactionData.get(TransactionData.class, mDbHelper, "dataId = ?", new String[]{dataId+""});

                                                    if (td == null) {
                                                        new TransactionData(
                                                                dataId,
                                                                item.optString("title"),
                                                                (float)item.optDouble("amount"),
                                                                item.optBoolean("approved"),
                                                                tid
                                                        ).save(mDbHelper);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
