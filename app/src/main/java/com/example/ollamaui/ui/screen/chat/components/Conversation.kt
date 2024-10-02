package com.example.ollamaui.ui.screen.chat.components

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
import com.example.ollamaui.domain.model.MessageModel

@Composable
fun Conversation(
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messageModel.messages.size) {
        listState.animateScrollToItem(messageModel.messages.size)
    }
    LazyColumn(
        modifier = modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.Bottom,
        state = listState,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
    ) {
        items(messageModel.messages){ message ->
            ChatDialog(message)
        }
    }
}