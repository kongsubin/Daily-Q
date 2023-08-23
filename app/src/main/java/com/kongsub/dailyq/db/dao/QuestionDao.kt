package com.kongsub.dailyq.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kongsub.dailyq.db.entity.QuestionEntity

@Dao
interface QuestionDao {
    // 서버의 정보는 로컬의 정보보다 최신이므로, 기본키의 충돌이 발생할 경우 서버의 데이터로 업데이트 한다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(questions: List<QuestionEntity>)

    @Query("SELECT * FROM question ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, QuestionEntity>

    @Query("DELETE FROM question")
    suspend fun deleteAll()
}