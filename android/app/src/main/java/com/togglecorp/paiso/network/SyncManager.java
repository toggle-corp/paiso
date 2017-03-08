package com.togglecorp.paiso.network;


import android.util.Log;

import com.google.firebase.database.Transaction;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;
import com.togglecorp.paiso.db.SerializableRemoteModel;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.db.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
* Central class transactionTo handle all synchronization between local and server data.
*
* The idea is transactionTo start transactionBy collecting all items that are modified transactionBy the app.
* Then postSerializable each modifications and mark them unmodified.
* On completion of all posts, start transactionTo fetch all data from server and update local.
* Since some earlier postSerializable may fail, replace local data only if it isn't modified.
*
* */
public class SyncManager {
    private static final String TAG = "SyncManager";


    private DbHelper mDbHelper;
    private List<SyncListener> mListeners = new ArrayList<>();

    private static User mUser;

    public SyncManager(DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    public static void setUser(User user) {
        mUser = user;
    }

    // Request synchronization transactionTo be performed
    public void requestSync() {
        if (mUser == null) {
            return;
        }

        // Making sure two sync requests are handled one after another
        synchronized (SyncManager.class) {
            new JsonRequest(mDbHelper.getContext())
                    .setData(mUser.toJson())
                    .post("api/v1/user/", new JsonRequestListener() {
                                @Override
                                public void onRequestComplete(JsonRequest request) {
                                    JSONObject data = request.getSuccessDataObject();
                                    if (data != null && data.has("user")) {
                                        mUser.fromJson(data.optJSONObject("user"));
                                        mUser.modified = false;
                                        mUser.save(mDbHelper);

                                        sync();
                                    }
                                }
                            });
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
        postSerializable(User.class, "api/v1/user/", new PostListener<User>() {
            @Override
            public void onSuccess(JSONObject data, User object) {
                if (data.has("user")){
                    object.fromJson(data.optJSONObject("user"));
                    object.modified = false;
                    object.save(mDbHelper);
                }
            }
        });

        // Step 2: Post modified contacts
        postSerializable(Contact.class, "api/v1/contact/", new PostListener<Contact>() {
            @Override
            public void onSuccess(JSONObject data, Contact object) {
                if (data.has("contact")) {
                    object.fromJson(data.optJSONObject("contact"));
                    object.modified = false;
                    object.save(mDbHelper);
                }
            }
        });

        // Step 3: Post modified transactions
        postSerializable(PaisoTransaction.class, "api/v1/transaction/", new PostListener<PaisoTransaction>() {
            @Override
            public void onSuccess(JSONObject data, PaisoTransaction object) {
                if (data.has("transaction")) {
                    object.fromJson(data.optJSONObject("transaction"));
                    object.modified = false;
                    object.save(mDbHelper);
                }
            }
        });
        postSerializable(TransactionData.class, "/api/v1/transaction-data/", new PostListener<TransactionData>() {
            @Override
            public void onSuccess(JSONObject data, TransactionData object) {
                if (data.has("data")) {
                    object.fromJson(data.optJSONObject("data"));
                    object.modified = false;
                    object.save(mDbHelper);
                }
            }
        });

        // Step 4: Get from server, but only keep objects if they are unmodified locally
        getTransactions();
    }

    private interface PostListener<T> {
        void onSuccess(JSONObject data, T object);
    }
    private <T extends SerializableRemoteModel> void postSerializable(final Class<T> c, final String apiUrl, final PostListener<T> postListener) {
        // Now the actual postSerializable for all modified data
        List<T> objects = T.query(c, mDbHelper, "modified = 1", null);
        for (T object: objects) {
            try {
                final T that = object;
                JSONObject jsObj = object.toJson();
                jsObj.put("userId", mUser.userId);
                new JsonRequest(mDbHelper.getContext())
                        .setData(jsObj)
                        .post(apiUrl, new JsonRequestListener() {
                            @Override
                            public void onRequestComplete(JsonRequest request) {
                                JSONObject data = request.getSuccessDataObject();
                                if (data != null) {
                                    postListener.onSuccess(data, that);
                                }
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private <T extends SerializableRemoteModel> T saveOrUpdate(Class<T> c, JSONObject json, String idKey, T defaultObject) {
        try {
            Integer id = json.optInt(idKey);
            T t = T.get(c, mDbHelper, idKey + " = ?", new String[]{id+""});
            if (t != null) {
                if (t.modified) {
                    return t;
                }
            } else {
                t = defaultObject;
            }
            t.fromJson(json);
            t.save(mDbHelper);
            return t;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getTransactions() {
        new JsonRequest(mDbHelper.getContext())
                .get("api/v1/transaction/" + "?userId=" + mUser.userId + "&users=1&data=1", new JsonRequestListener() {
                    @Override
                    public void onRequestComplete(JsonRequest request) {
                        JSONObject data = request.getSuccessDataObject();
                        if (data != null) {
                            JSONArray users = data.optJSONArray("users");
                            for (int i=0; i<users.length(); i++) {

                                saveOrUpdate(User.class,
                                        users.optJSONObject(i),
                                        "userId", new User());

                            }

                            JSONArray transactions = data.optJSONArray("transactions");
                            for (int i=0; i<transactions.length(); i++) {

                                JSONObject transactionJson = transactions.optJSONObject(i);
                                PaisoTransaction transaction = saveOrUpdate(PaisoTransaction.class,
                                        transactionJson,
                                        "transactionId", new PaisoTransaction());

                                if (transaction != null) {

                                    JSONArray datas = transactionJson.optJSONArray("data");
                                    for (int j=0; j<datas.length(); j++) {

                                        saveOrUpdate(TransactionData.class,
                                                datas.optJSONObject(j),
                                                "dataId", new TransactionData());

                                    }

                                }

                            }
                        }
                    }
                });
    }
}
