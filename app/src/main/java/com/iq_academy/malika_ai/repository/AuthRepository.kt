package com.iq_academy.malika_ai.repository

import com.iq_academy.malika_ai.model.verify.SendSMSRequest
import com.iq_academy.malika_ai.model.verify.VerifySMSRequest
import com.iq_academy.malika_ai.module.service.ApiService2
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
class AuthRepository @Inject constructor(private val apiService: ApiService2) {

    suspend fun sendSMS(sendSMSRequest: SendSMSRequest) = apiService.sendSMS(sendSMSRequest)

    suspend fun verifySMS(verifySMSRequest: VerifySMSRequest) =
        apiService.verifySMS(verifySMSRequest)

}