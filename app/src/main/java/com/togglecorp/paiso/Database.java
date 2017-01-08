package com.togglecorp.paiso;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public HashMap<String, Transaction> transactions = new HashMap<>();

    // Make sure every activity/fragment removes listener at or before onStop
    public List<RefreshListener> refreshListeners = new ArrayList<>();

    private void refresh() {
        for (RefreshListener listener: refreshListeners)
            if (listener != null)
                listener.refresh();

        // TODO: Do this only once in a while
        // Synchronize all transaction added by self to other users if they exist
        for (Map.Entry<String, Transaction> transactionEntry: transactions.entrySet()) {
            String key = transactionEntry.getKey();
            Transaction transaction = transactionEntry.getValue();

            if (transaction.added_by.equals(selfId)) {

                // Is this transaction for custom user despite actual user already existing
                String otherId = transaction.getOther(selfId);
                if (transaction.customUser && contacts.get(otherId) != null) {
                    if (contacts.get(otherId).userId != null) {
                        transaction.customUser = false;
                        transaction.setOther(selfId, contacts.get(otherId).userId);
                        editTransaction(key, transaction);
                    }
                }

                // Make sure this transaction is synchronized for other user
                if (!transaction.customUser) {
                    mRef.child("user_transactions").child(transaction.getOther(selfId))
                            .child(key).setValue("true");
                }
            }
        }
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

    public void addTransaction(Transaction transaction) {
        DatabaseReference newTransaction = mRef.child("transactions").push();
        newTransaction.setValue(transaction);

        mRef.child("user_transactions").child(selfId)
                .child(newTransaction.getKey()).setValue("true");

        if (!transaction.customUser) {
            mRef.child("user_transactions").child(transaction.getOther(selfId))
                    .child(newTransaction.getKey()).setValue("true");
        }
    }

    public void editTransaction(String key, Transaction transaction) {
        mRef.child("transactions").child(key).setValue(transaction);
    }

    public void deleteTransaction(String key) {
        Transaction transaction = transactions.get(key);
        transaction.deleted = true;
        editTransaction(key, transaction);

        mRef.child("user_transactions").child(selfId)
                .child(key).removeValue();

        if (!transaction.customUser) {
            mRef.child("user_transactions").child(transaction.getOther(selfId))
                    .child(key).removeValue();
        }

        mRef.child("transactions").child(key).removeValue();
    }


    // Firebase data listener
    private Map<Query, ValueEventListener> mListeners = new HashMap<>();
    private void addListener(Query ref, ValueEventListener listener) {
        if (mListeners.containsKey(ref))
            return;

//        Log.d(TAG, "Adding listener for: " + ref.toString());

        ref.addValueEventListener(listener);
        mListeners.put(ref, listener);
    }

    public void startSync(final Context context) {
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
                try {
                    // listener for each transaction
                    for (DataSnapshot t : dataSnapshot.getChildren())
                        listenForTransaction(t.getKey());

                    refresh();
                }
                catch (DatabaseException ignored) {}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mRef.child("user_transactions").child(selfId), transactionsListener);

        // Get all contacts who are also in Paiso
        // Reading transactions get hanged up due to this, so delay this to other thread
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fetchContactUsers(context);
                return null;
            }
        }.execute();
    }

    private void listenForTransaction(String transactionId) {
        if (transactionId == null)
            return;

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
        if (userId == null)
            return;

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
                ContactsContract.Data.CONTENT_URI,
                new String[] {
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Email.DATA,
                        ContactsContract.Contacts.PHOTO_URI
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
                            cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

                    // Store the contact
                    storeContact(id, new Contact(displayName, email, photoUrl));
                }
            }
            finally {
                cursor.close();

            }
        }
    }

    private void storeContact(final String id, final Contact contact) {
        // First store the contact
        contacts.put(id, contact);

        if (contact.email == null)
            return;

        // Also see if user exists with email of this contact
        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                if (dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot user = dataSnapshot.getChildren().iterator().next();

//                    Log.d(TAG, "Got email for: " + contact.email);

                    // Set userId of the contact
                    contact.userId = user.getKey();

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
//            Log.d(TAG, "Removing listener for: " + listener.getKey());
            listener.getKey().removeEventListener(listener.getValue());
        }
        mListeners.clear();

    }

}
