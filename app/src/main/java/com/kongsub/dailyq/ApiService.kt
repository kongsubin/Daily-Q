package com.kongsub.dailyq

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kongsub.dailyq.api.response.Question
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

/* Retrofit 생성시, OkHttpClient, Gson, Converters 등 여러 객체를 생성하므로
   싱글톤 패턴으로 만들어 앱 전체에서 하나의 인스턴스를 공유하게 만듦 */
interface ApiService {

    companion object {

        private var INSTANCE: ApiService? = null

        private fun create(context: Context) : ApiService {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            /* 컨버터 팩토리 : Retrofit 컨버터는 'HTTP 요청 본문' <-> '매개변수' 쌍방 변환시키는 역할을 함.
                - EX) JSON 으로 응답받은 문자열을 메서드의 반환타입인 Question 으로 변환.
                - JSON 외에도 , xml, protocol buffer, scalars(string, int 등 자료형) 변환기가 있음.
                - 제공하는 컨버터말고 커스텀 컨버터를 정의하여 사용가능. 
             */
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://10.0.2.2:5000")
                .build()
                .create(ApiService::class.java)
        }

        fun init(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: create(context).also {
                INSTANCE = it
            }
        }

        fun getInstance(): ApiService = INSTANCE!!
    }


    // @Path : 매개변수가 경로에서 사용됨
    // Question : 응답 구조
    @GET("v1/questions/{pid}")
    suspend fun getQuestion(@Path("pid") pid: String): Question
}