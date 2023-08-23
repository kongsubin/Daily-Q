package com.kongsub.dailyq.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.kongsub.dailyq.databinding.FragmentTimelineBinding
import com.kongsub.dailyq.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TimelineFragment : BaseFragment() {

    var _binding: FragmentTimelineBinding? = null
    val binding get() = _binding!!
    lateinit var adapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            adapter = TimelineAdapter(requireContext())

            // withLoadStateFooter : 로드 상태 표시
            // retry : 에러시 새로고침을 위한 것
            recycler.adapter = adapter.withLoadStateFooter(TimelineLoadStateAdapter{
                adapter.retry()
            })

            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            // initialLoadSize : 처음요청하는 페이지의 크기,
            /*
            Pager(PagingConfig(initialLoadSize = 6, pageSize = 3, enablePlaceholders = false)){
                TimelinePagingSource(api)
            }.flow.collectLatest {
                adapter.submitData(it)
            } */
            @OptIn(ExperimentalPagingApi::class)
            Pager(
                PagingConfig(initialLoadSize = 6, pageSize = 3, enablePlaceholders = false),
                null,
                TimelineRemoteMediator(api, db)
            ) {
                db.getQuestionDao().getPagingSource()
            }.flow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}