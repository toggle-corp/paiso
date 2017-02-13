package com.togglecorp.paiso;

public class DashboardTransaction {
    String userId;
    boolean customUser;

    String name;
    String extra;
    double amount;

    DashboardTransaction(String userId, boolean customUser,
                         String name, String extra, double amount)
    {
        this.userId = userId;
        this.customUser = customUser;
        this.name = name;
        this.extra = extra;
        this.amount = amount;
    }
}
