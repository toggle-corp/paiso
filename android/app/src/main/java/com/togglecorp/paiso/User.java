package com.togglecorp.paiso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    public String displayName;
    public String email;
    public String photoUrl;

    public User() {}

    public User(String displayName, String email, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("display_name", displayName);
            jsonObject.put("email", email);
            jsonObject.put("photo_url", photoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        try {
            user.displayName = jsonObject.getString("display_name");
            user.email = jsonObject.getString("email");
            user.photoUrl = jsonObject.getString("photo_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
