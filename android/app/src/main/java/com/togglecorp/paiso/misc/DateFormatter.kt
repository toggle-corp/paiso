package com.togglecorp.paiso.misc

import android.content.Context
import android.text.format.DateUtils
import java.util.*

object DateFormatter {
    fun getReadableTime(context: Context, date: Date) : CharSequence {
        return DateUtils.getRelativeDateTimeString(
                context, date.time, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_SHOW_TIME.or(DateUtils.FORMAT_SHOW_DATE) )
    }
}