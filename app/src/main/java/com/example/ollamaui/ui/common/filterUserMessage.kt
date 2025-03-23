package com.example.ollamaui.ui.common

fun filterUserMessage(userMessage: String):String?{
    val regex = Regex("Using this data:\\s*\\{([\\s\\S]*?)\\}\\s*\\.\\s*Respond to this prompt:\\s*\\{([\\s\\S]*?)\\}\\s*\\.")
    val match = regex.find(userMessage)
    return match?.groups?.get(2)?.value
}