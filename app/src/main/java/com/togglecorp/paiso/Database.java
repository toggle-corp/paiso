package com.togglecorp.paiso;

public class Database {

    public String selfId;
    public User self;

    private Database() {}
    private static Database mDatabase;

    public static Database get() {
        if (mDatabase == null)
            mDatabase = new Database();
        return mDatabase;
    }


}
