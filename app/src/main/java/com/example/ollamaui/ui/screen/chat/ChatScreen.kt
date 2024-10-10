package com.example.ollamaui.ui.screen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.ui.screen.chat.components.ChatBottomBar
import com.example.ollamaui.ui.screen.chat.components.ChatTopBar
import com.example.ollamaui.ui.screen.chat.components.Conversation
import com.example.ollamaui.ui.screen.chat.components.DotsPulsing

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatState: ChatStates,
    onBackClick: () -> Unit
) {
    var textValue by rememberSaveable { mutableStateOf("") }
    var textValueBackup by rememberSaveable { mutableStateOf("") }
    val selectedDialogs = remember { mutableStateListOf<MessageModel>() }
    val visibleDetails = remember { mutableStateListOf<MessageModel>() }
    Scaffold(
        topBar = {
            ChatTopBar(
                botName = chatState.chatModel.botName,
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
                    if(chatState.isSendingFailed){
                        chatViewModel.retry()
                    }else {
                        chatViewModel.sendButton(textValue)
                        textValue = ""
                        textValueBackup = textValue
                    }
                },
                onClearClick = { textValue = "" },
                onAttachClick = {},
                isModelSelected = chatState.chatModel.modelName != "",
                isSendingFailed = chatState.isSendingFailed
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
                messagesModel = chatState.chatModel.chatMessages ,
                modifier = Modifier.weight(1f),
                botName = chatState.chatModel.botName,
                userName = chatState.chatModel.userName,
                onItemClick = {
                    if(selectedDialogs.isEmpty()) {
                        if(visibleDetails.contains(it)){
                            visibleDetails.remove(it)
                        }else{
                            visibleDetails.add(it)
                        }
                    }else{
                        if(selectedDialogs.contains(it)) {
                            selectedDialogs.remove(it)
                        }else {
                            selectedDialogs.add(it)
                        }
                }
                              },
                onSelectedItemClick = { selectedDialogs.remove(it) },
                onLongPressItem = { if(selectedDialogs.contains(it)) selectedDialogs.remove(it) else selectedDialogs.add(it) },
                isSelected = { selectedDialogs.contains(it) },
                isVisible = { visibleDetails.contains(it) }
            )
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.height(20.dp)
            ) {
                AnimatedVisibility(visible = chatState.isRespondingList.contains(chatState.chatModel.chatId)) {
                    DotsPulsing()
                }
                AnimatedVisibility(visible = chatState.isSendingFailed) {
                    Text(text = "Try again!")
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        BackHandler {
            onBackClick()
        }
    }
}