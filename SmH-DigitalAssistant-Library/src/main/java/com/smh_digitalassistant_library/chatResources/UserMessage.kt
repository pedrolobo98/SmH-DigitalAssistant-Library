package com.smh_digitalassistant_library.chatResources

class UserMessage {
    lateinit var sender:String
    lateinit var message: String
    fun UserMessage(id:String,response_message:String){
        sender = id
        message = response_message
    }
}