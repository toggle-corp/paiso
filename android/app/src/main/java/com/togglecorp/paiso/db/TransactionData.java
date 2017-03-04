package com.togglecorp.paiso.db;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionData extends SerializableModel {
    public Long dataId = null;
    public Long transaction;

    public String title;
    public Float amount;
    public boolean approved;
    public long timestamp;


    public TransactionData() {}

    public TransactionData(Long dataId, String title, float amount, boolean approved, Long transaction) {
        this.dataId = dataId;
        this.title = title;
        this.amount = amount;
        this.approved = approved;
        this.transaction = transaction;
    }


    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        try {
            object.put("dataId", dataId);
            object.put("transaction", transaction);
            object.put("title", title);
            object.put("amount", amount);
            object.put("approved", approved);
            object.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}
