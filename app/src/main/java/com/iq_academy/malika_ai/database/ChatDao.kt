package com.iq_academy.malika_ai.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iq_academy.malika_ai.model.room.Chat

@Dao
interface ChatDao {

    @Insert
    suspend fun insertChatToDB(chat: Chat)

    @Query("SELECT * FROM call_table ORDER BY chatId DESC")
    suspend fun getAllchats(): List<Chat>

    @Query("DELETE FROM call_table")
    suspend fun deleteAllChats()

}