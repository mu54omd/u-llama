package com.example.ollamaui.ui.common

import com.example.ollamaui.domain.model.MessageModel

fun messageModelToText(input: List<MessageModel>): String{
    var result = ""
    input.forEach { message ->
        result += ("\"${message.role}\": ${message.content}\n")
    }
    return result
}