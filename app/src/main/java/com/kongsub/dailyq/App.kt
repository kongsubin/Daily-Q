package com.kongsub.dailyq

import android.app.Application
import com.kongsub.dailyq.api.ApiService

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AuthManager.init(this)
        ApiService.init(this)
    }
}