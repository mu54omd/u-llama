package com.mu54omd.ullama.ui.screen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.activity.EmbeddingModel
import com.mu54omd.ullama.domain.model.MessageModel
import com.mu54omd.ullama.domain.model.objectbox.StableFile
import com.mu54omd.ullama.helper.network.NetworkStatus
import com.mu54omd.ullama.ui.common.messageModelToText
import com.mu54omd.ullama.ui.screen.chat.components.AttachedFilesItem
import com.mu54omd.ullama.ui.screen.chat.components.ChatBottomBar
import com.mu54omd.ullama.ui.screen.chat.components.ChatDialog
import com.mu54omd.ullama.ui.screen.chat.components.ChatTopBar
import com.mu54omd.ullama.ui.screen.chat.components.alphaFilterColorMatrix
import com.mu54omd.ullama.ui.screen.chat.components.colorMatrixWithContent
import com.mu54omd.ullama.ui.screen.chat.components.outlineBlur
import com.mu54omd.ullama.ui.screen.common.CustomButton
import com.mu54omd.ullama.utils.Constants.SYSTEM_ROLE
import com.mu54omd.ullama.utils.Constants.TOP_BAR_HEIGHT
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatState: State<ChatStates>,
    networkStatus: State<NetworkStatus>,
    attachedFilesList: State<AttachedFilesList>,
    embeddingInProgressList: State<List<Long>>,
    embeddingModel: State<EmbeddingModel>,
    onBackClick: () -> Unit,
    onFileClick: (StableFile) -> Unit,
    onAttachClick: () -> Unit,
) {
    val chatId = chatState.value.chatModel.chatId
    var textValue by rememberSaveable(chatId) { mutableStateOf("") }
    val selectedDialogs = remember(chatId) { mutableStateMapOf<Int, MessageModel>() }
    val visibleDetails = remember(chatId) { mutableStateMapOf<Int, MessageModel>() }
    val clipboard: ClipboardManager = LocalClipboardManager.current

    val scope = rememberCoroutineScope()
    val selectedFiles = remember(chatId) { mutableStateListOf<StableFile>() }
    val isAnyFileAttached by remember { derivedStateOf { attachedFilesList.value.item.isNotEmpty() && embeddingModel.value.isEmbeddingModelSet } }
    var isAttachedFilesListEnabled by remember { mutableStateOf(false) }
    val isEmbeddingInProgress: (Long) -> Boolean =
        remember { { id -> embeddingInProgressList.value.contains(id) } }
    val tempText by remember(chatId) { derivedStateOf { chatViewModel.temporaryReceivedMessage[chatId] } }
    val isResponding by remember(chatId) {
        derivedStateOf {
            chatState.value.isRespondingList.contains(
                chatId
            )
        }
    }
    var lastItemHeight by remember { mutableIntStateOf(0) }
    var extraItemHeight by remember { mutableIntStateOf(0) }
    val listState = remember(chatId) {
        LazyListState(
            firstVisibleItemIndex = chatState.value.chatModel.chatMessages.messageModels.lastIndex,
            firstVisibleItemScrollOffset = Int.MAX_VALUE
        )
    }

    val isAtBottom by remember(chatId) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            val totalItems = layoutInfo.totalItemsCount
            if (visibleItems.isEmpty()) return@derivedStateOf true
            val lastVisible = visibleItems.last().index
            lastVisible >= totalItems - 1
        }
    }
    var isAutoScrollEnabled by remember { mutableStateOf(true) }

    val attachedFilesBgColor = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
        )
    )
    val isReading by chatViewModel.isReading.collectAsState()
    val topBgColor = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.background, Color.Transparent)
    )
    val bottomBgColor = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
    )

    val metaBallColorMatrix = remember {
        alphaFilterColorMatrix()
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection{
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if(available.y != 0f) {
                    isAutoScrollEnabled = false
                }
                return Offset.Zero
            }
        }
    }



    Scaffold(
    ) { contentPadding ->
        BackHandler {
            onBackClick()
        }
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
                isAutoScrollEnabled
            ) {
                if (isResponding && isAutoScrollEnabled) {
                    snapshotFlow { tempText }
                        .filterNotNull()
                        .collect {
                            val lastIndex = chatState.value.chatModel.chatMessages.messageModels.lastIndex + 1
                            listState.scrollToItem(lastIndex, extraItemHeight)
                        }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = TOP_BAR_HEIGHT)
                    .nestedScroll(nestedScrollConnection),
                verticalArrangement = Arrangement.Bottom,
                state = listState,
                contentPadding = PaddingValues(
                    top = 64.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 128.dp
                )
            ) {
                itemsIndexed(
                    items = chatState.value.chatModel.chatMessages.messageModels,
                    key = { _, item -> item.messageId }
                ) { index, message ->
                    if (message.role != SYSTEM_ROLE) {
                        val lastIndex = chatState.value.chatModel.chatMessages.messageModels.lastIndex
                        if (index == lastIndex || index == lastIndex - 1) {
                            visibleDetails[index] = message
                        }
                        ChatDialog(
                            messageModel = message,
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    if (index == chatState.value.chatModel.chatMessages.messageModels.lastIndex) {
                                        lastItemHeight = coordinates.size.height
                                    }
                                },
                            isSelected = selectedDialogs.contains(index) && selectedDialogs[index]?.messageId == message.messageId,
                            isDetailsVisible = visibleDetails.contains(index) && visibleDetails[index]?.messageId == message.messageId,
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
                            isLastMinusOneMessage = index == chatState.value.chatModel.chatMessages.messageModels.lastIndex - 1,
                            isLastMessage = index == chatState.value.chatModel.chatMessages.messageModels.lastIndex,
                            onReproduceResponse = {
                                isAutoScrollEnabled = true
                                chatViewModel.reproduceResponse()
                            },
                            isSendingFailed = chatState.value.isSendingFailed,
                            onDeleteLastMessage = { chatViewModel.deleteLastMessage(index) },
                            onEditLastMessage = {
                                textValue = chatViewModel.editLastMessage(index)
                            },
                            isResponding = isResponding,
                            onReadClick = { botMessage -> chatViewModel.read(chatId, botMessage) },
                            onStopClick = { chatViewModel.stopTTS(chatId) },
                            isReading = isReading[chatId] == true
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .offset(y = TOP_BAR_HEIGHT)
                    .align(Alignment.TopCenter)
                    .background(brush = topBgColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .background(brush = bottomBgColor)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = TOP_BAR_HEIGHT + 10.dp)
            ) {
                AnimatedVisibility(
                    visible = isAttachedFilesListEnabled,
                    enter = slideInVertically(),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight * 2 }
                    )
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(start = 1.dp, end = 1.dp),
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .height(36.dp)
                            .fillMaxWidth()
                            .drawBehind {
                                drawRoundRect(
                                    brush = attachedFilesBgColor,
                                    cornerRadius = CornerRadius(x = 20f, y = 20f)
                                )
                            }
                            .clip(RoundedCornerShape(50))
                            .padding(start = 2.dp, end = 2.dp)
                    ) {
                        itemsIndexed(
                            items = attachedFilesList.value.item,
                            key = { _, item -> item.fileId }
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
            ChatTopBar(
                modelName = chatState.value.chatModel.modelName,
                chatTitle = chatState.value.chatModel.chatTitle,
                onBackClick = onBackClick,
                onCopyClick = {
                    clipboard.setText(AnnotatedString(text = messageModelToText(selectedDialogs)))
                    selectedDialogs.clear()
                },
                onDeselectClick = { selectedDialogs.clear() },
                onShowAttachedFileClick = { isAttachedFilesListEnabled = true },
                onHideAttachedFileClick = { isAttachedFilesListEnabled = false },
                isCopyButtonEnabled = selectedDialogs.isNotEmpty(),
                isResponding = chatState.value.isRespondingList.contains(chatId) && !chatState.value.isSendingFailed,
                modifier = Modifier.align(Alignment.TopCenter),
                isAnyFileAttached = isAnyFileAttached
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(10.dp)
                    .requiredHeight(250.dp)
                    .alpha(0.9f)
                    .align(Alignment.BottomCenter)
                    .colorMatrixWithContent(metaBallColorMatrix)
            ) {
                var bottomBarLineCount by remember { mutableIntStateOf(0) }
                val offsetDistance by remember {
                    derivedStateOf {
                        when {
                            bottomBarLineCount > 2 -> { 120.dp }
                            bottomBarLineCount == 2 -> { 100.dp }
                            else -> 80.dp
                        }
                    }
                }
                val buttonOffset by animateDpAsState(
                    targetValue = if (!isAtBottom) offsetDistance else 0.dp,
                    animationSpec = tween(durationMillis = 500)
                )

                CustomButton(
                    onButtonClick = {
                        scope.launch {
                            listState.scrollToItem(
                                index = chatState.value.chatModel.chatMessages.messageModels.lastIndex,
                                scrollOffset = Int.MAX_VALUE
                            )
                            isAutoScrollEnabled = true
                        }
                    },
                    icon = R.drawable.baseline_expand_more_24,
                    description = "Scroll Down",
                    buttonSize = 30,
                    iconSize = 25,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    elevation = 0,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset { IntOffset(x = 0, y = -buttonOffset.roundToPx()) }
                        .outlineBlur(
                            20.dp,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                )
                ChatBottomBar(
                    textValue = textValue,
                    onValueChange = { textValue = it },
                    onSendClick = {
                        isAutoScrollEnabled = true
                        when {
                            chatState.value.isSendingFailed -> {
                                chatViewModel.retry()
                            }

                            chatState.value.isRespondingList.contains(chatId) -> {
                                chatViewModel.stop(chatId = chatId)
                            }

                            else -> {
                                chatViewModel.sendButton(
                                    text = textValue,
                                    selectedFiles = selectedFiles,
                                    embeddingModel = embeddingModel.value.embeddingModelName
                                )
                                textValue = ""
                            }
                        }
                    },
                    onClearClick = { textValue = "" },
                    onAttachClick = { onAttachClick() },
                    onLineCountChange = { lineCount -> bottomBarLineCount = lineCount },
                    isModelSelected = chatState.value.chatModel.modelName != "" && networkStatus.value == NetworkStatus.CONNECTED,
                    isSendingFailed = chatState.value.isSendingFailed,
                    isResponding = chatState.value.isRespondingList.contains(chatId),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .outlineBlur(
                            30.dp,
                            shape = RoundedCornerShape(30),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                )
            }
        }
    }
}