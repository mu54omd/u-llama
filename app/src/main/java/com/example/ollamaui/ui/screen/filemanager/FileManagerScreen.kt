package com.example.ollamaui.ui.screen.filemanager

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.activity.BaseAddress
import com.example.ollamaui.activity.EmbeddingModel
import com.example.ollamaui.domain.model.objectbox.StableFile
import com.example.ollamaui.ui.screen.chat.AttachedFilesList
import com.example.ollamaui.ui.screen.chat.components.AttachDocs
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.filemanager.components.EmptyFileManager
import com.example.ollamaui.ui.screen.filemanager.components.FileItem

@Composable
fun FileManagerScreen(
    fileManagerViewModel: FileManagerViewModel,
    attachedFiles: State<AttachedFilesList>,
    embeddingModel: State<EmbeddingModel>,
    baseAddress: State<BaseAddress>,
    onFileClick: (StableFile) -> Unit,
    onBackClick: () -> Unit,
) {
    var isFileExplorerEnable by remember { mutableStateOf(false) }
    val isEmbeddingModelSet by remember { derivedStateOf { embeddingModel.value.isEmbeddingModelSet } }
    val isAttachedFilesEmpty by remember { derivedStateOf { attachedFiles.value.item.isEmpty() } }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            Row {
                Box(
                    modifier = Modifier.fillMaxWidth().height(64.dp).padding(5.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp)
                    ) {
                        Text(text = "Name", maxLines = 1, modifier = Modifier.weight(0.1f).padding(5.dp), textAlign = TextAlign.Center)
                        VerticalDivider()
                        Text(text = "Type", maxLines = 1, modifier = Modifier.weight(0.1f).padding(5.dp), textAlign = TextAlign.Center)
                        VerticalDivider()
                        Text(text = "Added Time", maxLines = 1, modifier = Modifier.weight(0.2f).padding(5.dp), textAlign = TextAlign.Center)
                        VerticalDivider()
                        Text(text = "Hash", maxLines = 1, modifier = Modifier.weight(0.3f).padding(5.dp), textAlign = TextAlign.Center)
                    }
                }
            }
        },
        floatingActionButton = {
            CustomButton(
                isButtonEnabled = isEmbeddingModelSet,
                description = "Import File",
                icon = R.drawable.baseline_add_24,
                buttonSize = 40,
                iconSize = 30,
                onButtonClick = { isFileExplorerEnable = true }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValue ->
        LaunchedEffect(isEmbeddingModelSet) {
            if(!isEmbeddingModelSet) {
                snackbarHostState.showSnackbar(
                    message = "Please choose an embedding model in the setting menu!",
                    duration = SnackbarDuration.Indefinite
                )
            }
        }
        if (!isAttachedFilesEmpty) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValue),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            ) {
                items(
                    items = attachedFiles.value.item,
                    key = { it.fileId }
                    ) {
                    FileItem(
                        modifier = Modifier.animateItem(),
                        fileName = it.fileName,
                        hash = it.hash,
                        fileAddedTime = it.fileAddedTime,
                        onFileClick = { onFileClick(it) },
                        onDeleteFileClick = { fileManagerViewModel.removeAttachedFile(fileId = it.fileId, isImage = it.isImage) }
                    )
                }
            }
        } else {
            EmptyFileManager()
        }

    }

    AttachDocs(
        isEnabled = isFileExplorerEnable,
        onDispose = { isFileExplorerEnable = false },
        onSelectClick = { result, error, documentType, fileName, hash ->
            fileManagerViewModel.attachFileToChat(
                attachResult = result,
                attachError = error,
                documentType = documentType,
                hash = hash,
                fileName = fileName,
                embeddingModel = embeddingModel.value.embeddingModelName,
                ollamaBaseAddress = baseAddress.value.ollamaBaseAddress,
            )
        }
    )
    BackHandler {
        onBackClick()
    }
}