package com.example.ollamaui.ui.common

import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.utils.Constants.USER_ID

fun isFromMe(messageModel: MessageModel): Boolean{
    return messageModel.author.id == USER_ID
}