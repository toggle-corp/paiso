package com.togglecorp.paiso

import android.app.Application
import com.togglecorp.paiso.database.SyncManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        SyncManager.startPushing(this)
    }

}
