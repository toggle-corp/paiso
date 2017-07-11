package com.togglecorp.paiso.misc

import android.content.Context
import com.firebase.jobdispatcher.*
import com.togglecorp.paiso.database.SyncManager
import com.togglecorp.paiso.fcm.sendRegistrationToServer

class AutoSyncService : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        SyncManager.fetch(this).then { sendRegistrationToServer(this) }
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        return false
    }

    companion object {
        fun schedule(context: Context) {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))

            dispatcher.cancelAll()
            val job = dispatcher
                    .newJobBuilder()
                    .setService(AutoSyncService::class.java)
                    .setTrigger(Trigger.executionWindow(60*5, 60*10))
                    .setTag("sync-job")
                    .setRecurring(true)
                    .setLifetime(Lifetime.FOREVER)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build()

            dispatcher.mustSchedule(job)

        }
    }
}