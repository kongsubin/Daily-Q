package com.kongsub.dailyq

import android.content.Context
import android.content.SharedPreferences

// object > 싱글톤
object AuthManager {
    const val UID = "uid"
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"

    lateinit var prefs: SharedPreferences   // 로그아웃 전까지 사용할 수 있게, 앱 종료후에도 데이터를 보관 하는 기능.

    // 최초 1회 Context를 이용한 초기화
    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    // getter setter를 만들어, 사용자가 직접 SharedPreferences 을 호출하는 것을 방지한다.
    var uid: String?
        get() = prefs.getString(UID, null)
        set(value) {
            prefs.edit().putString(UID, value).apply()
        }
    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN, null)
        set(value) {
            prefs.edit().putString(ACCESS_TOKEN, value).apply()
        }
    var refreshToken: String?
        get() = prefs.getString(REFRESH_TOKEN, null)
        set(value) {
            prefs.edit().putString(REFRESH_TOKEN, value).apply()
        }

    fun clear() {
        prefs.edit().clear().apply()
    }
}