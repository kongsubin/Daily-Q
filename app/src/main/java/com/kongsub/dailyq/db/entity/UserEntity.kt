package com.kongsub.dailyq.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    var id: String,
    var name: String?,
    var description: String?,
    var photo: String?,
    @ColumnInfo(name="answer_count", defaultValue = "0")
    var answerCount: Int,
    var followerCount: Int,
    var followingCount: Int,
    var isFollowing: Boolean,
    @ColumnInfo(name="created_at", defaultValue = "CURRENT_TIMESTAMP")
    var updatedAt: Date?
)
