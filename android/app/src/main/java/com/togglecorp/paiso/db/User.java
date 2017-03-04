package com.togglecorp.paiso.db;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends SerializableModel {
    public String userId;
    public String displayName;
    public String email;
    public String phone;
    public String photoUrl;

    public User() {}

    public User(String userId, String displayName, String email, String phone, String photoUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        try {
            object.put("userId", userId);
            object.put("displayName", displayName);
            object.put("email", email);
            object.put("phone", phone);
            object.put("photoUrl", photoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}
