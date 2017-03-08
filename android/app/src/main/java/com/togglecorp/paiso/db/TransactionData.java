package com.togglecorp.paiso.db;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionData extends SerializableRemoteModel {
    public Integer dataId = null;
    public Integer paisoTransaction;

    public String title;
    public float amount;
    public boolean approved;
    public long timestamp;


    public TransactionData() {}

    public TransactionData(Integer dataId, String title, float amount, boolean approved, Integer transaction) {
        this.dataId = dataId;
        this.title = title;
        this.amount = amount;
        this.approved = approved;
        this.paisoTransaction = transaction;
    }


    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        try {
            object.put("dataId", dataId==null?JSONObject.NULL:dataId);
            object.put("transactionId", paisoTransaction);
            object.put("title", title);
            object.put("amount", amount);
            object.put("approved", approved);
            object.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public void fromJson(JSONObject json) {
        if (json == null) {
            return;
        }

        dataId = (Integer)json.opt("dataId");
        paisoTransaction = (Integer)json.opt("transactionId");
        title = json.optString("title");
        amount = (float)json.optDouble("amount");
        approved = json.optBoolean("approved");
        timestamp = json.optLong("timestamp");
    }
}
