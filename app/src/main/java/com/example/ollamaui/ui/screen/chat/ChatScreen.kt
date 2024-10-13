package com.example.ollamaui.ui.screen.chat

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.ui.common.messageModelToText
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
    val clipboard: ClipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            ChatTopBar(
                botName = chatState.chatModel.botName,
                chatTitle = chatState.chatModel.chatTitle,
                chatIcon = chatState.chatModel.chatIcon,
                onBackClick = onBackClick,
                onCopyClick = {
                    Log.d("cTAG", "$selectedDialogs")
                    clipboard.setText(AnnotatedString(text = messageModelToText(selectedDialogs)))
                    Log.d("cTAG", clipboard.toString())
                    selectedDialogs.clear()
                              },
                isCopyButtonEnabled = selectedDialogs.isNotEmpty()
            )
                 },
        bottomBar = {
            ChatBottomBar(
                textValue = textValue,
                onValueChange = { textValue = it },
                onSendClick = {
                    when{
                     chatState.isSendingFailed -> { chatViewModel.retry() }
                     chatState.isRespondingList.contains(chatState.chatModel.chatId) -> { chatViewModel.stop(chatId = chatState.chatModel.chatId) }
                     else -> {
                         chatViewModel.sendButton(textValue)
                         textValue = ""
                         textValueBackup = textValue
                     }
                    }
                },
                onClearClick = { textValue = "" },
                onAttachClick = {},
                isModelSelected = chatState.chatModel.modelName != "",
                isSendingFailed = chatState.isSendingFailed,
                isResponding = chatState.isRespondingList.contains(chatState.chatModel.chatId)
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
                AnimatedVisibility(visible = chatState.isRespondingList.contains(chatState.chatModel.chatId) && !chatState.isSendingFailed) {
                    DotsPulsing()
                }
                AnimatedVisibility(visible = chatState.isSendingFailed) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Try again!")
                        Text(text = " OR ")
                        Box(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(100))
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(2.dp)
                                .clickable {
                                    textValue = chatState.chatModel.chatMessages.messageModels.last().content
                                    chatViewModel.removeLastDialogFromDatabase()
                                }
                        ) {
                            Text(text = "Edit", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(text = " OR ")
                        Box(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(100))
                                .background(color = MaterialTheme.colorScheme.errorContainer)
                                .padding(2.dp)
                                .clickable { chatViewModel.removeLastDialogFromDatabase() }
                        ) {
                            Text(text = "Delete", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onErrorContainer))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        BackHandler {
            onBackClick()
        }
    }
}