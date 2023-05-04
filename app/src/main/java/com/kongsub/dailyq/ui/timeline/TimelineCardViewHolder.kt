package com.kongsub.dailyq.ui.timeline

import androidx.recyclerview.widget.RecyclerView
import com.kongsub.dailyq.R
import com.kongsub.dailyq.api.response.Question
import com.kongsub.dailyq.databinding.ItemTimelineCardBinding
import java.time.format.DateTimeFormatter

class TimelineCardViewHolder(val binding: ItemTimelineCardBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. M. d.")
    }

    fun bind(question: Question) {
        binding.date.text = DATE_FORMATTER.format(question.id)
        binding.question.text = question.text ?: ""

        binding.answerCount.text = if (question.answerCount > 0) {
            binding.root.context.getString(R.string.answer_count_format, question.answerCount)
        } else {
            binding.root.context.getString(R.string.no_answer_yet)
        }

        binding.card.setOnClickListener {
            // Todo 상세화면으로 이동
        }
    }
}