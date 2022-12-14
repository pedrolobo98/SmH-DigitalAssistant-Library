package com.smh_digitalassistant_library.chatResources

class BotResponse(
    var recipient_id: String,
    var text: String,
    var buttons: List<Buttons>
){
    inner class Buttons(var payload: String, var title: String)
}