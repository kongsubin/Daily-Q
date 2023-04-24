package com.kongsub.dailyq

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AuthManager.init(this)
        ApiService.init(this)
    }
}