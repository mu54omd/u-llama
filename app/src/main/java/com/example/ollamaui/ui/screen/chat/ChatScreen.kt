package com.example.ollamaui.ui.screen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.screen.chat.components.ChatBottomBar
import com.example.ollamaui.ui.screen.chat.components.ChatTopBar
import com.example.ollamaui.ui.screen.chat.components.Conversation

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatState: ChatStates,
    onBackClick: () -> Unit
) {

    var textValue by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChatTopBar(
                chatTitle = chatState.chatModel.chatTitle,
                onBackClick = onBackClick
            )
                 },
        bottomBar = {
            ChatBottomBar(
                textValue = textValue,
                onValueChange = { textValue = it },
                onSendClick = {
                    chatViewModel.sendButton(textValue)
                    textValue = ""
                },
                onClearClick = { textValue = "" },
                onAttachClick = {},
                isModelSelected = chatState.chatModel.modelName != ""
            )
        }
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding()
            )
        ) {
            Conversation(
                messageModel = chatState.chatModel.chatMessages ,
            )
            androidx.compose.animation.AnimatedVisibility(visible = chatState.isResponding) {
                LinearProgressIndicator(modifier = Modifier.width(25.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        BackHandler {
            onBackClick()
        }
    }
}