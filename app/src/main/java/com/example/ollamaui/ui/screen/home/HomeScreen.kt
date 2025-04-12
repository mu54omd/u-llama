package com.example.ollamaui.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.helper.NetworkStatus
import com.example.ollamaui.ui.common.printLastMessage
import com.example.ollamaui.ui.screen.home.components.ChatListItem
import com.example.ollamaui.ui.screen.home.components.CustomFabButton
import com.example.ollamaui.ui.screen.home.components.HomeTopBar
import com.example.ollamaui.ui.screen.home.components.NewChatModal
import com.example.ollamaui.ui.screen.home.components.SwipeActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onChatClick: (ChatModel) -> Unit,
    onSettingClick: () -> Unit,
    onFileManagerClick: () -> Unit,
    onLogClick: () -> Unit,
    onAddNewChatClick: (String, String, String) -> Unit,
    onDeleteChatByIdClick: (Int) -> Unit,
    onDeleteChatClick: (ChatModel) -> Unit,
    onRefreshClick: () -> Unit,
    chatsList: State<List<ChatModel>>,
    networkStatus: NetworkStatus,
    isChatReady: Boolean,
    modelList: List<String>,
    onBackClick: (Int) -> Int,
) {
    var isNewChatDialogVisible by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var botName by remember { mutableStateOf("") }
    var chatTitle by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    val selectedChats = remember { mutableStateListOf<Int>() }
    var selectedModel by remember { mutableStateOf("") }
    val isSelectedChatsEmpty by remember(selectedChats) { derivedStateOf { selectedChats.isEmpty() } }
    val maxChar = 25
    var isRevealed by remember { mutableIntStateOf(-1) }
    var backHandlerCounter by remember { mutableIntStateOf(0) }
    val chatListItems by remember { derivedStateOf { chatsList.value }}
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
                    HomeTopBar(
                        onSettingClick = { onSettingClick() },
                        onFileManagerClick = { onFileManagerClick() },
                        onLogClick = { onLogClick() },
                        onDeleteClick = {
                            selectedChats.forEach{ selectedChat ->
                                onDeleteChatByIdClick(selectedChat)
                            }
                            selectedChats.clear()
                        },
                        onSelectClick = {
                            isRevealed = -1
                            chatsList.value.forEach { chat ->
                                selectedChats.add(chat.chatId)
                            }
                        },
                        onDeselectClick = {
                            selectedChats.clear()
                        },
                        isSelectedChatsEmpty = isSelectedChatsEmpty,
                        chatsListSize = chatsList.value.size,
                        networkStatus = networkStatus
                    )
                 },
        bottomBar = {},
        floatingActionButton = {
            CustomFabButton(
                isModelListLoaded = isChatReady,
                isFabVisible = !isNewChatDialogVisible,
                onButtonClick = {
                    userName = ""
                    chatTitle = ""
                    botName = ""
                    systemPrompt = ""
                    if(!isChatReady){
                        onRefreshClick()
                    }
                    else {
                        isNewChatDialogVisible = true
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            AnimatedVisibility(
                visible = isNewChatDialogVisible,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                NewChatModal(
                    systemPrompt = systemPrompt,
                    maxChar = maxChar,
                    chatTitle = chatTitle,
                    onChatTitleChange = { if(it.length<=maxChar) chatTitle = it },
                    onCloseClick = { isNewChatDialogVisible = false},
                    onAcceptClick = {
                        onAddNewChatClick(chatTitle, systemPrompt, selectedModel)
                        isNewChatDialogVisible = false
                    },
                    onSystemPromptChange = { if(it.length<=maxChar*6) systemPrompt = it},
                    modelList = modelList,
                    onModelClick = { model -> selectedModel = model},
                )
            }
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopCenter),
            ) {
                    items(
                        items = chatListItems,
                        key = { chatItem -> chatItem.chatId }
                    ) { chatItem ->
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().animateItem()) {
                                SwipeActions(
                                    onDeleteClick = {
                                        onDeleteChatClick(chatItem)
                                    },
                                    isSelected = selectedChats.contains(chatItem.chatId)
                                )
                                ChatListItem(
                                    modelName = chatItem.modelName,
                                    chatTitle = chatItem.chatTitle,
                                    lastMessage = printLastMessage(chatItem.chatMessages),
                                    onItemClick = {
                                        when {
                                            selectedChats.contains(chatItem.chatId) -> {
                                                selectedChats.remove(chatItem.chatId)
                                            }

                                            isRevealed != -1 -> {
                                                isRevealed = -1
                                            }

                                            else -> {

                                                if (selectedChats.isEmpty()) {
                                                    onChatClick(chatItem)
                                                } else {
                                                    if (selectedChats.contains(chatItem.chatId)) {
                                                        selectedChats.remove(chatItem.chatId)
                                                    } else {
                                                        selectedChats.add(chatItem.chatId)
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    onItemLongPress = {
                                        when {
                                            isRevealed != -1 -> {
                                                isRevealed = -1
                                            }

                                            else -> {
                                                if (!selectedChats.contains(chatItem.chatId)) {
                                                    selectedChats.add(chatItem.chatId)
                                                }
                                            }
                                        }
                                    },
                                    onSelectedItemClick = { selectedChats.remove(chatItem.chatId) },
                                    isSelected = selectedChats.contains(chatItem.chatId),
                                    isNewMessageReceived = chatItem.newMessageStatus != 0,
                                    newMessageStatus = chatItem.newMessageStatus,
                                    cardOffset = 120f,
                                    onExpand = {
                                        if (selectedChats.isEmpty())
                                            isRevealed = chatItem.chatId
                                    },
                                    onCollapse = { isRevealed = -1 },
                                    isRevealed = isRevealed == chatItem.chatId
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(start = 30.dp, end = 30.dp))
                        }
                    }
                }
            }
        }
    BackHandler {
        if(selectedChats.isEmpty()) {
            backHandlerCounter++
            val result = onBackClick(backHandlerCounter)
            if(result == 1) {
                scope.launch {
                    delay(3000L)
                    backHandlerCounter = 0
                }
            }else{
                backHandlerCounter = 0
            }
        }else{
            selectedChats.clear()
        }
    }
}
