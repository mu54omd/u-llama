package com.example.ollamaui.ui.common

import com.example.ollamaui.domain.model.MessageModel

fun messageModelToText(input: Map<Int, MessageModel>): String{
    var result = ""
    input.forEach { message ->
        result += ("\"${message.value.role}\": ${message.value.content}\n")
    }
    return result.removeSuffix("\n")
}