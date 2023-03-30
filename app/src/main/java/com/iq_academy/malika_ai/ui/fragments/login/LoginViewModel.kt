package com.iq_academy.malika_ai.ui.fragments.login

import com.iq_academy.malika_ai.model.SendSMSRequest
import com.iq_academy.malika_ai.model.VerifySMSRequest

interface LoginViewModel {

    fun sendSMS(sendSMSRequest: SendSMSRequest, block: (Result<Unit>) -> Unit)

    fun verifySMS(verifySMSRequest: VerifySMSRequest, block: (Result<Unit>) -> Unit)

}