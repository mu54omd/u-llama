package com.example.ollamaui.ui.common

fun filterUserMessage(userMessage: String):String?{
    val regex = Regex("Using this data: \\{(\n*.*)\\}. Respond to this prompt: \\{(\n*.*)\\}.")
    val match = regex.find(userMessage)
    return if(match != null) match.groups[2]?.value else null
}