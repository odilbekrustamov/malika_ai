package com.iq_academy.malika_ai.ui.fragments.login

import com.iq_academy.malika_ai.model.verify.SendSMSRequest
import com.iq_academy.malika_ai.model.verify.SendSMSResponse
import com.iq_academy.malika_ai.model.verify.VerifySMSRequest
import com.iq_academy.malika_ai.model.verify.VerifySMSResponse

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
interface LoginViewModel {

    fun sendSMS(sendSMSRequest: SendSMSRequest, block: (Result<SendSMSResponse>) -> Unit)

    fun verifySMS(verifySMSRequest: VerifySMSRequest, block: (Result<VerifySMSResponse>) -> Unit)

}