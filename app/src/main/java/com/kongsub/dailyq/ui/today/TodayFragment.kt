package com.kongsub.dailyq.ui.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.*
import com.kongsub.dailyq.api.response.HelloWorld
import com.kongsub.dailyq.databinding.FragmentTodayBinding
import com.kongsub.dailyq.ui.base.BaseFragment
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.util.*



class TodayFragment : BaseFragment() {
    var _binding: FragmentTodayBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 생성
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        // 바인딩 해제
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread {
            val url = URL("http://10.0.2.2:5000/v1/hello-world")

            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")
            conn.connect()

            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val body = reader.readText()
            reader.close()
            conn.disconnect()

            /* JSONObject 사용
            val json = JSONObject(body)
            val date = json.getString("date")
            val message = json.getString("message")
             */

            /*
            Json 속성 네이밍 정책.
                # 코틀린, 자바 : 카멜 케이스
                # JSON : 스네이크 케이스
                => 서로 다른 문자열이 같은 것을 알리는 Gson Naming 정책ß
                    1. IDENTITY : 정확하게 이름 동일
                    2. LOWER_CASE_WITH_UNDERSCORES : JSON 필드를 스네이크 케이스 언더바 _
                    3. LOWER_CASE_WITH_DASHED : JSON 필드를 스네이크 케이스 하이픈 -
                    4. UPPER_CAMEL_CASE : 첫글자를 대문자로
                    5. UPPER_CAMEL_CASE_WITH_SPACES : 첫글자를 대문자, 연결은 공백

            val nGson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            일정한 규칙이 없는 경우, 어노테이션 활용
                data class Version(
                    @SerializedName("version")
                    val appVersion:String
                )
            */


            val gson = Gson()
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.KOREA)
            val helloWorld = gson.fromJson(body, HelloWorld::class.java)

            activity?.runOnUiThread{
                binding.date.text = dateFormat.format(helloWorld.date)
                binding.question.text = helloWorld.message
            }
        }.start()
    }
}

