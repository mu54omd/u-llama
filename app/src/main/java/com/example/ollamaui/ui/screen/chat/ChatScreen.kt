package com.example.ollamaui.ui.screen.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.ui.common.messageModelToText
import com.example.ollamaui.ui.screen.chat.components.AttachDocs
import com.example.ollamaui.ui.screen.chat.components.AttachedFilesItem
import com.example.ollamaui.ui.screen.chat.components.ChatBottomBar
import com.example.ollamaui.ui.screen.chat.components.ChatTopBar
import com.example.ollamaui.ui.screen.chat.components.Conversation
import com.example.ollamaui.ui.screen.chat.components.DotsPulsing
import com.example.ollamaui.ui.screen.chat.components.FileDetails
import com.example.ollamaui.ui.screen.common.CustomButton
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatState: ChatStates,
    attachedImagesList: AttachedFilesList,
    attachedFilesList: AttachedFilesList,
    embeddingModel: String,
    isEmbeddingModelSet: Boolean,
    onBackClick: () -> Unit
) {
    var textValue by rememberSaveable { mutableStateOf("") }
    var textValueBackup by rememberSaveable { mutableStateOf("") }
    val selectedDialogs = remember { mutableStateMapOf<Int, MessageModel>() }
    val visibleDetails = remember { mutableStateMapOf<Int, MessageModel>() }
    val clipboard: ClipboardManager = LocalClipboardManager.current
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = chatState.chatModel.chatMessages.messageModels.size
    )
    val scope = rememberCoroutineScope()
    val isFabVisible by remember {
        derivedStateOf {
            listState.canScrollForward
        }
    }
    var isEnabled by remember { mutableStateOf(false) }
    var isFileDetailsVisible by remember { mutableStateOf(false) }
    val file = remember { mutableStateOf(File()) }
    val selectedImages = remember { mutableStateListOf<File>() }
    val selectedDocs = remember { mutableStateListOf<File>() }

    Scaffold(
        topBar = {
            ChatTopBar(
                modelName = chatState.chatModel.modelName,
                chatTitle = chatState.chatModel.chatTitle,
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
                     chatState.isSendingFailed -> { chatViewModel.retry() }
                     chatState.isRespondingList.contains(chatState.chatModel.chatId) -> { chatViewModel.stop(chatId = chatState.chatModel.chatId) }
                     else -> {
                         chatViewModel.sendButton(text = textValue, selectedImages = selectedImages, selectedDocs = selectedDocs, embeddingModel = embeddingModel)
                         textValue = ""
                         textValueBackup = textValue
                     }
                    }
                },
                onClearClick = { textValue = "" },
                onAttachClick = { isEnabled = true },
                isModelSelected = chatState.chatModel.modelName != "",
                isSendingFailed = chatState.isSendingFailed,
                isResponding = chatState.isRespondingList.contains(chatState.chatModel.chatId),
                focusRequester = focusRequester
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
                        scope.launch { listState.animateScrollToItem(index = chatState.chatModel.chatMessages.messageModels.size)}
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
            AnimatedVisibility(
                visible = attachedImagesList.item.any { it.chatId == chatState.chatModel.chatId }, enter = scaleIn(), exit = scaleOut()
            ) {
                LazyRow(modifier = Modifier.height(32.dp)) {
                    itemsIndexed(attachedImagesList.item.filter { it.chatId == chatState.chatModel.chatId }){ index, item ->
                        AttachedFilesItem(
                            item = item,
                            index = index,
                            onFilesLongPress = {
                                if(!selectedImages.contains(it)) {
                                    selectedImages.add(it)
                                }
                            },
                            onFilesClick = {
                                if(selectedImages.isEmpty()) {
                                    isFileDetailsVisible = true
                                    file.value = it
                                }else{
                                    if(selectedImages.contains(it)){
                                        selectedImages.remove(it)
                                    }else{
                                        selectedImages.add(it)
                                    }
                                }
                            },
                            onSelectedItemClick = {
                                selectedImages.remove(it)
                            },
                            onRemoveClick = { _, isImage ->
                                selectedImages.remove(item)
                                chatViewModel.removeAttachedFile(index, isImage)
                                            },
                            isSelected = selectedImages.contains(item)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = attachedFilesList.item.any { it.chatId == chatState.chatModel.chatId }, enter = scaleIn(), exit = scaleOut()
            ) {
                LazyRow(modifier = Modifier.height(32.dp)) {
                    itemsIndexed(attachedFilesList.item.filter { it.chatId == chatState.chatModel.chatId }){ index, item ->
                        AttachedFilesItem(
                            item =item,
                            index = index,
                            onFilesLongPress = {
                                if(!selectedDocs.contains(it)) {
                                    selectedDocs.add(it)
                                }
                            },
                            onFilesClick = {
                                if(selectedDocs.isEmpty()) {
                                    isFileDetailsVisible = true
                                    file.value = it
                                }else{
                                    if(selectedDocs.contains(it)){
                                        selectedDocs.remove(it)
                                    }else{
                                        selectedDocs.add(it)
                                    }
                                }
                            },
                            onSelectedItemClick = {
                                selectedDocs.remove(it)
                            },
                            onRemoveClick = { _, isImage ->
                                selectedDocs.remove(item)
                                chatViewModel.removeAttachedFile(index, isImage)
                                            },
                            isSelected = selectedDocs.contains(item)
                        )
                    }
                }
            }
            Conversation(
                messagesModel = chatState.chatModel.chatMessages ,
                modifier = Modifier.weight(1f),
                onItemClick = {index, message ->
                    if(selectedDialogs.isEmpty()) {
                        if(visibleDetails.contains(index)){
                            visibleDetails.remove(index)
                        }else{
                            visibleDetails[index] = message
                        }
                    }else{
                        if(selectedDialogs.contains(index)) {
                            selectedDialogs.remove(index)
                        }else {
                            selectedDialogs[index] = message
                        }
                }
                              },
                onSelectedItemClick = { index, _ -> selectedDialogs.remove(index) },
                onLongPressItem = { index, message -> if(selectedDialogs.contains(index)) selectedDialogs.remove(index) else selectedDialogs[index] = message },
                isSelected = { index, _ -> selectedDialogs.contains(index) },
                isVisible = { index, _ -> visibleDetails.contains(index) },
                listState = listState
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
            AttachDocs(
                isEnabled = isEnabled,
                onDispose = {isEnabled = false},
                onSelectClick = { result, error, documentType, fileName ->
                    chatViewModel.attachFileToChat(
                        attachResult = result,
                        attachError = error,
                        documentType = documentType,
                        fileName = fileName,
                        embeddingModel = embeddingModel,
                        isEmbeddingModelSet = isEmbeddingModelSet
                        )
                }
            )
            AnimatedVisibility(
                visible = isFileDetailsVisible
            ) {
                FileDetails(
                    onDismissRequest = { isFileDetailsVisible = false},
                    file = file.value
                )
            }
        }

        BackHandler {
            onBackClick()
        }
    }
}