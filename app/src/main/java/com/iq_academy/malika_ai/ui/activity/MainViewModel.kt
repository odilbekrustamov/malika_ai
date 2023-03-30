package com.iq_academy.malika_ai.ui.activity

interface MainViewModel {

    fun deleteAllChats(block: (Result<Unit>) -> Unit)

}