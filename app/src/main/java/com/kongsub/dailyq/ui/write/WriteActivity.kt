package com.kongsub.dailyq.ui.write

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kongsub.dailyq.R
import com.kongsub.dailyq.api.asRequestBody
import com.kongsub.dailyq.api.response.Answer
import com.kongsub.dailyq.api.response.Question
import com.kongsub.dailyq.databinding.ActivityMainBinding
import com.kongsub.dailyq.databinding.ActivityWriteBinding
import com.kongsub.dailyq.ui.base.BaseActivity
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
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
    var imageUrl: String? = null

    // 이미지 선택시, 이미지 intent 에서 URI 가 전달됨.
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                lifecycleScope.launch {
                    val imageUri = result.data?.data ?: return@launch
                    val requestBody = imageUri.asRequestBody(contentResolver)

                    val part = MultipartBody.Part.createFormData("image", "filename", requestBody)
                    val imageResponse = api.uploadImage(part)

                    if (imageResponse.isSuccessful) {
                        imageUrl = imageResponse.body()!!.url

                        binding.photo.load(imageUrl){
                            transformations(RoundedCornersTransformation(resources.getDimension(R.dimen.thumbnail_rounded_corner)))
                            binding.photoArea.isVisible = true
                        }
                    }
                }
            }
        }


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

            // 이미지 업로드
            imageUrl = answer?.photo
            binding.answer.setText(answer?.text)

            imageUrl?.let {
                binding.photo.load(it) {
                    // RoundedCornersTransformation : 섬네일이 둥글게 보여지게 하기 위함.
                    transformations(RoundedCornersTransformation(resources.getDimension(R.dimen.thumbnail_rounded_corner)))
                }
            }
        }

        binding.photoArea.setOnClickListener {
            showDeleteConfirmDialog()
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
            // 사진을 선택할 수 있도록 image type을 인텐트로 전달.
            R.id.add_photo -> {
                startForResult.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES,
                        arrayOf("image/jpeg", "image/png"))
                    }
                )
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
                api.writeAnswer(question.id, text, imageUrl)
            } else {
                api.editAnswer(question.id, text, imageUrl)
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

    fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.dialog_msg_are_you_sure_to_delete)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                binding.photo.setImageResource(0)
                binding.photoArea.isVisible = false
                imageUrl = null
            }.setNegativeButton(android.R.string.cancel) { dialog,
            which ->

            }.show()
    }
}