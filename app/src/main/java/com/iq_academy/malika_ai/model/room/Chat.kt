package com.iq_academy.malika_ai.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_table")
data class Chat(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "chatId") var chatId: Int? = null,
    @ColumnInfo(name = "user_question") var userQuestion: String? = null,
    @ColumnInfo(name = "ai_answer") var aiAnswer: String? = null,
    @ColumnInfo(name = "user_time") var userTime: String? = null,
    @ColumnInfo(name = "ai_time") var aiTime: String? = null,
)