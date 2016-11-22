package com.togglecorp.paiso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private static FirebaseDatabase mFbDb;
    public static DatabaseReference getDatabase() { return mFbDb.getReference(); }

    private AuthUser mAuthUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize firebase database
        if (mFbDb == null) {
            mFbDb = FirebaseDatabase.getInstance();
            mFbDb.setPersistenceEnabled(true);
        }
        mDatabase = getDatabase();

        // Get logged in user or start Login Activity
        mAuthUser = new AuthUser(this);
        if (mAuthUser.getFbUser() == null) {
            // Not signed in, launch the Login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set active user id to the database
        Database.get().selfId = mAuthUser.getFbUser().getUid();
        Database.get().self = mAuthUser.getUser();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new DashboardFragment()).commit();

    }



    @Override
    public void onStart() {
        super.onStart();
        startSync();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopSync();
    }

    private Map<String, ValueEventListener> mListeners = new HashMap<>();
    private void addListener(Query ref, ValueEventListener listener) {
        if (mListeners.containsKey(ref.toString()))
            return;

        Log.d(TAG, "Adding listener for: " + ref.toString());

        ref.addValueEventListener(listener);
        mListeners.put(ref.toString(), listener);
    }

    public void startSync() {
        // First store self information
        DatabaseReference self = mDatabase.child("users").child(mAuthUser.getFbUser().getUid());
        self.child("displayName").setValue(mAuthUser.getUser().displayName);
        self.child("email").setValue(mAuthUser.getUser().email);
        self.child("photoUrl").setValue(mAuthUser.getUser().photoUrl);

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

                Database.get().refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mDatabase.child("user_transactions").child(mAuthUser.getFbUser().getUid()),
                transactionsListener);

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

                    Database.get().mCustomUsers.put(user.getKey(), user.getValue(String.class));
                }

                Database.get().refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void listenForTransaction(String transactionId) {
        ValueEventListener transactionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                // Store the transaction
                Transaction transaction = dataSnapshot.getValue(Transaction.class);
                Database.get().mTransactions.put(dataSnapshot.getKey(), transaction);

                // Get the users if they are not custom added users
                if (!transaction.customUser) {
                    String selfId = mAuthUser.getFbUser().getUid();

                    if (!transaction.to.equals(selfId))
                        listenForUser(transaction.to);
                    if (!transaction.by.equals(selfId))
                        listenForUser(transaction.by);
                }

                Database.get().refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mDatabase.child("transactions").child(transactionId), transactionListener);
    }

    public void listenForUser(String userId) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    return;

                // Store the user
                User user = dataSnapshot.getValue(User.class);

                Database.get().refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        addListener(mDatabase.child("users").child(userId), userListener);
    }

    public void stopSync() {
        for (Map.Entry<String, ValueEventListener> listener: mListeners.entrySet()) {
            Log.d(TAG, "Removing listener for: " + listener.getKey());
            mDatabase.getDatabase().getReferenceFromUrl(listener.getKey())
                    .removeEventListener(listener.getValue());
        }
        mListeners.clear();

    }
}
