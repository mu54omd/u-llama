package com.mu54omd.ullama.ui.common

fun filterAssistantMessage(assistantMessage: String): Triple<String?,String?, String?>{
    val thinking = assistantMessage.split("</think>")
    return if(thinking.size == 1){
        Triple(first = assistantMessage, second = null, third = null)
    }else {
        Triple(
            first = null,
            second = thinking[0].substring(8).trimIndent(),
            third = thinking[1].trimIndent()
        )
    }
}