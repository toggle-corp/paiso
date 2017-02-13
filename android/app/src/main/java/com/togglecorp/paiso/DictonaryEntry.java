package com.togglecorp.paiso;

import android.provider.BaseColumns;

public class DictonaryEntry {

    public String key;
    public String value;

    public final static String TABLE_NAME = "dictionary";
    public final static String COLUMN_NAME_KEY = "key";
    public final static String COLUMN_NAME_VALUE = "value";
}
