package com.example.ollamaui.ui.screen.filemanager

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun FileManagerScreen(
    fileManagerViewModel: FileManagerViewModel,
    attachedFiles: State<AttachedFilesList>,
    embeddingInProgressList: State<List<Long>>,
    embeddingModel: State<EmbeddingModel>,
    baseAddress: State<BaseAddress>,
    onFileClick: (StableFile) -> Unit,
    isEmbeddingModelPulled: Boolean,
    onBackClick: () -> Unit,
) {
    var isFileExplorerEnable by remember { mutableStateOf(false) }
    val isEmbeddingSettingsSet by remember { derivedStateOf { embeddingModel.value.isEmbeddingModelSet && isEmbeddingModelPulled } }
    val isAttachedFilesEmpty by remember { derivedStateOf { attachedFiles.value.item.isEmpty() } }
    val snackbarHostState = remember { SnackbarHostState() }
    val isEmbeddingInProgress: (Long) -> Boolean = remember { { id -> embeddingInProgressList.value.contains(id) } }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            ) {
                Box(
                    modifier = Modifier.weight(0.8f).height(64.dp).padding(5.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp)
                    ) {
                        Text(text = "Name", maxLines = 1, modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
                        VerticalDivider(thickness = 2.dp)
                        Text(text = "Type", maxLines = 1, modifier = Modifier.weight(0.1f), textAlign = TextAlign.Center)
                        VerticalDivider(thickness = 2.dp)
                        Text(text = "Added Time", maxLines = 1, modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
                        VerticalDivider(thickness = 2.dp)
                        Text(text = "Hash", maxLines = 1, modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.width(25.dp))
            }
        },
        floatingActionButton = {
            CustomButton(
                isButtonEnabled = isEmbeddingSettingsSet,
                description = "Import File",
                icon = R.drawable.baseline_add_24,
                buttonSize = 40,
                iconSize = 30,
                onButtonClick = { isFileExplorerEnable = true },
                containerColor = MaterialTheme.colorScheme.outlineVariant
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValue ->
        LaunchedEffect(isEmbeddingSettingsSet) {
            if(!isEmbeddingSettingsSet) {
                snackbarHostState.showSnackbar(
                    message = "Please choose and pull an embedding model in the settings menu!",
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
                        onDeleteFileClick = { fileManagerViewModel.removeAttachedFile(fileId = it.fileId, isImage = it.isImage) },
                        isFileReady = !isEmbeddingInProgress(it.fileId)
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
        },
        exceededAlert = { fileSize ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Selected file size: ${(fileSize/1024)/1024}MB > Allowed file size: 5MB"
                )
            }
        }
    )
    BackHandler {
        onBackClick()
    }
}