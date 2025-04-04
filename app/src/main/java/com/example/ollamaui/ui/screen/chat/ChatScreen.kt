package com.example.ollamaui.ui.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.activity.EmbeddingModel
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.objectbox.StableFile
import com.example.ollamaui.ui.common.messageModelToText
import com.example.ollamaui.ui.screen.chat.components.AttachedFilesItem
import com.example.ollamaui.ui.screen.chat.components.ChatBottomBar
import com.example.ollamaui.ui.screen.chat.components.ChatDialog
import com.example.ollamaui.ui.screen.chat.components.ChatTopBar
import com.example.ollamaui.ui.screen.chat.components.PulsingDots
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatState: State<ChatStates>,
    attachedFilesList: State<AttachedFilesList>,
    embeddingInProgressList: State<List<Long>>,
    embeddingModel: State<EmbeddingModel>,
    onBackClick: () -> Unit,
    onFileClick: (StableFile) -> Unit,
    onAttachClick: () -> Unit,
) {
    var textValue by rememberSaveable { mutableStateOf("") }
    val selectedDialogs = remember(chatState.value.chatModel.chatId) { mutableStateMapOf<Int, MessageModel>() }
    val visibleDetails = remember(chatState.value.chatModel.chatId) { mutableStateMapOf<Int, MessageModel>() }
    val clipboard: ClipboardManager = LocalClipboardManager.current

    val scope = rememberCoroutineScope()
    val selectedFiles = remember(chatState.value.chatModel.chatId) { mutableStateListOf<StableFile>() }
    val isAnyFileAttached by remember { derivedStateOf { attachedFilesList.value.item.isNotEmpty() && embeddingModel.value.isEmbeddingModelSet }}
    val isEmbeddingInProgress: (Long) -> Boolean = remember { { id -> embeddingInProgressList.value.contains(id) } }
    val tempText by remember(chatState.value.chatModel.chatId) { derivedStateOf { chatViewModel.temporaryReceivedMessage[chatState.value.chatModel.chatId] } }
    val isResponding by remember(chatState.value.chatModel.chatId) { derivedStateOf { chatState.value.isRespondingList.contains(chatState.value.chatModel.chatId) } }
    var lastItemHeight by remember { mutableIntStateOf(0) }
    var extraItemHeight by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = chatState.value.chatModel.chatMessages.messageModels.lastIndex,
        initialFirstVisibleItemScrollOffset = Int.MAX_VALUE
    )
    val isFabVisible by remember { derivedStateOf { listState.canScrollForward && !isResponding } }

    Scaffold(
        topBar = {
            ChatTopBar(
                modelName = chatState.value.chatModel.modelName,
                chatTitle = chatState.value.chatModel.chatTitle,
                onBackClick = onBackClick,
                onCopyClick = {
                    clipboard.setText(AnnotatedString(text = messageModelToText(selectedDialogs)))
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
                     chatState.value.isSendingFailed -> { chatViewModel.retry() }
                     chatState.value.isRespondingList.contains(chatState.value.chatModel.chatId) -> { chatViewModel.stop(chatId = chatState.value.chatModel.chatId) }
                     else -> {
                         chatViewModel.sendButton(text = textValue, selectedFiles = selectedFiles, embeddingModel = embeddingModel.value.embeddingModelName)
                         textValue = ""
                     }
                    }
                },
                onClearClick = { textValue = "" },
                onAttachClick = { onAttachClick() },
                isModelSelected = chatState.value.chatModel.modelName != "",
                isSendingFailed = chatState.value.isSendingFailed,
                isResponding = chatState.value.isRespondingList.contains(chatState.value.chatModel.chatId),
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                CustomButton(
                    onButtonClick = {
                        scope.launch {
                            listState.animateScrollToItem(
                                index = chatState.value.chatModel.chatMessages.messageModels.lastIndex,
                                scrollOffset = lastItemHeight
                            )
                        }
                    },
                    icon = R.drawable.baseline_expand_more_24,
                    description = "Scroll Down",
                    buttonSize = 50,
                    iconSize = 40,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    elevation = 10
                )
            }
        }
    ) { contentPadding ->
        val brush = Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                )
        ) {
            LaunchedEffect(
                isResponding,
                extraItemHeight,
            ) {
                if (!listState.isScrollInProgress) {
                    val lastIndex =
                        chatState.value.chatModel.chatMessages.messageModels.lastIndex + if (isResponding) 1 else 0
                    val scrollOffset = if (isResponding) extraItemHeight else lastItemHeight
                    snapshotFlow { chatState.value.chatModel.chatMessages.messageModels }
                        .collect {
                            listState.scrollToItem(lastIndex, scrollOffset)
                        }
                }
            }
            LazyColumn(
                modifier = Modifier.padding(bottom = 25.dp),
                verticalArrangement = Arrangement.Bottom,
                state = listState,
                contentPadding = PaddingValues(top = 128.dp, start = 10.dp, end = 10.dp, bottom = 128.dp)
            ) {
                itemsIndexed(
                    items = chatState.value.chatModel.chatMessages.messageModels,
                    key = { _, item ->  item.messageId }
                ){ index, message ->
                    if (message.role != SYSTEM_ROLE) {
                        ChatDialog(
                            messageModel = message,
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    if(index == chatState.value.chatModel.chatMessages.messageModels.lastIndex) {
                                        lastItemHeight = coordinates.size.height
                                    }
                            },
                            isSelected = selectedDialogs.contains(index) && selectedDialogs[index]?.messageId == message.messageId,
                            isVisible = visibleDetails.contains(index) && visibleDetails[index]?.messageId == message.messageId,
                            onLongPressItem = {
                                if (selectedDialogs.contains(index)) {
                                    selectedDialogs.remove(index)
                                } else {
                                    selectedDialogs[index] = message
                                }
                            },
                            onItemClick = {
                                if (selectedDialogs.isEmpty()) {
                                    if (visibleDetails.contains(index)) {
                                        visibleDetails.remove(index)
                                    } else {
                                        visibleDetails[index] = message
                                    }
                                } else {
                                    if (selectedDialogs.contains(index)) {
                                        selectedDialogs.remove(index)
                                    } else {
                                        selectedDialogs[index] = message
                                    }
                                }
                            },
                            onSelectedItemClick = { selectedDialogs.remove(index) },
                        )
                    }
                }
                item {
                    if (isResponding) {
                        tempText?.let {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .padding(16.dp)
                                    .onGloballyPositioned { coordinates ->
                                        extraItemHeight = coordinates.size.height
                                    }
                            ) {
                                Text(text = it)
                            }
                        }
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            brush = brush,
                        )
                    }
                    .align(Alignment.TopCenter)
            ) {
                AnimatedVisibility(
                    visible = isAnyFileAttached,
                    enter = slideInVertically(),
                    exit = shrinkHorizontally()
                ) {
                    LazyRow(modifier = Modifier.height(32.dp)) {
                        itemsIndexed(
                            items = attachedFilesList.value.item,
                            key = { _, item -> item.fileId}
                        ) { index, item ->
                            AttachedFilesItem(
                                item = item,
                                onFilesLongPress = {
                                    if (!selectedFiles.contains(it)) {
                                        selectedFiles.add(it)
                                    }
                                },
                                onFilesClick = {
                                    if (selectedFiles.isEmpty()) {
                                        onFileClick(it)
                                    } else {
                                        if (selectedFiles.contains(it)) {
                                            selectedFiles.remove(it)
                                        } else {
                                            selectedFiles.add(it)
                                        }
                                    }
                                },
                                onSelectedItemClick = {
                                    selectedFiles.remove(it)
                                },
                                isSelected = selectedFiles.contains(item),
                                isFileReady = !isEmbeddingInProgress(item.fileId)
                            )
                        }
                    }
                }
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(30.dp)
                    .align(Alignment.BottomCenter)
            ) {
                AnimatedVisibility(visible = chatState.value.isRespondingList.contains(chatState.value.chatModel.chatId) && !chatState.value.isSendingFailed) {
                    PulsingDots()
                }
                AnimatedVisibility(visible = chatState.value.isSendingFailed) {
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
                                    textValue =
                                        chatState.value.chatModel.chatMessages.messageModels.last().content
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

    }
}