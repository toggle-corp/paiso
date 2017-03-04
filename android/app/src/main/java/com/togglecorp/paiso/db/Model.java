package com.togglecorp.paiso.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Model {
    // Every table has an '_id' field which is the primary key
    public long _id = -1;

    // Get the CREATE TABLE sql query
    public String getCreateTableSql() {
        Class myClass = this.getClass();
        Field[] fields = myClass.getFields();

        // Parse each field and get the equivalent SQLite type
        String cols = "";
        for (Field field : fields) {
            String sqlType = "";
            String typeName = field.getType().getSimpleName();

            // String maps to TEXT; long, int and boolean to INTEGER; float and double to REAL
            switch (typeName) {
                case "String":
                    sqlType = "TEXT";
                    break;
                case "int":
                case "long":
                case "Long":
                case "Integer":
                    sqlType = "INTEGER";
                    break;
                case "boolean":
                case "Boolean":
                    sqlType = "INTEGER";
                    break;
                case "float":
                case "double":
                case "Float":
                case "Double":
                    sqlType = "REAL";
                    break;
                case "byte[]":
                    sqlType = "BLOB";
                    break;
            }

            if (!sqlType.equals("")) {
                // separate column names by comma
                if (!cols.equals(""))
                    cols += ", ";
                // add the column
                cols += field.getName() + " " + sqlType;
                // _id is the PRIMARY KEY
                if (field.getName().equals("_id"))
                    cols += " PRIMARY KEY AUTOINCREMENT";
            }
        }
        return "CREATE TABLE " + myClass.getSimpleName() + " ("
                + cols + ")";
    }

    // Get DROP TABLE sql query
    public String getDestroyTableSql() {
        Class myClass = this.getClass();
        return "DROP TABLE IF EXISTS " + myClass.getSimpleName();
    }

    // Save (insert/update) the object as a row in the table
    public void save(SQLiteOpenHelper helper) {
        Class myClass = this.getClass();
        Field[] fields = myClass.getFields();

        if (_id >= 0)
            delete(myClass, helper, "_id=?", new String[]{"" + _id});

        ContentValues values = new ContentValues();

        // Parse each field and put values for each field
        for (Field field : fields) {

            // We won't set the _id field if it is -1
            if (!(field.getName().equals("_id")) || this._id >= 0) {
                String typeName = field.getType().getSimpleName();
                try {
                    switch (typeName) {
                        case "String":
                            values.put(field.getName(), (String)field.get(this));
                            break;
                        case "int":
                            values.put(field.getName(), field.getInt(this));
                            break;
                        case "Integer":
                            values.put(field.getName(), (Integer)field.get(this));
                            break;
                        case "long":
                            values.put(field.getName(), field.getLong(this));
                            break;
                        case "Long":
                            values.put(field.getName(), (Long)field.get(this));
                            break;
                        case "boolean":
                            values.put(field.getName(), field.getBoolean(this) ? 1 : 0);
                            break;
                        case "Boolean":
                            values.put(field.getName(), (Boolean)field.get(this));
                        case "float":
                            values.put(field.getName(), field.getFloat(this));
                            break;
                        case "Float":
                            values.put(field.getName(), (Float)field.get(this));
                            break;
                        case "double":
                            values.put(field.getName(), field.getDouble(this));
                            break;
                        case "Double":
                            values.put(field.getName(), (Double)field.get(this));
                            break;
                        case "byte[]":
                            values.put(field.getName(), (byte[]) field.get(this));
                            break;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // finally insert and in case unique fields (e.g. _id) conflicts, replace the row in the table
        SQLiteDatabase db = helper.getWritableDatabase();
        _id = db.insertWithOnConflict(myClass.getSimpleName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public static <T extends Model> List<T> getAll(Class<T> myClass, SQLiteOpenHelper helper) {
        return query(myClass, helper, null, null, null, null, null);
    }

    public static <T extends Model> List<T> getAll(Class<T> myClass, SQLiteOpenHelper helper, String order_by) {
        return query(myClass, helper, null, null, null, null, order_by);
    }

    // Get list of rows from database table with given sql query
    public static <T extends Model> List<T> query(Class<T> myClass, SQLiteOpenHelper helper, String selection, String[] args, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Field[] fields = myClass.getFields();

        // query
        Cursor c = db.query(true, myClass.getSimpleName(), null, selection, args, groupBy, having, orderBy, null);

        // Create object from each row in the result/cursor
        List<T> list = new ArrayList<>(c.getCount());
        c.moveToPosition(-1);
        while (c.moveToNext()) {
            try {
                T object = myClass.newInstance();

                // For each field, set the value from the cursor
                for (Field field : fields) {
                    String typeName = field.getType().getSimpleName();
                    switch (typeName) {
                        case "String":
                            field.set(object, c.getString(c.getColumnIndex(field.getName())));
                            break;
                        case "int":
                        case "Integer":
                            field.setInt(object, c.getInt(c.getColumnIndex(field.getName())));
                            break;
                        case "long":
                        case "Long":
                            field.setLong(object, c.getLong(c.getColumnIndex(field.getName())));
                            break;
                        case "boolean":
                        case "Boolean":
                            field.setBoolean(object, c.getInt(c.getColumnIndex(field.getName())) != 0);
                            break;
                        case "float":
                        case "Float":
                            field.setFloat(object, c.getFloat(c.getColumnIndex(field.getName())));
                            break;
                        case "double":
                        case "Double":
                            field.setDouble(object, c.getDouble(c.getColumnIndex(field.getName())));
                            break;
                        case "byte[]":
                            field.set(object, c.getBlob(c.getColumnIndex(field.getName())));
                            break;
                    }
                }

                list.add(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        c.close();
        db.close();
        return list;
    }

    public static <T extends Model> List<T> query(Class<T> myClass, SQLiteOpenHelper helper, String selection, String[] args) {
        return query(myClass, helper, selection, args, null, null, null);
    }

    public static <T extends Model> T get(Class<T> myClass, SQLiteOpenHelper helper, String selection, String[] args, String order_by) {
        return get(myClass, helper.getReadableDatabase(), selection, args, order_by);
    }

    public static <T extends Model> T get(Class<T> myClass, SQLiteOpenHelper helper, String selection, String[] args) {
        return get(myClass, helper, selection, args, null);
    }

    public static <T extends Model> T get(Class<T> myClass, SQLiteDatabase db, String selection, String[] args, String order_by) {
        Field[] fields = myClass.getFields();

        // query
        Cursor c = db.query(myClass.getSimpleName(), null, selection, args, null, null, order_by);

        // Create object from each row in the result/cursor
        c.moveToPosition(-1);
        T object = null;
        if (c.moveToNext()) {
            try {
                object = myClass.newInstance();

                // For each field, set the value from the cursor
                for (Field field: fields) {
                    String typeName = field.getType().getSimpleName();
                    switch (typeName) {
                        case "String":
                            field.set(object, c.getString(c.getColumnIndex(field.getName())));
                            break;
                        case "int":
                        case "Integer":
                            field.setInt(object, c.getInt(c.getColumnIndex(field.getName())));
                            break;
                        case "long":
                        case "Long":
                            field.setLong(object, c.getLong(c.getColumnIndex(field.getName())));
                            break;
                        case "boolean":
                        case "Boolean":
                            field.setBoolean(object, c.getInt(c.getColumnIndex(field.getName())) != 0);
                            break;
                        case "float":
                        case "Float":
                            field.setFloat(object, c.getFloat(c.getColumnIndex(field.getName())));
                            break;
                        case "double":
                        case "Double":
                            field.setDouble(object, c.getDouble(c.getColumnIndex(field.getName())));
                            break;
                        case "byte[]":
                            field.set(object, c.getBlob(c.getColumnIndex(field.getName())));
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        c.close();
        db.close();
        return object;
    }

    public static <T extends Model> T get(Class<T> myClass, SQLiteOpenHelper helper, long id) {
        return get(myClass, helper, "_id=?", new String[]{id+""}, null);
    }

    public static <T extends Model> T get(Class<T> myClass, SQLiteDatabase db, long id) {
        return get(myClass, db, "_id=?", new String[]{id+""}, null);
    }

    // Get the number of rows with given query
    public static int count(Class myClass, SQLiteOpenHelper helper, String selection, String[] args) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + myClass.getSimpleName() + " WHERE " + selection, args);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        cursor.close();
        db.close();
        return size;
    }

    // Delete everything
    public static void deleteAll(Class myClass, SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(myClass.getSimpleName(), null, null);
        db.close();
    }

    // Delete with query
    public static void delete(Class myClass, SQLiteOpenHelper helper, String selection, String[] args) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(myClass.getSimpleName(), selection, args);
        db.close();
    }

    // Delete seld
    public void delete(SQLiteOpenHelper helper) {
        delete(getClass(), helper, "_id=?", new String[]{_id+""});
    }
}
