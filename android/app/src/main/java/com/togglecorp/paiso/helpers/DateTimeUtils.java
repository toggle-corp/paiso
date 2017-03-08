package com.togglecorp.paiso.helpers;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;

public class DateTimeUtils {

    public static String getFormattedDate(Context context, long timestamp) {
        return android.text.format.DateUtils.getRelativeDateTimeString(
                context,
                timestamp,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,
                0
        ).toString();
    }
}
