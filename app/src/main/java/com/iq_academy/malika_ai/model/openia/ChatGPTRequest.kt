package com.iq_academy.malika_ai.model.openia

import com.google.gson.annotations.SerializedName

data class ChatGPTRequest(

    @SerializedName("model") var model: String? = null,
    @SerializedName("messages") var messages: ArrayList<Message> = arrayListOf()

)


data class Messages(

    @SerializedName("role") var role: String? = null,
    @SerializedName("content") var content: String? = null

)