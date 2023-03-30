package com.iq_academy.malika_ai.model

data class VerifySMSRequest(
    val code: String,
    val phone: String
)