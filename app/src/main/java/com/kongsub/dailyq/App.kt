package com.kongsub.dailyq

import android.app.Application
import com.kongsub.dailyq.api.ApiService
import com.kongsub.dailyq.db.AppDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AuthManager.init(this)
        ApiService.init(this)
        AppDatabase.init(this)
        Notifier.init(this)
    }
}