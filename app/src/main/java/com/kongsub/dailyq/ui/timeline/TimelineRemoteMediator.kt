package com.kongsub.dailyq.ui.timeline

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kongsub.dailyq.api.ApiService
import com.kongsub.dailyq.db.AppDatabase
import com.kongsub.dailyq.db.entity.QuestionEntity
import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.time.LocalDate

@OptIn(ExperimentalPagingApi::class)
class TimelineRemoteMediator(val api: ApiService, val db: AppDatabase): RemoteMediator<Int, QuestionEntity>() {

    // 시작시 호출하며 갱신 필요 여부를 반환
    // 갱신이 필요한 경우 : InitializeAction.LAUNCH_INITIAL_REFRESH
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    // 로칼 DB의 PagingSosurce에서 데이터를 불러오다, 데이터가 없는 경계에 도달할 경우 서버로부터 추가 데이터 요청할 경우.
    override suspend fun load(
        loadType: LoadType,                         // 새로고침인지, 목록의 앞 뒷부분 데이터인지
        state: PagingState<Int, QuestionEntity>     // 페이징 설정 및 불러온 페이지
    ): MediatorResult {                             // 리턴값 : DB 갱ㅇ신 후 결과에 따른 성공 또는 실패, 더 불러올 데이터가 있는지 여부 반환
        val pageSize = state.config.pageSize
        val today = LocalDate.now()

        val fromDate = when (loadType) {
            // 모든 데이터를 삭제하고 오늘 날짜의 데이터부터 받기
            LoadType.REFRESH -> {
                today
            }
            // 목록의 앞부분 데이터를 불러오기.
            LoadType.PREPEND -> {
                val firstItem = state.firstItemOrNull()     // 이전에 불러온 데이터의 가장 앞부분

                // endOfPaginationReached 가 false or true 인경우에는 더이상 데이터를 불러올 필요가 없다는 뜻.
                // endOfPaginationReached = true : 더 이상 불러올 데이터가 없음을 의미 -> load() 메서드를 더이상 호출하지 않겠다.

                // 처음 목록을 불러오는 경우.
                if (firstItem == null) {
                    return MediatorResult.Success(endOfPaginationReached = false)
                }
                // 이미 모두 DB에 있는 경우
                if (firstItem.id >= today) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                } else {
                    firstItem.id.plusDays(pageSize.toLong())
                    val prevKey = firstItem.id
                    if (prevKey > today) {
                        today
                    } else {
                        prevKey
                    }

                }
            }
            // 목록의 다음 데이터를 불러오기. (하루전의 날짜를 fromDate로 사용)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) {
                    today
                } else {
                    lastItem.id.minusDays(1)
                }
            }
        }

        try {
            val questions = api.getQuestions(fromDate, pageSize).body()
            val endOfPaginationReached = questions.isNullOrEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.getQuestionDao().deleteAll()
                }

                questions?.map {
                    QuestionEntity(it.id, it.text, it.answerCount, it.updatedAt, it.createdAt)
                }?.let {
                    db.getQuestionDao().insertOrReplace(it)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException){
            return MediatorResult.Error(e)
        } catch (e: SocketTimeoutException){
            return MediatorResult.Error(e)
        } catch (e: HttpException){
            return MediatorResult.Error(e)
        }
    }
}