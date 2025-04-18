package com.mu54omd.ullama.ui.common

import com.mu54omd.ullama.domain.model.MessageModel

fun messageModelToText(input: Map<Int, MessageModel>): String{
    var result = ""
    input.forEach { message ->
        result += ("\"${message.value.role}\": ${message.value.content}\n")
    }
    return result.removeSuffix("\n")
}