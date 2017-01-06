package com.togglecorp.paiso;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {
    private List<String> mUserIds =  new ArrayList<>();
    private String mTransactionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTransactionId = bundle.getString("transaction-id", null);
        }

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setTitle("Add transaction");

        // Populate contact spinner
        Spinner spinner = (Spinner)findViewById(R.id.spinner_user);
        List<String> userList = new ArrayList<>();
        for (HashMap.Entry<String, User> user: Database.get().users.entrySet()) {
            mUserIds.add(user.getKey());
            userList.add(user.getValue().displayName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, userList);
        spinner.setAdapter(adapter);

        // FAB button to add transaction
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add_transaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
            }
        });

        // For editing, fill old values
        if (mTransactionId != null) {
            Transaction oldTransaction = Database.get().transactions.get(mTransactionId);

            ((EditText)findViewById(R.id.input_title)).setText(oldTransaction.title);
            ((EditText)findViewById(R.id.input_title)).setSelection(oldTransaction.title.length());
            ((EditText)findViewById(R.id.input_amount)).setText(oldTransaction.amount+"");
            ((Spinner)findViewById(R.id.spinner_user)).setSelection(
                    mUserIds.indexOf(oldTransaction.getOther(Database.get().selfId))
            );
            ((ToggleButton)findViewById(R.id.toggle_whom)).setChecked(
                    oldTransaction.to.equals(Database.get().selfId)
            );
        }

        // Show fab
        fab.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_transaction, menu);
        menu.findItem(R.id.delete_transaction).setVisible(mTransactionId != null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                onBackPressed();
                return true;
            // Add transaction
            case R.id.delete_transaction:
                deleteTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addTransaction() {
        String title = ((EditText)findViewById(R.id.input_title)).getText().toString();
        String amount = ((EditText)findViewById(R.id.input_amount)).getText().toString();
        int user = ((Spinner)findViewById(R.id.spinner_user)).getSelectedItemPosition();

        if (user < 0 || title.equals("") || amount.equals("")) {
            return;
        }

        Transaction transaction = new Transaction();
        transaction.title = title;
        transaction.amount = Double.parseDouble(amount);

        if (((ToggleButton)findViewById(R.id.toggle_whom)).isChecked()) {
            transaction.to = Database.get().selfId;
            transaction.by = mUserIds.get(user);
        } else {
            transaction.by = Database.get().selfId;
            transaction.to = mUserIds.get(user);
        }
        transaction.added_by = Database.get().selfId;

        if (mTransactionId == null)
            Database.get().addTransaction(transaction);
        else {
            // Keep the creation time
            transaction.date = Database.get().transactions.get(mTransactionId).date;
            Database.get().editTransaction(mTransactionId, transaction);
        }

        finish();
    }

    private void deleteTransaction() {
        if (mTransactionId != null) {
            Database.get().deleteTransaction(mTransactionId);
        }
        finish();
    }
}
