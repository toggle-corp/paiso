package com.togglecorp.paiso.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.helpers.PermissionListener;
import com.togglecorp.paiso.helpers.PermissionsManager;
import com.togglecorp.paiso.helpers.PhoneUtils;

public class PhoneVerificationActivity extends AppCompatActivity {
    private final static String TAG = "PhoneVerifyActivity";
    public static final int REQUEST_CODE = 9841;

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
        setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        PermissionsManager.check(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        readPhoneNumber();
                    }
                });


        /*mPage1 = findViewById(R.contactId.page1);

        mCountryCode = (Spinner) findViewById(R.contactId.country_code);
        mPhoneNumber = (EditText) findViewById(R.contactId.phone_number);
        mCode = (EditText) findViewById(R.contactId.code);
        mBackButton = (Button) findViewById(R.contactId.back);
        mNextButton = (Button) findViewById(R.contactId.next);
        mNextButton.setEnabled(false);

        mPhoneNumber.addTextChangedListener(this);

        mCountryCode.setAdapter(new CountryCodeAdapter(this));
        mPage2 = findViewById(R.contactId.page2);

        showPage1();*/
    }

    private void readPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String phoneNumber = PhoneUtils.getNormalizedPhone(this, telephonyManager.getLine1Number());
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        ((TextView)findViewById(R.id.phone_number)).setText(phoneNumber);
    }

    public void verify(View view) {
        Intent data = new Intent();
        data.putExtra("phone", ((TextView)findViewById(R.id.phone_number)).getText());
        setResult(Activity.RESULT_OK, data);
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
