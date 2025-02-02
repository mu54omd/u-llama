package com.example.ollamaui.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
import com.example.ollamaui.R
import com.example.ollamaui.activity.MainViewModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatModel
import com.example.ollamaui.ui.screen.home.components.AboutDialog
import com.example.ollamaui.ui.screen.home.components.CustomFabButton
import com.example.ollamaui.ui.screen.home.components.DeleteDialog
import com.example.ollamaui.ui.screen.home.components.HomeTopBar
import com.example.ollamaui.ui.screen.home.components.NewChatDialog
import com.example.ollamaui.ui.screen.home.components.NewChatItem

@Composable
fun HomeScreen(
    onChatClick: (ChatModel) -> Unit,
    onSettingClick: () -> Unit,
    homeViewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    chatsList: ChatsList,
    isModelListLoaded: Boolean,
    modelList: List<String>,
) {
    var isNewChatDialogVisible by remember { mutableStateOf(false) }
    var isAboutDialogVisible by remember { mutableStateOf(false) }
    var isDeleteDialogVisible by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }
    var botName by remember { mutableStateOf("") }
    var chatTitle by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }

    var selectedChat by remember { mutableStateOf(EmptyChatModel.empty) }
    val selectedChats = remember { mutableStateListOf<Int>() }
    var selectedModel by remember { mutableStateOf("") }
    val isSelectedChatsEmpty by remember(selectedChats) { derivedStateOf { selectedChats.isEmpty() } }
//    val activity = (LocalContext.current as? Activity)

    val maxChar = 25

    val avatarList = listOf(
        R.drawable.avatar_man_01, R.drawable.avatar_man_02, R.drawable.avatar_man_03, R.drawable.avatar_man_04,
        R.drawable.avatar_woman_01, R.drawable.avatar_woman_02, R.drawable.avatar_woman_03, R.drawable.avatar_woman_04
    )
    var selectedAvatar by remember { mutableIntStateOf(avatarList.random()) }

    Scaffold(
        topBar = {
                    HomeTopBar(
                        onSettingClick = { onSettingClick() },
                        onAboutClick = {
                            isAboutDialogVisible = true
                        },
                        onDeleteClick = {
                            selectedChats.forEach{ selectedChat ->
                                homeViewModel.deleteChatById(selectedChat)
                            }
                            selectedChats.clear()
                        },
                        onSelectClick = {
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
        bottomBar = { },
        floatingActionButton = {
            CustomFabButton(
                isModelListLoaded = isModelListLoaded,
                isFabVisible = !isNewChatDialogVisible,
                onButtonClick = {
                    userName = ""
                    chatTitle = ""
                    botName = ""
                    systemPrompt = ""
                    if(!isModelListLoaded){
                        mainViewModel.refresh()
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
                NewChatDialog(
                    userName = userName,
                    botName = botName,
                    systemPrompt = systemPrompt,
                    maxChar = maxChar,
                    onUserNameChange = { if(it.length<=maxChar) userName = it },
                    chatTitle = chatTitle,
                    onChatTitleChange = { if(it.length<=maxChar) chatTitle = it },
                    onCloseClick = { isNewChatDialogVisible = false},
                    onAcceptClick = {
                        homeViewModel.addNewChat(
                            chatTitle = chatTitle,
                            userName = userName,
                            botName = botName,
                            chatIcon = selectedAvatar,
                            selectedModel = selectedModel,
                            systemPrompt = systemPrompt
                            )
                        isNewChatDialogVisible = false
                    },
                    onBotNameChange = { if(it.length<=maxChar) botName = it },
                    onSystemPromptChange = { if(it.length<=maxChar*6) systemPrompt = it},
                    modelList = modelList,
                    onModelClick = { model -> selectedModel = model},
                    avatarList = avatarList,
                    onAvatarClick = { selectedAvatar = it },
                    selectedAvatar = selectedAvatar
                )
            }
            AnimatedVisibility(
                visible = isAboutDialogVisible,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                AboutDialog(
                    onCloseClick = { isAboutDialogVisible = false}
                )
            }
            AnimatedVisibility(
                visible = isDeleteDialogVisible,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                selectedChat.let { chatItem ->
                    DeleteDialog(
                        chatTitle = chatItem.chatTitle,
                        userName = chatItem.userName,
                        onAcceptClick = {
                            homeViewModel.deleteChat(chatItem)
                            isDeleteDialogVisible = false
                        },
                        onCloseClick = { isDeleteDialogVisible = false}
                    )
                }

            }
            LazyColumn(
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                    items(
                        items = chatsList.items,
                        key = { chatItem -> chatItem.chatId }
                    ) { chatItem ->
                        NewChatItem(
                            modelName = chatItem.modelName,
                            chatTitle = chatItem.chatTitle,
                            userName = chatItem.userName,
                            botName = chatItem.botName,
                            onDeleteClick = {
                                selectedChat = chatItem
                                isDeleteDialogVisible = true
                            },
                            onItemClick = {
                                when {
                                    selectedChats.contains(chatItem.chatId) -> {
                                        selectedChats.remove(chatItem.chatId)
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
                            chatImage = chatItem.chatIcon,
                            modifier = Modifier.animateItem(),
                            onItemLongPress = {
                                if(!selectedChats.contains(chatItem.chatId)) {
                                    selectedChats.add(chatItem.chatId)
                                }
                            },
                            onSelectedItemClick = {
                                selectedChats.remove(chatItem.chatId)
                            },
                            isSelected = selectedChats.contains(chatItem.chatId),
                            isNewMessageReceived = chatItem.newMessageStatus != 0,
                            newMessageStatus = chatItem.newMessageStatus
                        )
                    }
                }
            }
        }
    }
