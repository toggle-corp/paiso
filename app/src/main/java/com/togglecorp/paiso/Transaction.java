package com.togglecorp.paiso;

public class Transaction {
    public String by;
    public String to;

    public Boolean customUser;

    public Long date;
    public Double amount;

    // Status
    public Long added_by;
    public Long accepted_by;

    Transaction() {
        customUser = false;
        added_by = null;
        accepted_by = null;
    }
}
