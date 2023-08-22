package com.kongsub.dailyq.ui.profile

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kongsub.dailyq.api.ApiService
import com.kongsub.dailyq.api.response.QuestionAndAnswer
import java.time.LocalDate

class UserAnswerPagingSource(val api: ApiService, val uid: String) : PagingSource<LocalDate, QuestionAndAnswer>() {

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, QuestionAndAnswer> {
        val userAnswerResponse = api.getUserAnswers(uid, params.key)

        return if (userAnswerResponse.isSuccessful) {
            val userAnswers = userAnswerResponse.body()!!

            val nextKey = if (userAnswers.isNotEmpty()) {
                userAnswers.minOf { it.question.id }
            } else {
                null
            }

            LoadResult.Page(
                data = userAnswers,
                prevKey = null,
                nextKey = nextKey
            )
        } else {
            LoadResult.Error(Throwable("Paging Error"))
        }
    }

    override fun getRefreshKey(state: PagingState<LocalDate, QuestionAndAnswer>): LocalDate? = null
}