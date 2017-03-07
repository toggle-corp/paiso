package com.togglecorp.paiso.db;

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

    public List<TransactionData> getData(DbHelper dbHelper) {
        return TransactionData.query(TransactionData.class, dbHelper, "paisoTransaction = ?",
                new String[]{transactionId+""});
    }

    // Pass in dbHelper transactionTo also get taransaction data in json
    public JSONObject toJson(DbHelper dbHelper) {
        JSONObject object = new JSONObject();

        try {
            object.put("transactionId", transactionId==null?JSONObject.NULL:transactionId);
            object.put("contact", contact);
            object.put("transactionType", transactionType);

            if (dbHelper != null) {
                JSONArray dataArray = new JSONArray();

                List<TransactionData> dataList = getData(dbHelper);
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
        contact = (Integer)json.opt("contact");
        transactionType = json.optString("transactionType");
    }
}
