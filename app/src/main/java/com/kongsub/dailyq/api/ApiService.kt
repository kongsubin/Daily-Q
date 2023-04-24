package com.kongsub.dailyq.api

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kongsub.dailyq.AuthManager
import com.kongsub.dailyq.api.response.Question
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import com.kongsub.dailyq.api.adapter.LocalDateAdapter
import com.kongsub.dailyq.api.converter.LocalDateConverterFactory
import com.kongsub.dailyq.api.response.Answer
import com.kongsub.dailyq.api.response.AuthToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/* Retrofit 생성시, OkHttpClient, Gson, Converters 등 여러 객체를 생성하므로
   싱글톤 패턴으로 만들어 앱 전체에서 하나의 인스턴스를 공유하게 만듦 */
interface ApiService {

    companion object {

        private var INSTANCE: ApiService? = null

        private fun okHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            /*
            NONE : 로그 출력 x
            BASIC : 요청라인과 응답 라인만 출력
            HEADERS : 요청라인과 요청헤더 응답라인과 응답 헤더 출력
            BODY : 요청라인, 헤더, 본문, 응답 라인, 헤더, 본문을 출력
             */

            return builder
                .connectTimeout(3, TimeUnit.SECONDS)    // TCP handshake 실패 시, 기본값 10초
                .writeTimeout(10, TimeUnit.SECONDS)
                // 서버와 연결 후 데이터 수신시 정해진 시간 초과할때 발생,
                // 10초안에 데이터를 받아와야한다는 뜻이 아닌, 데이터를 읽어오는 각 작동의 간격이 10초를 초과하면 안됨.
                .readTimeout(10, TimeUnit.SECONDS)  // 서버로 데이터를 보낼 때 발생
                .addInterceptor(AuthInterceptor()) // OkHttp - Application 간의 Interceptors
                .addInterceptor(logging)
                .build()
        }
        private fun create(context: Context) : ApiService {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter)
                .create()

            /* 컨버터 팩토리 : Retrofit 컨버터는 'HTTP 요청 본문' <-> '매개변수' 쌍방 변환시키는 역할을 함.
                - EX) JSON 으로 응답받은 문자열을 메서드의 반환타입인 Question 으로 변환.
                - JSON 외에도 , xml, protocol buffer, scalars(string, int 등 자료형) 변환기가 있음.
                - 제공하는 컨버터말고 커스텀 컨버터를 정의하여 사용가능.
             */
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(LocalDateConverterFactory())
                .baseUrl("http://10.0.2.2:5000")
                .client(okHttpClient())
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

    @FormUrlEncoded
    @POST("/v2/token")
    suspend fun login(
        @Field("username") uid: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password",
    ): Response<AuthToken>

    @FormUrlEncoded
    @POST("/v2/token")
    fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
    ): Call<AuthToken>

    // @Path : 매개변수가 경로에서 사용됨
    // Question : 응답 구조
    @GET("v2/questions/{pid}")
    suspend fun getQuestion(
        @Path("pid") pid: LocalDate
    ): Response<Question> // Response - HTTP 응답 코드 및 헤더와 같은 정보를 가져올 수 잇음.

    @GET("/v2/questions/{qid}/answers/{uid}")
    suspend fun getAnswer(
        @Path("qid") qid: LocalDate,
        @Path("uid") uid: String? = AuthManager.uid // "anonymous" => 인증없이 사용하기 위해 하드코딩한 것을 변경
    ): Response<Answer>

    @FormUrlEncoded // 요청의 Content-Type 을 "application/x-www-form-urlencoded"로 만든다.
    @POST("/v2/questions/{qid}/answers")
    suspend fun writeAnswer(
        @Path("qid") qid: LocalDate,
        @Field("text") text: String? = null,
        @Field("photo") photo: String? = null
    /*
    @Path("qid") qid: String,
    @Body params: WirteParmas // 객체를 JSON으로 직렬화하여 요청의 본문으로 사용됨.
     */
    ): Response<Answer>

    @FormUrlEncoded
    @PUT("/v2/questions/{qid}/answers/{uid}")
    suspend fun editAnswer(
        @Path("qid") qid: LocalDate,
        @Field("text") text: String? = null,
        @Field("photo") photo: String? = null,
        @Path("uid") uid: String? = AuthManager.uid // "anonymous" => 인증없이 사용하기 위해 하드코딩한 것을 변경
    ): Response<Answer>

    @DELETE("/v2/questions/{qid}/answers/{uid}")
    suspend fun deleteAnswer(
        @Path("qid") qid: LocalDate,
        @Path("uid") uid: String? = AuthManager.uid // "anonymous" => 인증없이 사용하기 위해 하드코딩한 것을 변경
    ): Response<Unit>

}