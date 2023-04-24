package com.kongsub.dailyq.api

import com.kongsub.dailyq.AuthManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator() : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = response.request.header("Authorization")
            ?.split(" ")
            ?.getOrNull(1)
        accessToken ?: return null  // Authorization 헤더를 사용하지 않는 경우, null을 반환
        AuthManager.refreshToken ?: return null // 리프레시 토큰이 있어야 발급 가능하므로, 리프레시 토큰이 없는 경우, Null 반환

        val api = ApiService.getInstance()

        synchronized(this) {    // 여러번 요청이 들어와, 토큰 갱신이 여러번 동시적으로 일어나는 것을 방지하기 위해 synchronized 사용.
            if (accessToken == AuthManager.accessToken) {
                val authTokenResponse = api.refreshToken(AuthManager.refreshToken!!).execute().body()!!

                AuthManager.accessToken = authTokenResponse.accessToken
                AuthManager.refreshToken = authTokenResponse.refreshToken
            }
        }

        // 토큰 갱신시, 새로운 Request 를 생성하고, Authorization 헤더가 교체된 요청을 만들어 반환
        return response.request.newBuilder()
            .header("Authorization", "Bearer ${AuthManager.accessToken}")
            .build()
    }
}