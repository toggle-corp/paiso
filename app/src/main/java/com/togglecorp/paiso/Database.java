package com.togglecorp.paiso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


    public HashMap<String, User> mUsers = new HashMap<>();
    public HashMap<String, String> mCustomUsers = new HashMap<>();
    public HashMap<String, Transaction> mTransactions;

    // Make sure every activity/fragment removes listener at or before onStop
    public List<RefreshListener> mRefreshListeners = new ArrayList<>();

    public void refresh() {
        for (RefreshListener listener: mRefreshListeners)
            if (listener != null)
                listener.refresh();
    }

}
