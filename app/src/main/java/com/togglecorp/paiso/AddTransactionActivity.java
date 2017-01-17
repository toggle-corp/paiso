package com.togglecorp.paiso;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AddTransactionActivity extends AppCompatActivity {

    private String mTransactionId = null;
    private String mContactId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction);

        // Get data passed to this activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTransactionId = bundle.getString("transaction-id", null);
            mContactId = bundle.getString("contact-id", null);
        }
        if (mContactId == null) {
            finish();
            return;
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

        // FAB button to add transaction
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add_transaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
            }
        });

        // Name of contact
        ((TextView)findViewById(R.id.username)).setText(
                Database.get().contacts.get(mContactId).displayName);

        // For editing, fill old values
        if (mTransactionId != null) {
            Transaction oldTransaction = Database.get().transactions.get(mTransactionId);

            ((EditText)findViewById(R.id.input_title)).setText(oldTransaction.title);
            ((EditText)findViewById(R.id.input_title)).setSelection(oldTransaction.title.length());
            ((EditText)findViewById(R.id.input_amount)).setText(oldTransaction.amount+"");
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

        Transaction transaction = new Transaction();
        transaction.title = title;
        transaction.amount = Double.parseDouble(amount);

        // Check if user with this contact exists in paiso
        if (Database.get().contacts.get(mContactId).userId != null) {
            mContactId = Database.get().contacts.get(mContactId).userId;
            transaction.customUser = false;
        }
        else {
            transaction.customUser = true;
        }

        if (((ToggleButton)findViewById(R.id.toggle_whom)).isChecked()) {
            transaction.to = Database.get().selfId;
            transaction.by = mContactId;
        } else {
            transaction.by = Database.get().selfId;
            transaction.to = mContactId;
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
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this transaction?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mTransactionId != null) {
                            Database.get().deleteTransaction(mTransactionId);
                        }
                        AddTransactionActivity.this.finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
