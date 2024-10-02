package com.example.ollamaui.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.home.components.AboutDialog
import com.example.ollamaui.ui.screen.home.components.CustomFabButton
import com.example.ollamaui.ui.screen.home.components.HomeTopBar
import com.example.ollamaui.ui.screen.home.components.NewChatDialog
import com.example.ollamaui.ui.screen.home.components.NewChatItem
import com.example.ollamaui.ui.screen.home.components.SettingDialog

@Composable
fun HomeScreen(
    onChatClick: (Int) -> Unit,
    homeViewModel: HomeViewModel,
    homeState: HomeStates,
    isOllamaAddressSet: Boolean,
    ollamaAddress: String,
    onSaveOllamaAddressClick: (String) -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }
    var fabListVisible by remember { mutableStateOf(false) }
    var isFabDialogVisible by remember { mutableStateOf(false) }
    var isAboutDialogVisible by remember { mutableStateOf(false) }
    var isSettingDialogVisible by remember { mutableStateOf(!isOllamaAddressSet) }
    var yourName by remember { mutableStateOf("") }
    var chatTitle by remember { mutableStateOf("") }
    var httpValue by remember { mutableStateOf(ollamaAddress) }
    val avatarList = listOf(R.drawable.avatar_logo_01, R.drawable.avatar_logo_02, R.drawable.avatar_logo_03, R.drawable.avatar_logo_04, R.drawable.avatar_logo_05, R.drawable.avatar_logo_06, R.drawable.avatar_logo_07, R.drawable.avatar_logo_08, R.drawable.avatar_logo_09)

    Scaffold(
        topBar = {
                    HomeTopBar(
                        onSettingClick = {
                            fabListVisible = false
                            isSettingDialogVisible = true
                                         },
                        onAboutClick = {
                            fabListVisible = false
                            isAboutDialogVisible = true
                        }
                    )
                 },
        bottomBar = {},
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            CustomFabButton(
                isModelListLoaded = homeState.isModelListLoaded,
                fabListVisible = fabListVisible,
                modelList = homeState.modelList,
                onItemClick = { item ->
                    fabListVisible = false
                    homeViewModel.selectOllamaModel(item)
                    yourName = ""
                    chatTitle = ""
                    isFabDialogVisible = true
                              },
                onButtonClick = {
                    if(!homeState.isModelListLoaded){
                        homeViewModel.refresh()
                    }
                    else
                    fabListVisible = !fabListVisible
                }
            )
        },
        modifier = Modifier.pointerInput(Unit){ detectTapGestures { fabListVisible = false }}
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            if (!homeState.isModelListLoaded){
                CircularProgressIndicator()
                if(homeState.statusError != null){
                    val errorMessage = stringResource(homeState.statusError)
                    val throwableMessage =  homeState.stateThrowable
                    LaunchedEffect(homeState.statusError) {
                        snackbarHostState
                            .showSnackbar(
                                message = errorMessage + "\n" + throwableMessage,
                                withDismissAction = true,
                                duration = SnackbarDuration.Indefinite
                            )
                    }
                }
            }

            AnimatedVisibility(
                visible = isFabDialogVisible
            ) {
                NewChatDialog(
                    yourName = yourName,
                    onYourNameChange = { yourName = it },
                    chatTitle = chatTitle,
                    onChatTitleChange = { chatTitle = it },
                    onCloseClick = { isFabDialogVisible = false},
                    onAcceptClick = {
                        homeViewModel.addNewChat(chatTitle, yourName, avatarList.random())
                        homeViewModel.reloadDatabase()
                        isFabDialogVisible = false
                    }
                )
            }
            AnimatedVisibility(
                visible = isAboutDialogVisible
            ) {
                AboutDialog(
                    onCloseClick = { isAboutDialogVisible = false}
                )
            }
            AnimatedVisibility(
                visible = isSettingDialogVisible
            ) {
                SettingDialog(
                    httpValue = httpValue,
                    onAcceptClick = {
                        onSaveOllamaAddressClick(httpValue)
                        homeViewModel.refresh()
                        isSettingDialogVisible = false
                    },
                    onCloseClick = { isSettingDialogVisible = false},
                    onValueChange = { httpValue = it}
                )
            }
            LazyColumn(
                modifier = Modifier.align(Alignment.TopCenter),
                contentPadding = PaddingValues(10.dp)
            ) {
                items(homeState.chatList){ chatItem ->
                    NewChatItem(
                        modelName = chatItem.modelName,
                        chatTitle = chatItem.chatTitle,
                        onDeleteClick = {
                            fabListVisible = false
                            homeViewModel.deleteChat(chatItem)
                            homeViewModel.reloadDatabase()
                        },
                        onItemClick = {
                            fabListVisible = false
                            onChatClick(chatItem.chatId)
                        },
                        chatImage = chatItem.chatIcon
                    )
                }
            }
        }
    }
}