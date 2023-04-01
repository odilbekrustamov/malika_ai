package com.iq_academy.malika_ai.module.service

import com.iq_academy.malika_ai.model.verify.SendSMSRequest
import com.iq_academy.malika_ai.model.verify.SendSMSResponse
import com.iq_academy.malika_ai.model.verify.VerifySMSResponse
import com.iq_academy.malika_ai.model.verify.VerifySMSRequest
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.ChatGPTResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Named
/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
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

    @Headers(
        "Content-Type: application/json"
    )
    @POST("phone")
    suspend fun sendSMS(@Body sendSMSRequest: SendSMSRequest): SendSMSResponse

    @POST("verify")
    suspend fun verifySMS(@Body verifySMSRequest: VerifySMSRequest): VerifySMSResponse


}