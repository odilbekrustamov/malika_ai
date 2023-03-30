package com.iq_academy.malika_ai.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.ChatGPTResponse
import com.iq_academy.malika_ai.model.room.Chat
import com.iq_academy.malika_ai.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImp @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel(),
    HomeViewModel {
    override fun getAllchats(block: (Result<List<Chat>>) -> Unit) {
        viewModelScope.launch {
            try {
                block(Result.success(mainRepository.getAllchats()))
            } catch (e: Exception) {
                block(Result.failure(e))
            }
        }
    }

    override fun getSendSMS(
        chatGPTRequest: ChatGPTRequest,
        block: (Result<ChatGPTResponse>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                block(Result.success(mainRepository.getQuestion(chatGPTRequest)))
            } catch (e: Exception) {
                block(Result.failure(e))
            }
        }
    }

    override fun insertChat(chat: Chat, block: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                block(Result.success(mainRepository.insertChatToDB(chat)))
            } catch (e: Exception) {
                block(Result.failure(e))
            }
        }
    }
}