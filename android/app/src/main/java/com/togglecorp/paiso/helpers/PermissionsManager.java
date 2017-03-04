package com.togglecorp.paiso.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PermissionsManager {
    private final static String TAG = "PermissionManager";
    private static List<Pair<PermissionListener, Integer>> mRequesters = new ArrayList<>();

    public static void check(Activity activity, String[] permissions, PermissionListener listener) {

        int result;
        List<String> neededPermissions = new ArrayList<>();
        for (String permission: permissions) {
            Log.d(TAG, "Looking permission: " + permission);
            result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Needed permission: " + permission);
                neededPermissions.add(permission);
            } else {
                listener.onGranted();
            }
        }
        if (!neededPermissions.isEmpty()) {
            int requestCode = listener.hashCode() & 0xFFFF;
            mRequesters.add(new Pair<>(listener, requestCode));
            ActivityCompat.requestPermissions(activity, neededPermissions.toArray(new String[neededPermissions.size()]), requestCode);
        }
    }

    public static void handleResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Iterator<Pair<PermissionListener, Integer>> iterator = mRequesters.iterator();
                while (iterator.hasNext()) {
                    Pair<PermissionListener, Integer> r = iterator.next();
                    if (r.first != null & r.second == requestCode) {
                        r.first.onGranted();
                        iterator.remove();
                    }
                }
            }
        }
    }
}
