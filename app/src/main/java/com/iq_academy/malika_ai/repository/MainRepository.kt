package com.iq_academy.malika_ai.repository

import com.iq_academy.malika_ai.database.ChatDao
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.room.Chat
import com.iq_academy.malika_ai.module.service.ApiService1
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
class MainRepository @Inject constructor(
    private val chatDao: ChatDao,
    private val apiService: ApiService1
) {

    /**
     * Retrofit API
     */

    suspend fun getQuestion(chatGPTRequest: ChatGPTRequest) = apiService.getQuestion(chatGPTRequest)

    /**
     * Room
     */

    suspend fun insertChatToDB(chat: Chat) = chatDao.insertChatToDB(chat)

    suspend fun getAllchats() = chatDao.getAllchats()

    suspend fun deleteAllChats() = chatDao.deleteAllChats()

}