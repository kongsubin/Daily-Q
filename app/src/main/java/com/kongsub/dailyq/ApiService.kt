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

interface ApiService {

    companion object {
        fun create(context: Context) : ApiService {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://10.0.2.2:5000")
                .build()
                .create(ApiService::class.java)
        }
    }


    // @Path : 매개변수가 경로에서 사용됨
    // Question : 응답 구조
    @GET("v1/questions/{pid}")
    suspend fun getQuestion(@Path("pid") pid: String): Question
}