package com.togglecorp.paiso;

import com.google.firebase.database.ServerValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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

    public Transaction() {
        customUser = false;
        added_by = null;
        accepted_by = null;
        date = ServerValue.TIMESTAMP;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("by", by);
            jsonObject.put("to", to);
            jsonObject.put("custom_user", customUser);
            jsonObject.put("date", date);
            jsonObject.put("amount", amount);
            jsonObject.put("added_by", added_by);
            jsonObject.put("accepted_by", accepted_by);
            jsonObject.put("deleted", deleted);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Transaction fromJson(JSONObject jsonObject) {
        Transaction transaction = new Transaction();
        try {
            transaction.title = jsonObject.getString("title");
            transaction.by = jsonObject.getString("by");
            transaction.to = jsonObject.getString("to");
            transaction.date = jsonObject.get("date");
            transaction.amount = jsonObject.getDouble("amount");
            transaction.added_by = jsonObject.getString("added_by");
            transaction.accepted_by = jsonObject.getString("accepted_by");
            transaction.deleted = jsonObject.getBoolean("deleted");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaction;
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

    public String getOtherContactId(String selfId) {
        String otherId = getOther(selfId);
        if (customUser)
            return otherId;
        else {
            for (Map.Entry<String, Contact> contact: Database.get().contacts.entrySet()) {
                if (contact.getValue().userId != null && contact.getValue().userId.equals(otherId))
                    return contact.getKey();
            }
        }
        return null;
    }
}
