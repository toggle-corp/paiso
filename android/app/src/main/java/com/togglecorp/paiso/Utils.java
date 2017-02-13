package com.togglecorp.paiso;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.util.Locale;

public class Utils {
    public static String formatCurrency(double amount) {
        if (amount % 1.0 != 0)
            return String.format(Locale.getDefault(), "%.2f", amount);
        else
            return String.format(Locale.getDefault(), "%.0f", amount);
    }

    public static String formatDate(Context context, Long date) {
        return DateUtils.getRelativeDateTimeString(
                context,
                date,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,
                0
        ).toString();
    }

    public static void setBalance(Context context, TextView balanceView, double total) {
        balanceView.setText(Utils.formatCurrency(total));
        if (total < 0)
            balanceView.setTextColor(ContextCompat.getColor(context,R.color.colorNegativeBalance));
        else
            balanceView.setTextColor(ContextCompat.getColor(context,R.color.colorPositiveBalance));
    }
}
