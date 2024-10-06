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
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.chat.components.ChatBottomBar
import com.example.ollamaui.ui.screen.chat.components.ChatTopBar
import com.example.ollamaui.ui.screen.chat.components.Conversation
import com.example.ollamaui.ui.screen.common.CustomButton

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatState: ChatStates,
    onBackClick: () -> Unit
) {

    var textValue by rememberSaveable { mutableStateOf("") }
    var textValueBackup by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChatTopBar(
                chatTitle = chatState.chatModel.chatTitle,
                chatIcon = chatState.chatModel.chatIcon,
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
                    textValueBackup = textValue
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
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                )
        ) {
            Conversation(
                messageModel = chatState.chatModel.chatMessages ,
                modifier = Modifier.weight(1f)
            )
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.height(20.dp)
            ) {
                AnimatedVisibility(visible = chatState.isResponding) {
                    LinearProgressIndicator(modifier = Modifier.width(30.dp))
                }
                AnimatedVisibility(visible = chatState.chatError != null) {
                    CustomButton(
                        description = "Resend Message",
                        onButtonClick = { chatViewModel.sendButton(textValueBackup)},
                        icon = R.drawable.baseline_refresh_24,
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        BackHandler {
            onBackClick()
        }
    }
}