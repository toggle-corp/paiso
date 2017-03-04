package com.togglecorp.paiso.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DB_NAME = "Paiso.db";
    private static final int DB_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(new User().getCreateTableSql());
        db.execSQL(new Contact().getCreateTableSql());
        db.execSQL(new Transaction().getCreateTableSql());
        db.execSQL(new TransactionData().getCreateTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetAll(db);
    }

    private void resetAll(SQLiteDatabase db) {
        db.execSQL(new User().getDestroyTableSql());
        db.execSQL(new Contact().getDestroyTableSql());
        db.execSQL(new Transaction().getDestroyTableSql());
        db.execSQL(new TransactionData().getDestroyTableSql());

        onCreate(db);
    }

}
