package com.togglecorp.paiso;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Locale;

public class Utils {
    public static String formatCurrency(double amount) {
        if (amount % 1.0 != 0)
            return String.format(Locale.getDefault(), "%.2f", amount);
        else
            return String.format(Locale.getDefault(), "%.0f", amount);
    }

    public static String formatDate(Context mContext, Long date)
    {
        return DateUtils.getRelativeDateTimeString(
                mContext,
                date,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,
                0
        ).toString();
    }
}
