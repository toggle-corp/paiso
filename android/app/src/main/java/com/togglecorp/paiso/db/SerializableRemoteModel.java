package com.togglecorp.paiso.db;

import android.util.Log;

import org.json.JSONObject;

public abstract class SerializableRemoteModel extends Model {
    public boolean modified;

    public abstract JSONObject toJson();
    public JSONObject toJson(DbHelper dbHelper) {
        return toJson();
    }
    public abstract void fromJson(JSONObject json);

    public static String optString(JSONObject json, String key) {
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }

    public static Integer optInteger(JSONObject json, String key) {
        if (!json.isNull(key) && json.optInt(key, -1) >= 0){
            return json.optInt(key, -1);
        } else {
            return null;
        }
    }
}
