package com.togglecorp.paiso.db;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaisoTransaction extends SerializableRemoteModel {
    public Integer transactionId = null;
    public Integer user;
    public Integer contact;
    public String transactionType;

    public PaisoTransaction() {}

    public PaisoTransaction(Integer transactionId, Integer user, Integer contact, String transactionType) {
        this.transactionId = transactionId;
        this.user = user;
        this.contact = contact;
        this.transactionType = transactionType;
    }

    public List<TransactionData> getAllData(DbHelper dbHelper) {
        return TransactionData.query(TransactionData.class, dbHelper, "paisoTransaction = ?",
                new String[]{transactionId+""}, null, null, "-timestamp");
    }

    public List<TransactionData> getApprovedData(DbHelper dbHelper) {
        return TransactionData.query(TransactionData.class, dbHelper, "paisoTransaction = ? AND approved = 1",
                new String[]{transactionId+""}, null, null, "-timestamp");
    }

    public List<TransactionData> getPendingData(DbHelper dbHelper) {
        return TransactionData.query(TransactionData.class, dbHelper, "paisoTransaction = ? AND approved = 0",
                new String[]{transactionId+""}, null, null, "-timestamp");
    }

    public TransactionData getLatestApproved(DbHelper dbHelper) {
        List<TransactionData> data = getApprovedData(dbHelper);
        if (data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    public Contact getContact(DbHelper dbHelper) {
        return Contact.get(Contact.class, dbHelper, "contactId=?", new String[]{contact+""});
    }

    // Pass in dbHelper transactionTo also get taransaction data in json
    public JSONObject toJson(DbHelper dbHelper) {
        JSONObject object = new JSONObject();

        try {
            object.put("transactionId", transactionId==null?JSONObject.NULL:transactionId);
            object.put("contactId", contact);
            object.put("transactionType", transactionType);

            if (dbHelper != null) {
                JSONArray dataArray = new JSONArray();

                List<TransactionData> dataList = getAllData(dbHelper);
                for (TransactionData data: dataList) {
                    dataArray.put(data.toJson());
                }

                object.put("data", dataArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public  JSONObject toJson() {
        return toJson(null);
    }


    @Override
    public void fromJson(JSONObject json) {
        if (json == null) {
            return;
        }

        transactionId = (Integer)json.opt("transactionId");
        contact = (Integer)json.opt("contactId");
        transactionType = json.optString("transactionType");
    }
}
