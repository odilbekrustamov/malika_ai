package com.iq_academy.malika_ai.repository

import com.iq_academy.malika_ai.model.SendSMSRequest
import com.iq_academy.malika_ai.model.VerifySMSRequest
import com.iq_academy.malika_ai.module.service.ApiService2
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService2) {

    suspend fun sendSMS(sendSMSRequest: SendSMSRequest) = apiService.sendSMS(sendSMSRequest)

    suspend fun verifySMS(verifySMSRequest: VerifySMSRequest) =
        apiService.verifySMS(verifySMSRequest)

}