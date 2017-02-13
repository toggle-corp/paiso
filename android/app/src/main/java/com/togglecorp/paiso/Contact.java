package com.togglecorp.paiso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contact {
    public String userId = null;
    public String displayName = null;
    public String data;
    public String photoUrl = null;

    public Contact() {}
    
    public Contact(String displayName, String data, String photoUrl) {
        this.displayName = displayName;
        this.data = data;
        this.photoUrl = photoUrl;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("display_name", displayName);
            jsonObject.put("data", data);
            jsonObject.put("photo_url", photoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Contact fromJson(JSONObject jsonObject) {
        Contact contact = new Contact();
        try {
            contact.userId = jsonObject.getString("user_id");
            contact.displayName = jsonObject.getString("display_name");
            contact.data = jsonObject.getString("data");
            contact.photoUrl = jsonObject.getString("photo_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contact;
    }
}
