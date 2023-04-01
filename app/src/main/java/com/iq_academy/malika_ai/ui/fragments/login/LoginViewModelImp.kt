package com.iq_academy.malika_ai.ui.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iq_academy.malika_ai.model.verify.SendSMSRequest
import com.iq_academy.malika_ai.model.verify.SendSMSResponse
import com.iq_academy.malika_ai.model.verify.VerifySMSRequest
import com.iq_academy.malika_ai.model.verify.VerifySMSResponse
import com.iq_academy.malika_ai.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
@HiltViewModel
class LoginViewModelImp @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel(),
    LoginViewModel {

    override fun sendSMS(sendSMSRequest: SendSMSRequest, block: (Result<SendSMSResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                block(Result.success(authRepository.sendSMS(sendSMSRequest)))
            } catch (e: Exception) {
                block(Result.failure(e))
            }
        }
    }

    override fun verifySMS(
        verifySMSRequest: VerifySMSRequest,
        block: (Result<VerifySMSResponse>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                block(Result.success(authRepository.verifySMS(verifySMSRequest)))
            } catch (e: Exception) {
                block(Result.failure(e))
            }
        }
    }
}