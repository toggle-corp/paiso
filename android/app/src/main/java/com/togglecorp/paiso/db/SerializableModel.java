package com.togglecorp.paiso.db;

import org.json.JSONObject;

public abstract class SerializableModel extends Model {
    public boolean modified;

    public abstract JSONObject toJson();
}
