package com.kongsub.dailyq.ui.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kongsub.dailyq.api.response.Question
import com.kongsub.dailyq.databinding.ItemTimelineCardBinding

class TimelineAdapter(val context: Context) : PagingDataAdapter<Question, TimelineCardViewHolder>(QuestionComparator) {
    object QuestionComparator: DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }

    val inflater = LayoutInflater.from(context)

    // ViewHolder : 10개 정도의 뷰 홀더들을 재사용함. 뷰 홀더의 text 나 이미지 등은 바뀔 수 있음.
    // ViewHolder 를 생성하는 함수. 13~15번 정도 호출되며 그 이상 호출되지 않음.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineCardViewHolder {
        return TimelineCardViewHolder(ItemTimelineCardBinding.inflate(inflater, parent, false))
    }

    // 생성된 ViewHolder 에 데이터를 바인딩 해주는 함수.
    override fun onBindViewHolder(holder: TimelineCardViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}