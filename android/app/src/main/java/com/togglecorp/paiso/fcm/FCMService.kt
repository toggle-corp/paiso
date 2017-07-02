package com.togglecorp.paiso.fcm

import android.app.NotificationManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.togglecorp.paiso.R
import com.togglecorp.paiso.database.SyncManager
import android.content.Intent
import com.togglecorp.paiso.MainActivity
import android.app.PendingIntent
import android.content.Context


class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        SyncManager.fetch(this)

        val data = message?.data.orEmpty()
        if (data.isEmpty()) {
            return
        }

        val title = data["title"]!!
        val user = data["user"]!!
        val amount = data["amount"]!!.toFloat()
        var action = data["action"]!!

        action = action[0].toUpperCase() + action.substring(1)

        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("$title ($amount)")
                .setContentText("$action by $user")

        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.setContentIntent(resultPendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())

    }
}
