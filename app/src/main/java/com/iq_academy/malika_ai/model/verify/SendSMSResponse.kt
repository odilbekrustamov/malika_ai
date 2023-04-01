package com.iq_academy.malika_ai.model.verify

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

data class SendSMSResponse(
    val error: Any,
    val error_code: Int,
    val message: String
)