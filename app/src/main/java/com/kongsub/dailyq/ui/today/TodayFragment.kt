package com.kongsub.dailyq.ui.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.gson.*
import com.kongsub.dailyq.ApiService
import com.kongsub.dailyq.api.response.HelloWorld
import com.kongsub.dailyq.databinding.FragmentTodayBinding
import com.kongsub.dailyq.ui.base.BaseFragment
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

        // 코루틴 스코프
        viewLifecycleOwner.lifecycleScope.launch {
            val question = api.getQuestion(LocalDate.now())
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy. M. d.")
            binding.date.text = dateFormatter.format(question.id)
            binding.question.text = question.text
        }
    }
}

