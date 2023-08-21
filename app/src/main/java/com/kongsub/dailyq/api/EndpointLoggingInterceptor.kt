package com.kongsub.dailyq.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

// 인터셉터의 위치를 구분할 이름과, 확인하려는 API URL의 접미사를 전달 받음. 퇴사하고싶당^^...
class EndpointLoggingInterceptor(val name: String, val urlSuffix: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.method != "GET" || !request.url.encodedPath.endsWith(urlSuffix)){
            return chain.proceed((request))
        }

        Log.i("DailyQ_$urlSuffix",
        """--> $name
            |${request.url}
            |${request.headers}""".trimMargin())

        val response = chain.proceed(request)

        Log.i("DailyQ_$urlSuffix",
        """<-- $name
            |Response code: ${response.code} (Network: ${response.networkResponse?.code}, 
            Cache: ${response.cacheResponse?.code})
            |${response.headers}""".trimMargin())

        return response
    }
}