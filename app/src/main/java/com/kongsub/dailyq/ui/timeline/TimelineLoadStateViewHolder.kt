package com.kongsub.dailyq.ui.timeline

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.kongsub.dailyq.databinding.ItemTimelineLoadStateBinding

// 재시도 버튼 클릭시 실행될 함수를 생성자로 받음.
class TimelineLoadStateViewHolder(
    val binding: ItemTimelineLoadStateBinding,
    val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retry.setOnClickListener {
            retry()
        }
    }
    fun bind(loadState: LoadState) {
        binding.progress.isVisible = loadState is LoadState.Loading
        binding.retry.isVisible = loadState is LoadState.Error
    }
}