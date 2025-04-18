package com.mu54omd.ullama.ui.common

import com.mu54omd.ullama.domain.model.MessageModel
import com.mu54omd.ullama.utils.Constants.USER_ROLE

fun isFromMe(messageModel: MessageModel): Boolean{
    return messageModel.role == USER_ROLE
}