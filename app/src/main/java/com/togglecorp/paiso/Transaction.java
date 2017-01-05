package com.togglecorp.paiso;

import com.google.firebase.database.ServerValue;

public class Transaction {
    public String title;

    public String by;
    public String to;

    public Boolean customUser;

    public Object date;
    public Double amount;

    // Status
    public String added_by;
    public String accepted_by;

    Transaction() {
        customUser = false;
        added_by = null;
        accepted_by = null;
        date = ServerValue.TIMESTAMP;
    }
}
