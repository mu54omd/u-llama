package com.mu54omd.ullama.ui.common

import com.mu54omd.ullama.domain.model.MessagesModel
import com.mu54omd.ullama.utils.Constants.SYSTEM_ROLE
import com.mu54omd.ullama.utils.Constants.USER_ROLE

fun printLastMessage(messagesModel: MessagesModel): String{
    return filterAssistantMessage(messagesModel.messageModels
        .lastOrNull { it.role != USER_ROLE && it.role != SYSTEM_ROLE }?.content ?: "").first ?: filterAssistantMessage(messagesModel.messageModels
        .lastOrNull { it.role != USER_ROLE && it.role != SYSTEM_ROLE }?.content ?: "").third!!
}