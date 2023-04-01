package com.iq_academy.malika_ai.ui.fragments.home

import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.ChatGPTResponse
import com.iq_academy.malika_ai.model.room.Chat

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
interface HomeViewModel {

    fun getAllchats(block: (Result<List<Chat>>) -> Unit)

    fun getSendSMS(chatGPTRequest: ChatGPTRequest, block: (Result<ChatGPTResponse>) -> Unit)

    fun insertChat(chat: Chat, block: (Result<Unit>) -> Unit)


}