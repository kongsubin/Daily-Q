package com.kongsub.dailyq.ui.write

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.kongsub.dailyq.R
import com.kongsub.dailyq.api.response.Answer
import com.kongsub.dailyq.api.response.Question
import com.kongsub.dailyq.databinding.ActivityMainBinding
import com.kongsub.dailyq.databinding.ActivityWriteBinding
import com.kongsub.dailyq.ui.base.BaseActivity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WriteActivity : BaseActivity() {
    companion object {
        const val EXTRA_QID = "qid"
        const val EXTRA_MODE = "mode"
    }

    enum class Mode {
        WRITE, EDIT
    }

    lateinit var binding: ActivityWriteBinding
    lateinit var mode: Mode
    lateinit var question: Question
    var answer: Answer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 질문 전달 받기
        val qid = intent.getSerializableExtra(EXTRA_QID) as LocalDate
        mode = intent?.getSerializableExtra(EXTRA_MODE)!! as Mode

        supportActionBar?.title = DateTimeFormatter.ofPattern(getString(R.string.date_format)).format(qid)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            question = api.getQuestion(qid).body()!!
            answer = api.getAnswer(qid).body()

            binding.question.text = question.text
            binding.answer.setText(answer?.text)
        }
    }

    // 앱바에서 이미지 첨부 버튼과 완료 버튼 추가
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.write_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 완료 버튼 클릭시, write 함수 호출
        when(item.itemId) {
            R.id.done -> {
                write()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // mode에 따라 writeAnswer 인지, editAnswer 인지 분기 n
    fun write() {
        val text = binding.answer.text.toString().trimEnd()
        lifecycleScope.launch {
            val answerResponse = if (answer == null) {
                // 완료 요청을 전달하기 위함.
                setResult(RESULT_OK)
                api.writeAnswer(question.id, text)
            } else {
                api.editAnswer(question.id, text)
            }
            if(answerResponse.isSuccessful) {
                finish()
            } else {
                Toast.makeText(
                    this@WriteActivity,
                    answerResponse.message(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}