package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.MessageModel

@Composable
fun Conversation(
    messageModel: MessageModel,
    isResponding: Boolean,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messageModel.messages.size) {
        listState.animateScrollToItem(messageModel.messages.size)
    }
    LazyColumn(
        modifier = modifier.padding(bottom = 10.dp),
        state = listState,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
    ) {
        items(messageModel.messages){ message ->
            ChatDialog(message)

        }
    }
    AnimatedVisibility(visible = isResponding) {
        LinearProgressIndicator(modifier = Modifier.width(25.dp))
    }
    Spacer(modifier = Modifier.height(10.dp))
}