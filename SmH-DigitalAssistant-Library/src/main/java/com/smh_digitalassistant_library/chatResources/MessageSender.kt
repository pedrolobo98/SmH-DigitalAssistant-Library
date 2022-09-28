package com.smh_digitalassistant_library.chatResources

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageSender {
    @POST("webhook")
    fun sendMessage(@Body userMessage: UserMessage?): Call<List<BotResponse>>
}