package com.togglecorp.paiso.ui;

import android.Manifest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.togglecorp.paiso.R;
import com.togglecorp.paiso.adapters.CountryCodeAdapter;
import com.togglecorp.paiso.helpers.PermissionListener;
import com.togglecorp.paiso.helpers.PermissionsManager;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PhoneVerificationActivity extends AppCompatActivity {
    private final static String TAG = "PhoneVerificationActivity";

    /*private Spinner mCountryCode;
    private EditText mPhoneNumber;
    private EditText mCode;
    private Button mBackButton;
    private Button mNextButton;

    private View mPage1;
    private View mPage2;

    private String mGeneratedCode;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        PermissionsManager.check(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        readPhoneNumber();
                    }
                });


        /*mPage1 = findViewById(R.id.page1);

        mCountryCode = (Spinner) findViewById(R.id.country_code);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mCode = (EditText) findViewById(R.id.code);
        mBackButton = (Button) findViewById(R.id.back);
        mNextButton = (Button) findViewById(R.id.next);
        mNextButton.setEnabled(false);

        mPhoneNumber.addTextChangedListener(this);

        mCountryCode.setAdapter(new CountryCodeAdapter(this));
        mPage2 = findViewById(R.id.page2);

        showPage1();*/
    }

    private void readPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();

        phoneNumber = PhoneNumberUtils.stripSeparators("+977" + phoneNumber);

        ((TextView)findViewById(R.id.phone_number)).setText(phoneNumber);
    }

    public void verify(View view) {
        finish();
    }

    public void skip(View view) {
        finish();
    }

    /*private void showPage1() {
        mNextButton.setText("Send code");
        mBackButton.setVisibility(View.INVISIBLE);

        mPage1.setVisibility(View.VISIBLE);
        mPage2.setVisibility(View.INVISIBLE);
    }

    private void showPage2() {
        mNextButton.setText("Verify");
        mBackButton.setVisibility(View.VISIBLE);

        mPage1.setVisibility(View.INVISIBLE);
        mPage2.setVisibility(View.VISIBLE);

        mCode.setText(mGeneratedCode);
    }

    private void sendCode() {
        PermissionsManager.check(this, new String[]{
                Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS
        }, new PermissionListener() {
            @Override
            public void onGranted() {
//                SmsManager.getDefault().sendTextMessage();
            }
        });
    }

    public void onBack(View view) {
        showPage1();
    }

    public void onNext(View view) {
        if (mPage1.getVisibility() == View.VISIBLE) {
            mGeneratedCode = new BigInteger(130, new SecureRandom()).toString(32).substring(0, 8);
            sendCode();
        } else {

        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        PermissionsManager.handleResult(requestCode, permissions, grantResults);

    }

    /*@Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mPhoneNumber.getText().length() > 0) {
            mNextButton.setEnabled(true);
        } else {
            mNextButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }*/
}
