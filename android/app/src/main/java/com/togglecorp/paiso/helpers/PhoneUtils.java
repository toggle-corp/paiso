package com.togglecorp.paiso.helpers;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PhoneUtils {
    public static String getNormalizedPhone(Context context, String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getNetworkCountryIso().toUpperCase();

        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            phoneNumber = phoneUtil.format(phoneUtil.parse(phoneNumber, countryIso), PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            phoneNumber = null;
            e.printStackTrace();
        }
        return phoneNumber;
    }
}
