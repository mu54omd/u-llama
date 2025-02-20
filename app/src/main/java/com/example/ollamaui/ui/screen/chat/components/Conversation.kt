package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE

@Composable
fun Conversation(
    messagesModel: MessagesModel,
    modifier: Modifier = Modifier,
    isSelected: (Int, MessageModel) -> Boolean,
    isVisible: (Int, MessageModel) -> Boolean,
    onItemClick: (Int, MessageModel) -> Unit,
    onLongPressItem: (Int, MessageModel) -> Unit,
    onSelectedItemClick: (Int, MessageModel) -> Unit,
    listState: LazyListState,
) {
    LaunchedEffect(messagesModel.messageModels.size) {
        listState.scrollToItem(index = messagesModel.messageModels.lastIndex)
    }
    LazyColumn(
        modifier = modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.Bottom,
        state = listState,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
    ) {
        itemsIndexed(
            items = messagesModel.messageModels,
            key = { _, item ->  item.messageId }
        ){ index, message ->
            if (message.role != SYSTEM_ROLE) {
                ChatDialog(
                    messageModel = message,
                    modifier = Modifier.animateItem(),
                    isSelected = isSelected(index, message),
                    isVisible = isVisible(index, message),
                    onLongPressItem = { onLongPressItem(index, message) },
                    onItemClick = { onItemClick(index, message) },
                    onSelectedItemClick = { onSelectedItemClick(index, message) },
                )
            }
        }
    }
}