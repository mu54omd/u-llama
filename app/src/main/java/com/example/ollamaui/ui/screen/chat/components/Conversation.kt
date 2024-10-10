package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE

@Composable
fun Conversation(
    botName: String,
    userName: String,
    messagesModel: MessagesModel,
    modifier: Modifier = Modifier,
    isSelected: (MessageModel) -> Boolean,
    isVisible: (MessageModel) -> Boolean,
    onItemClick: (MessageModel) -> Unit,
    onLongPressItem: (MessageModel) -> Unit,
    onSelectedItemClick: (MessageModel) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messagesModel.messageModels.size) {
        listState.animateScrollToItem(messagesModel.messageModels.size)
    }
    LazyColumn(
        modifier = modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.Bottom,
        state = listState,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
    ) {
        items(
            items = messagesModel.messageModels,
        ){ message ->
            if (message.role != SYSTEM_ROLE) {
                ChatDialog(
                    messageModel = message,
                    modifier = Modifier.animateItem(),
                    botName = botName,
                    userName = userName,
                    isSelected = isSelected(message),
                    isVisible = isVisible(message),
                    onLongPressItem = { onLongPressItem(message) },
                    onItemClick = { onItemClick(message) },
                    onSelectedItemClick = { onSelectedItemClick(message) },
                )
            }
        }
    }
}