package com.togglecorp.paiso;

public class DashboardTransaction {
    String name;
    String extra;
    double amount;

    DashboardTransaction(String name, String extra, double amount){
        this.name = name;
        this.extra = extra;
        this.amount = amount;
    }
}
