package com.togglecorp.paiso.helpers;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {
    public static int getThemeColor(Context context, int attribute) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attribute, value, true);
        return value.data;
    }
}
