package com.togglecorp.paiso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDbHelper extends SQLiteOpenHelper {
    // Database name and version
    public static final String DATABASE_NAME = "PaisoLocalDatabase.db";
    public static final int DATABASE_VERSION = 1;

    // Sql create and delete queries
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DictonaryEntry.TABLE_NAME + " (" +
                    DictonaryEntry.COLUMN_NAME_KEY + " TEXT PRIMARY_KEY," +
                    DictonaryEntry.COLUMN_NAME_VALUE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DictonaryEntry.TABLE_NAME;

    public LocalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void save(DictonaryEntry dictionaryEntry) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DictonaryEntry.COLUMN_NAME_KEY, dictionaryEntry.key);
        values.put(DictonaryEntry.COLUMN_NAME_VALUE, dictionaryEntry.value);
        db.insertWithOnConflict(DictonaryEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public void delete(String key) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = DictonaryEntry.COLUMN_NAME_KEY + " LIKE ?";
        String[] selectionArgs = { key };
        db.delete(DictonaryEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    public DictonaryEntry get(String key) {
        SQLiteDatabase db = getReadableDatabase();
        DictonaryEntry dictonaryEntry = null;

        String selection = DictonaryEntry.COLUMN_NAME_KEY + " LIKE ?";
        String[] selectionArgs = { key };
        String[] projection = {
                DictonaryEntry.COLUMN_NAME_KEY,
                DictonaryEntry.COLUMN_NAME_VALUE
        };

        Cursor cursor = db.query(DictonaryEntry.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            dictonaryEntry = new DictonaryEntry();
            dictonaryEntry.key = cursor.getString(cursor.getColumnIndex(DictonaryEntry.COLUMN_NAME_KEY));
            dictonaryEntry.value = cursor.getString(cursor.getColumnIndex(DictonaryEntry.COLUMN_NAME_VALUE));
        }
        cursor.close();

        db.close();
        return dictonaryEntry;
    }
}
