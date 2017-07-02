package com.togglecorp.paiso.fcm

import android.preference.PreferenceManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.promise.Promise
import retrofit2.Response


class FCMTokenService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        if (refreshedToken != null) {
            sendRegistrationToServer(refreshedToken)
        }
    }

    private fun sendRegistrationToServer(refreshedToken: String) {
        val myId = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("myRemoteId", -1)
        if (myId == -1) {
            return
        }

        val tokenId = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("fcmTokenId", -1)

        val token = FCMToken(token=refreshedToken, user=myId)
        val promise: Promise<Response<FCMToken>?>

        if (tokenId == -1) {
            promise = FCMTokenApi.post(Auth.getHeader(this), token).promise()
        } else {
            promise = FCMTokenApi.put(Auth.getHeader(this), tokenId, token).promise()
        }

        promise.then {
            val newId = it?.body()?.id

            if (newId != null) {
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit().putInt("fcmTokenId", newId).apply()
            }
        }
    }

}