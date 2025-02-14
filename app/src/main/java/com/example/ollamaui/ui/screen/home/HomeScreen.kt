package com.example.ollamaui.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.ui.screen.home.components.CustomFabButton
import com.example.ollamaui.ui.screen.home.components.HomeTopBar
import com.example.ollamaui.ui.screen.home.components.NewChatItem
import com.example.ollamaui.ui.screen.home.components.NewChatModal
import com.example.ollamaui.ui.screen.home.components.SwipeActions

@Composable
fun HomeScreen(
    onChatClick: (ChatModel) -> Unit,
    onSettingClick: () -> Unit,
    onLogClick: () -> Unit,
    homeViewModel: HomeViewModel,
    onRefreshClick: () -> Unit,
    chatsList: ChatsList,
    isChatReady: Boolean,
    modelList: List<String>,
) {
    var isNewChatDialogVisible by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var botName by remember { mutableStateOf("") }
    var chatTitle by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    val selectedChats = remember { mutableStateListOf<Int>() }
    var selectedModel by remember { mutableStateOf("") }
    val isSelectedChatsEmpty by remember(selectedChats) { derivedStateOf { selectedChats.isEmpty() } }
//    val activity = (LocalContext.current as? Activity)
    val maxChar = 25
    var isRevealed by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
                    HomeTopBar(
                        onSettingClick = { onSettingClick() },
                        onLogClick = { onLogClick() },
                        onDeleteClick = {
                            selectedChats.forEach{ selectedChat ->
                                homeViewModel.deleteChatById(selectedChat)
                            }
                            selectedChats.clear()
                        },
                        onSelectClick = {
                            isRevealed = -1
                            chatsList.items.forEach { chat ->
                                selectedChats.add(chat.chatId)
                            }
                        },
                        onDeselectClick = {
                            selectedChats.clear()
                        },
                        isSelectedChatsEmpty = isSelectedChatsEmpty,
                        chatsListSize = chatsList.items.size
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
                        homeViewModel.addNewChat(
                            chatTitle = chatTitle,
                            selectedModel = selectedModel,
                            systemPrompt = systemPrompt
                            )
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
                        items = chatsList.items,
                        key = { chatItem -> chatItem.chatId }
                    ) { chatItem ->
                        Box(modifier = Modifier.fillMaxWidth().animateItem()) {
                            SwipeActions(
                                onDeleteClick = {
                                    homeViewModel.deleteChat(chatItem)
                                },
                                isSelected = selectedChats.contains(chatItem.chatId)
                            )
                            NewChatItem(
                                modelName = chatItem.modelName,
                                chatTitle = chatItem.chatTitle,
                                onItemClick = {
                                    when {
                                        selectedChats.contains(chatItem.chatId) -> {
                                            selectedChats.remove(chatItem.chatId)
                                        }
                                        isRevealed != -1 -> { isRevealed = -1}
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
                                    when{
                                        isRevealed != -1 -> { isRevealed = -1}
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
                                    if(selectedChats.isEmpty())
                                        isRevealed = chatItem.chatId
                                           },
                                onCollapse = { isRevealed = -1},
                                isRevealed = isRevealed == chatItem.chatId
                            )
                        }
                    }
                }
            }
        }
    }
