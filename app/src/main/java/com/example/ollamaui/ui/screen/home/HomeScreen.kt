package com.example.ollamaui.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.screen.home.components.CustomFabButton
import com.example.ollamaui.ui.screen.home.components.HomeTopBar
import com.example.ollamaui.ui.screen.home.components.NewChatItem
import java.util.Date

@Composable
fun HomeScreen(
    onChatClick: (Int) -> Unit,
    homeViewModel: HomeViewModel,
    homeState: HomeStates
) {

    val snackbarHostState = remember { SnackbarHostState() }
    var fabListVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
                    HomeTopBar(
                    onDrawerClick = {},
                    onAboutClick = {}
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
                    homeViewModel.addNewChat(Date().toString().split(" ")[3])
                    homeViewModel.reloadDatabase()
                              },
                onButtonClick = {
                    if(!homeState.isModelListLoaded){
                        homeViewModel.refresh()
                    }
                    else
                    fabListVisible = !fabListVisible
                }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            if (homeState.isModelListLoaded) {
                LaunchedEffect(homeState.statusError) {
                    snackbarHostState
                        .showSnackbar(
                            message = homeState.ollamaStatus,
                            withDismissAction = true,
                        )
                }
            }else {
                CircularProgressIndicator()
                if(homeState.statusError != null){
                    val errorMessage = stringResource(homeState.statusError)
                    LaunchedEffect(homeState.statusError) {
                        snackbarHostState
                            .showSnackbar(
                                message = errorMessage,
                                withDismissAction = true,
                            )
                    }
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(10.dp)
            ) {
                items(homeState.chatList){ chatItem ->
                    NewChatItem(
                        modelName = chatItem.modelName,
                        chatTitle = chatItem.chatTitle,
                        onDeleteClick = {
                            homeViewModel.deleteChat(chatItem)
                            homeViewModel.reloadDatabase()
                        },
                        onItemClick = { onChatClick(chatItem.chatId) }
                    )
                }
            }
        }
    }
}