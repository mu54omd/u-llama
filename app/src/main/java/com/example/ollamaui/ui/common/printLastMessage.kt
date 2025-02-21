package com.example.ollamaui.ui.common

import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE
import com.example.ollamaui.utils.Constants.USER_ROLE

fun printLastMessage(messagesModel: MessagesModel): String{
    return filterAssistantMessage(messagesModel.messageModels
        .lastOrNull { it.role != USER_ROLE && it.role != SYSTEM_ROLE }?.content ?: "").first ?: filterAssistantMessage(messagesModel.messageModels
        .lastOrNull { it.role != USER_ROLE && it.role != SYSTEM_ROLE }?.content ?: "").third!!
}