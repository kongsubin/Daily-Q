package com.kongsub.dailyq

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ApiService.init(this)
    }
}