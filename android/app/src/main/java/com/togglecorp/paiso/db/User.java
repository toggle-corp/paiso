package com.togglecorp.paiso.db;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends SerializableRemoteModel {
    public Integer userId;
    public String displayName;
    public String email;
    public String phone;
    public String photoUrl;

    public User() {}

    public User(Integer userId, String displayName, String email, String phone, String photoUrl) {
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
            object.put("userId", userId==null?JSONObject.NULL:userId);
            object.put("displayName", displayName);
            object.put("email", email==null?JSONObject.NULL:email);
            object.put("phone", phone==null?JSONObject.NULL:phone);
            object.put("photoUrl", photoUrl==null?JSONObject.NULL:photoUrl);
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

        userId = (Integer)json.opt("userId");
        displayName = json.optString("displayName");
        email = optString(json, "email");
        phone = optString(json, "phone");
        photoUrl = optString(json, "photoUrl");
    }
}
