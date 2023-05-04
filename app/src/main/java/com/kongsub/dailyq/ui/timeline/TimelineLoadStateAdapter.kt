package com.kongsub.dailyq.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.kongsub.dailyq.databinding.ItemTimelineLoadStateBinding

// 로딩 상태를 표시하기 위해 LoadStateAdapter 를 상속함.
class TimelineLoadStateAdapter(val retry: () -> Unit) : LoadStateAdapter<TimelineLoadStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): TimelineLoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder = ItemTimelineLoadStateBinding.inflate(layoutInflater, parent, false)

        return TimelineLoadStateViewHolder(viewHolder, retry)
    }

    // LoadStateAdapter 는 항상 하나의 아이템만 가짐으로 뷰 타입과 Position 을 전달하지 않고 LoadState 만 전달
    override fun onBindViewHolder(holder: TimelineLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}