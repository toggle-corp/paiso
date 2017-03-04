package com.togglecorp.paiso.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Transaction extends SerializableModel {
    public Long transactionId = null;
    public String to = null;
    public String by = null;
    public Long unregisteredContact;
    public String addedBy;

    public Transaction() {}

    public Transaction(Long transactionId, String by, String to, Long unregisteredContact, String addedBy) {
        this.transactionId = transactionId;
        this.by = by;
        this.to = to;
        this.addedBy = addedBy;
        this.unregisteredContact = unregisteredContact;
    }

    public List<TransactionData> getData(DbHelper dbHelper) {
        return TransactionData.query(TransactionData.class, dbHelper, "transaction = ?",
                new String[]{transactionId+""});
    }

    // Pass in dbHelper to also get taransaction data in json
    public JSONObject toJson(DbHelper dbHelper) {
        JSONObject object = new JSONObject();

        try {
            object.put("transactionId", transactionId);
            object.put("to", to);
            object.put("by", by);
            object.put("unregisteredContact", unregisteredContact);
//            object.put("addedBy", addedBy);
//            Added by may not be needed while posting

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

}
