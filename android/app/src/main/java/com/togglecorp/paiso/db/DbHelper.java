package com.togglecorp.paiso.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Paiso.db";
    private static final int DB_VERSION = 1;

    private Context mContext;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(new User().getCreateTableSql());
        db.execSQL(new Contact().getCreateTableSql());
        db.execSQL(new PaisoTransaction().getCreateTableSql());
        db.execSQL(new TransactionData().getCreateTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetAll(db);
    }

    public void resetAll(SQLiteDatabase db) {
        db.execSQL(new User().getDestroyTableSql());
        db.execSQL(new Contact().getDestroyTableSql());
        db.execSQL(new PaisoTransaction().getDestroyTableSql());
        db.execSQL(new TransactionData().getDestroyTableSql());

        onCreate(db);
    }

    public Context getContext() {
        return mContext;
    }


}
