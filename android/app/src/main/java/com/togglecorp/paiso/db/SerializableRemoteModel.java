package com.togglecorp.paiso.db;

import android.util.Log;

import org.json.JSONObject;

public abstract class SerializableRemoteModel extends Model {
    public boolean modified;

    public abstract JSONObject toJson();
    public abstract void fromJson(JSONObject json);

    public static String optString(JSONObject json, String key) {
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }
}
