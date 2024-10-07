package com.example.ollamaui.ui.common

import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.utils.Constants.USER_ROLE

fun isFromMe(messageModel: MessageModel): Boolean{
    return messageModel.role == USER_ROLE
}