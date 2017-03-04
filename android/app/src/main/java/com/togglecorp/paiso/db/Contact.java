package com.togglecorp.paiso.db;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact extends SerializableModel {
    public Long contactId = null;
    public String displayName;
    public String email;
    public String phone;
    public String photoUrl;


    public Contact() {}

    public Contact(Long contactId, String displayName, String email, String phone, String photoUrl) {
        this.contactId = contactId;
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        try {
            object.put("contactId", contactId);
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
