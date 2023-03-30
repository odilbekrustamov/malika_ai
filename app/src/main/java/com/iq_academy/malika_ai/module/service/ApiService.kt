package com.iq_academy.malika_ai.module.service

import com.iq_academy.malika_ai.model.SendSMSRequest
import com.iq_academy.malika_ai.model.VerifySMSRequest
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.ChatGPTResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Named

interface ApiService1 {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("v1/chat/completions")
    @Named("Retrofit1")
    suspend fun getQuestion(@Body chatGPTRequest: ChatGPTRequest): ChatGPTResponse

}

interface ApiService2 {

    @POST("phone")
    @Named("Retrofit2")
    suspend fun sendSMS(sendSMSRequest: SendSMSRequest)

    @POST("phone")
    @Named("Retrofit2")
    suspend fun verifySMS(verifySMSRequest: VerifySMSRequest)


}