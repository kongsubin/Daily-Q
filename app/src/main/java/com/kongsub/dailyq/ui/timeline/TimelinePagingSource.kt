package com.kongsub.dailyq.ui.timeline

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kongsub.dailyq.api.ApiService
import com.kongsub.dailyq.api.response.Question
import java.time.LocalDate

// paging 라이브러리에서 API나 로컬 데이터베이스에서 데이터를 불러오는 역할
/*  매개변수
    - LocalDate : 질문 목록 가져오기 API 호출시 사용하는 Key
    - Question : 가져올 데이터 타입
    생성자
    - API 인스턴스
 */
class TimelinePagingSource(val api: ApiService) : PagingSource<LocalDate, Question>(){

    // LoadParams 를 인자로 받아 조건에 따라 데이터를 불러옴.
    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, Question> {
        val fromDate = params.key ?: LocalDate.now()

        val questionsResponse = api.getQuestions(fromDate, params.loadSize)

        if (questionsResponse.isSuccessful) {
            val questions = questionsResponse.body()!!

            if (questions.isNotEmpty()){
                val oldest = questions.minOf { it.id }
                val nextKey = oldest.minusDays(1)

                // 불러온 데이터와 다음에 넘길 데이터의 Key 값을 반환
                return LoadResult.Page(
                    data = questions,
                    prevKey = null,
                    nextKey = nextKey
                )
            }
            return LoadResult.Page(
                data = questions,
                prevKey = null,
                nextKey = null
            )
        }
        return LoadResult.Error(Throwable("Paging Error"))
    }

    // PagingState 를 받아 페이징 데이터를 갱신할 때 사용할 키를 반환. null 반환시, load() 메서드에서 기본값을 사용
    override fun getRefreshKey(state: PagingState<LocalDate, Question>): LocalDate? = null
}