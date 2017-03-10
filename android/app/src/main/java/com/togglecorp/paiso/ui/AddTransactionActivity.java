package com.togglecorp.paiso.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.network.SyncManager;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTransactionActivity extends AppCompatActivity implements TextWatcher {

    private Contact mContact;
    private PaisoTransaction mTransaction;
    private TransactionData mTransactionData;
    private DbHelper mDbHelper;

    private String mUserName;
    private String mContactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Get contact contactId to display details for
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        long id = bundle.getLong("contactId", -1);
        if (id < 0) {
            finish();
            return;
        }

        mDbHelper = new DbHelper(this);

        mContact = Contact.get(Contact.class, mDbHelper, id);
        if (mContact == null || mContact.contactId == null) {
            finish();
            return;
        }

        long tid = bundle.getLong("transactionId", -1);
        if (tid >= 0) {
            mTransaction = PaisoTransaction.get(PaisoTransaction.class, mDbHelper, tid);
            mTransactionData = mTransaction.getLatest(mDbHelper);
        }

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if (mTransaction == null) {
            setTitle("Add transaction");
        } else {
            setTitle("Edit transaction");
        }

        // Initialization

        mUserName = "You"; // SyncManager.getUser().displayName;
        mContactName = mContact.displayName;

        ((TextView) findViewById(R.id.user_name)).setText(mUserName);
        Picasso.with(this)
                .load(SyncManager.getUser().photoUrl)
                .into((CircleImageView)findViewById(R.id.user_avatar));

        ((TextView) findViewById(R.id.contact_name)).setText(mContactName);

        if (mContact.photoUrl == null) {
            ((CircleImageView)findViewById(R.id.contact_avatar)).setImageResource(R.drawable.ic_avatar);
        } else {
            Picasso.with(this)
                    .load(mContact.photoUrl)
                    .into((CircleImageView)findViewById(R.id.contact_avatar));
        }

        ((EditText) findViewById(R.id.amount)).addTextChangedListener(this);
        ((EditText) findViewById(R.id.title)).addTextChangedListener(this);

        // Load old data

        if (mTransaction != null) {
            findViewById(R.id.arrow).setScaleX(mTransaction.transactionType.equals("to") ? -2 : 2);

            if (mTransactionData != null) {
                ((EditText) findViewById(R.id.amount)).setText(mTransactionData.amount+"");
                ((EditText) findViewById(R.id.title)).setText(mTransactionData.title);
            }
        }

        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_transaction, menu);
        menu.findItem(R.id.delete_transaction).setVisible(mTransaction != null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.delete_transaction:
                deleteTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeDirection(View view) {
        View arrow = findViewById(R.id.arrow);
        arrow.setScaleX(arrow.getScaleX() * -1);
        refresh();
    }

    public void refresh() {
        String title = ((EditText) findViewById(R.id.title)).getText().toString();
        String amount = ((EditText) findViewById(R.id.amount)).getText().toString();

        // Summary
        View arrow = findViewById(R.id.arrow);
        String summary = (arrow.getScaleX() > 0 ? mContactName : mUserName)
                + " gave " + (arrow.getScaleX() > 0 ? mUserName : mContactName);

        if (amount.length() > 0) {
            summary += " " + amount;
        }

        ((TextView)findViewById(R.id.summary)).setText(summary);

        // Done button
        findViewById(R.id.add_transaction).setVisibility(
                (amount.length() > 0 && title.length() > 0) ? View.VISIBLE : View.INVISIBLE);
    }

    public void addTransaction(View view) {
        String title = ((EditText) findViewById(R.id.title)).getText().toString();
        String amount_str = ((EditText) findViewById(R.id.amount)).getText().toString();

        if (title.length() == 0 || amount_str.length() == 0) {
            return;
        }

        float amount = Float.parseFloat(amount_str);
        String type = (findViewById(R.id.arrow).getScaleX() < 0) ? "to" : "by";

        PaisoTransaction transaction = (mTransaction == null) ? new PaisoTransaction() : mTransaction;
        transaction.user = SyncManager.getUser().userId;
        transaction.contact = mContact.contactId;
        transaction.transactionType = type;
        transaction.modified = true;
        transaction.save(mDbHelper);

        TransactionData data = new TransactionData();
        data.localTransaction = (int)transaction._id;
        data.title = title;
        data.amount = amount;
        data.approved = false;
        data.timestamp = new Date().getTime();
        data.modified = true;
        data.save(mDbHelper);

        finish();
    }

    public void deleteTransaction() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this transaction?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mTransaction != null) {
                            mTransaction.deleted = true;
                            mTransaction.modified = true;
                            mTransaction.save(mDbHelper);
                        }
                        AddTransactionActivity.this.finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        refresh();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
