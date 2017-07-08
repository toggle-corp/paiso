package com.togglecorp.paiso.fcm

import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.promise.Promise
import retrofit2.Response


fun sendRegistrationToServer(context: Context) {
    val refreshedToken = PreferenceManager.getDefaultSharedPreferences(context).getString("fcm-token", null)
    if (refreshedToken == null) {
        return
    }

    val myId = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt("myRemoteId", -1)
    if (myId == -1) {
        return
    }

    val tokenId = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt("fcmTokenId", -1)

    val token = FCMToken(token=refreshedToken, user=myId)
    val promise: Promise<Response<FCMToken>?>

    if (tokenId == -1) {
        promise = FCMTokenApi.post(Auth.getHeader(context), token).promise()
    } else {
        promise = FCMTokenApi.put(Auth.getHeader(context), tokenId, token).promise()
    }

    promise.then {
        val newId = it?.body()?.id

        if (newId != null) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit().putInt("fcmTokenId", newId).apply()
        }
    }
}

class FCMTokenService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString("fcm-token", refreshedToken).apply()

        if (refreshedToken != null) {
            sendRegistrationToServer(this)
        }
    }
}