package com.togglecorp.paiso;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {
    private List<String> mContactIds =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction);

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
        List<String> contactList = new ArrayList<>();
        for (HashMap.Entry<String, Contact> contact: Database.get().contacts.entrySet()) {
            mContactIds.add(contact.getKey());
            contactList.add(contact.getValue().displayName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, contactList);
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_transaction, menu);
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
            case R.id.add_transaction:
                addTransaction();
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
        if (((RadioButton)findViewById(R.id.radio_by)).isChecked()) {
            transaction.to = Database.get().selfId;
            transaction.by = Database.get().contacts.get(mContactIds.get(user)).userId;
        } else {
            transaction.by = Database.get().selfId;
            transaction.to = Database.get().contacts.get(mContactIds.get(user)).userId;
        }
        transaction.added_by = Database.get().selfId;

        Database.get().addTransaction(transaction);

        finish();
    }
}
