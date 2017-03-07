package com.togglecorp.paiso.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.togglecorp.paiso.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class JsonRequest {
    private static final String TAG = "JsonRequest";

    private JSONObject mData;
    private String mBaseAddress;

    private String mResult;
    private int mStatus;

    public JsonRequest(Context context) {
        mData = null;
        mBaseAddress = context.getString(R.string.base_url);
    }

    public JsonRequest setData(JSONObject data) {
        mData = data;
        return this;
    }

    private JsonRequest request(String method, String path) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mBaseAddress + path);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            if (mData != null) {
                String data = mData.toString();
                connection.setDoOutput(true);

                connection.setFixedLengthStreamingMode(data.getBytes().length);
                //connection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                out.write(data.getBytes());
                out.flush();
            }

            mStatus = connection.getResponseCode();

            // Get the response and disconnect when done
            if (mStatus >= 400) {
                InputStream in = new BufferedInputStream(connection.getErrorStream());
                mResult = new Scanner(in).useDelimiter("\\A").next();
            } else {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                mResult = new Scanner(in).useDelimiter("\\A").next();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            mStatus = 444;
            mResult = "Couldn't execute request";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return this;
    }

    public boolean isSuccess() {
        return mStatus == 200 || mStatus == 201;
    }

    public JSONObject getSuccessDataObject() {
        if (!isSuccess() || !getResult().optBoolean("status") || !getResult().has("data"))
            return null;
        return getResult().optJSONObject("data");
    }

    public JSONArray getSuccessDataArray() {
        if (!isSuccess() || !getResult().optBoolean("status") || !getResult().has("data"))
            return null;
        return getResult().optJSONArray("data");
    }

    private class AsyncRequest extends AsyncTask<Void, Void, Void> {
        private String mMethod;
        private String mPath;
        private JsonRequestListener mListener;

        private AsyncRequest(String method, String path, JsonRequestListener listener) {
            mMethod = method;
            mPath = path;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            request(mMethod, mPath);
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (mListener != null) {
                mListener.onRequestComplete(JsonRequest.this);
            }
        }

    }


    public String getResultText() {
        return mResult;
    }

    public JSONObject getResult() {
        try {
            return new JSONObject(mResult);
        } catch (JSONException e) {
            e.printStackTrace();

            JSONObject json = new JSONObject();
            try {
                json.put("result", mResult);
                json.put("status", mStatus);
            } catch (JSONException ignored) {}
            return json;
        }
    }

    public int getStatus() {
        return mStatus;
    }

    public void get(String path, JsonRequestListener listener) {
        new AsyncRequest("GET", path, listener).execute();
    }

    public void post(String path, JsonRequestListener listener) {
        new AsyncRequest("POST", path, listener).execute();
    }

    public void delete(String path, JsonRequestListener listener) {
        new AsyncRequest("DELETE", path, listener).execute();
    }
}
