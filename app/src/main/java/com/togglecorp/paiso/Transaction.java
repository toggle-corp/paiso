package com.togglecorp.paiso;

import com.google.firebase.database.ServerValue;

public class Transaction {
    public String title;

    // These are id of either actual user (customUser == false) or contact id (customUser == true)
    public String by;
    public String to;

    public Boolean customUser;

    public Object date;
    public Double amount;

    // Status
    public String added_by;
    public String accepted_by;
    public Boolean deleted;

    Transaction() {
        customUser = false;
        added_by = null;
        accepted_by = null;
        date = ServerValue.TIMESTAMP;
    }

    public String getOther(String selfId) {
        if (selfId.equals(by))
            return to;
        else
            return by;
    }

    public void setOther(String selfId, String newId) {
        if (selfId.equals(by)) {
            to = newId;
        }
        else {
            by = newId;
        }
    }

    public Double getSignedAmount(String selfId) {
        if (selfId.equals(by))
            return amount;
        else
            return -amount;
    }
}
