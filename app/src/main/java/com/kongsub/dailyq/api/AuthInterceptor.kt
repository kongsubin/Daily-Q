package com.kongsub.dailyq.api

import com.kongsub.dailyq.AuthManager
import okhttp3.Interceptor
import okhttp3.Response

/* 어플리케이션 - OKHttp 간의 인터셉터
*  api 요청 헤더에 Authorization 정보를 추가함.
* */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        // Authorization 헤더가 필요한 경우에만, 선언
        val authType = request.tag(AuthType::class.java) ?: AuthType.ACCESS_TOKEN
        when (authType) {
            AuthType.NO_AUTH -> {

            }
            AuthType.ACCESS_TOKEN -> {
                AuthManager.accessToken?.let { token ->
                    builder.header("Authorization", "Bearer $token")
                }
            }
        }
        return chain.proceed(builder.build())
    }

}