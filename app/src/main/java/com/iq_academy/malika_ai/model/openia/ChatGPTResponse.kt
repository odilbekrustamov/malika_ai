package com.iq_academy.malika_ai.model.openia

import com.google.gson.annotations.SerializedName

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
data class ChatGPTResponse(

    @SerializedName("id") var id: String? = null,
    @SerializedName("object") var objec: String? = null,
    @SerializedName("created") var created: Int? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("usage") var usage: Usage? = Usage(),
    @SerializedName("choices") var choices: ArrayList<Choices> = arrayListOf()

)

data class Usage(

    @SerializedName("prompt_tokens") var promptTokens: Int? = null,
    @SerializedName("completion_tokens") var completionTokens: Int? = null,
    @SerializedName("total_tokens") var totalTokens: Int? = null

)

data class Message(

    @SerializedName("role") var role: String? = null,
    @SerializedName("content") var content: String? = null

)

data class Choices(

    @SerializedName("message") var message: Message? = Message(),
    @SerializedName("finish_reason") var finishReason: String? = null,
    @SerializedName("index") var index: Int? = null

)