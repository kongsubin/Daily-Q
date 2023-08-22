package com.kongsub.dailyq.ui.profile

import android.content.Intent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kongsub.dailyq.R
import com.kongsub.dailyq.api.response.QuestionAndAnswer
import com.kongsub.dailyq.databinding.ItemUserAnswerCardBinding
import com.kongsub.dailyq.ui.details.DetailsActivity
import com.kongsub.dailyq.ui.image.ImageViewerActivity
import java.time.format.DateTimeFormatter

class UserAnswerViewHolder(val binding: ItemUserAnswerCardBinding) : RecyclerView.ViewHolder(binding.root){
    companion object {
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. M. d.")
    }

    fun bind(item: QuestionAndAnswer) {
        val question = item.question
        val answer = item.answer

        binding.date.text = DATE_FORMATTER.format(question.id)

        binding.question.text = question.text
        binding.textAnswer.text = answer.text
        answer.photo?.let {
            binding.photoAnswer.load(it) {
                placeholder(R.drawable.ph_image)
                error(R.drawable.ph_image)
            }
        }
        binding.textAnswer.isVisible = !answer.text.isNullOrEmpty()
        binding.photoAnswer.isVisible = !answer.photo.isNullOrEmpty()

        binding.photoAnswer.setOnClickListener {
            val context = itemView.context
            context.startActivity(Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(ImageViewerActivity.EXTRA_URL, answer.photo)
            })
        }
        binding.root.setOnClickListener {
            val context = itemView.context
            context.startActivity(Intent(context, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.EXTRA_QID, question.id)
            })
        }
    }
}