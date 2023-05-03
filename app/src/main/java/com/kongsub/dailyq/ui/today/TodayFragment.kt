package com.kongsub.dailyq.ui.today

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kongsub.dailyq.R
import com.kongsub.dailyq.api.response.Question
import com.kongsub.dailyq.databinding.FragmentTodayBinding
import com.kongsub.dailyq.ui.base.BaseFragment
import com.kongsub.dailyq.ui.image.ImageViewerActivity
import com.kongsub.dailyq.ui.write.WriteActivity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TodayFragment : BaseFragment() {
    var _binding: FragmentTodayBinding? = null
    val binding get() = _binding!!

    var question: Question? = null

    // registerForActivityResult 를 사용하여 콜백을 등록한다.
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        // 글쓰기 완료 및 수정 완료시, 작성한 내역을 다시 화면에 그려줌.
        if(result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch{
                setupAnswer()
            }
        }
    }

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

        binding.writeButton.setOnClickListener{
            // ActivityResultLauncher 의 launch() 메서드로 intent 를 등록해, WriteActivity 를 실행
            startForResult.launch(Intent(requireContext(),
                WriteActivity::class.java).apply{
                putExtra(WriteActivity.EXTRA_QID, question!!.id)
                putExtra(WriteActivity.EXTRA_MODE, WriteActivity.Mode.WRITE)
            })
        }
        binding.editButton.setOnClickListener {
            startForResult.launch(Intent(requireContext(),
                WriteActivity::class.java).apply {
                putExtra(WriteActivity.EXTRA_QID, question!!.id)
                putExtra(WriteActivity.EXTRA_MODE, WriteActivity.Mode.EDIT)
            })
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmDialog()
        }

        // 코루틴 스코프
        viewLifecycleOwner.lifecycleScope.launch{
            val questionResponse = api.getQuestion(LocalDate.now())
            if(questionResponse.isSuccessful) {
                question = questionResponse.body()!!

                val dataFormatter = DateTimeFormatter.ofPattern("yyyy. M. d")

                binding.date.text = dataFormatter.format(question!!.id)
                binding.question.text = question!!.text

                setupAnswer()
            }
        }
    }

    fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.dialog_msg_are_you_sure_to_delete)
            .setPositiveButton(R.string.ok){ dialog, which ->
                lifecycleScope.launch {
                    val deleteResponse = api.deleteAnswer(question!!.id)
                    if(deleteResponse.isSuccessful){
                        binding.answerArea.isVisible = false
                        binding.writeButton.isVisible = true
                    }
                }
            }.setNegativeButton(R.string.cancel) { dialog, which ->

            }.show()
    }

    suspend fun setupAnswer() {
        val question = question ?: return

        val answer = api.getAnswer(question.id).body()
        binding.answerArea.isVisible = answer != null
        binding.textAnswer.text = answer?.text

        binding.writeButton.isVisible = answer == null

        binding.photoAnswer.isVisible = !answer?.photo.isNullOrEmpty()
        answer?.photo?.let {
            binding.photoAnswer.load(it) {
                placeholder(R.drawable.ph_image)
            }
            binding.photoAnswer.setOnClickListener {
                startActivity(
                    Intent(requireContext(),
                ImageViewerActivity::class.java).apply {
                    putExtra(ImageViewerActivity.EXTRA_URL, answer.photo)
                })
            }
        }
    }
}

