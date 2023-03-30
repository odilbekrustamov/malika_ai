package com.iq_academy.malika_ai.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iq_academy.malika_ai.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModelImp @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel(),
    MainViewModel {
    override fun deleteAllChats(block: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                block(Result.success(mainRepository.deleteAllChats()))
            } catch (e: Exception) {
                block(Result.failure(e))
            }
        }
    }
}