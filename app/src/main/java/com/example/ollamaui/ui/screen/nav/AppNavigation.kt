package com.example.ollamaui.ui.screen.nav

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ollamaui.activity.BaseAddress
import com.example.ollamaui.activity.EmbeddingModel
import com.example.ollamaui.activity.MainStates
import com.example.ollamaui.activity.MainViewModel
import com.example.ollamaui.domain.model.chat.ModelParameters
import com.example.ollamaui.helper.NetworkStatus
import com.example.ollamaui.ui.screen.chat.ChatScreen
import com.example.ollamaui.ui.screen.chat.ChatViewModel
import com.example.ollamaui.ui.screen.chat.components.EmptyChatScreen
import com.example.ollamaui.ui.screen.files.FilesScreen
import com.example.ollamaui.ui.screen.files.FilesViewModel
import com.example.ollamaui.ui.screen.home.HomeScreen
import com.example.ollamaui.ui.screen.home.HomeViewModel
import com.example.ollamaui.ui.screen.loading.LoadingScreen
import com.example.ollamaui.ui.screen.log.LogScreen
import com.example.ollamaui.ui.screen.log.LogViewModel
import com.example.ollamaui.ui.screen.setting.SettingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    mainState: State<MainStates>,
    baseAddress: State<BaseAddress>,
    embeddingModel: State<EmbeddingModel>,
    modelParameters: State<ModelParameters>,
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val chatsList = homeViewModel.chatsList.collectAsStateWithLifecycle()
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatState = chatViewModel.chatState.collectAsStateWithLifecycle()
    val attachedFilesList = chatViewModel.attachedFiles.collectAsStateWithLifecycle()
    val logViewModel: LogViewModel = hiltViewModel()
    val fileViewModel: FilesViewModel = hiltViewModel()
    val fileContent = fileViewModel.output.collectAsState()
    val selectedFile = fileViewModel.selectedFile.collectAsState()

    val networkStatus = mainViewModel.networkStatus.collectAsState()

    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val scope = rememberCoroutineScope()
    var selectedChatId by rememberSaveable { mutableIntStateOf(-1) }

    val activity = LocalActivity.current
    val snackbarHostState  = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){ snackbarData ->
                Snackbar(
                    shape = RectangleShape,
                ){
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = snackbarData.visuals.message, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.LoadingScreen.route,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .safeDrawingPadding(),

        ) {
            composable(
                route = Screens.HomeScreen.route,
            ) {
                NavigableListDetailPaneScaffold(
                    navigator = navigator,
                    listPane = {
                        AnimatedPane {
                            HomeScreen(
                                chatsList = chatsList,
                                onChatClick = {
                                    val chatModel = homeViewModel.findChat(chatId = it.chatId) ?: it
                                    chatViewModel.loadStates(
                                        chatModel = chatModel,
                                        url = baseAddress.value.ollamaBaseAddress,
                                        modelParameters = modelParameters.value
                                    )
                                    selectedChatId = chatModel.chatId
                                    scope.launch {
                                        navigator.navigateTo(
                                            pane = ListDetailPaneScaffoldRole.Detail,
                                            contentKey = chatModel.chatId
                                        )
                                    }
                                },
                                onDeleteChatClick = { chatModel ->
                                    homeViewModel.deleteChat(chatModel)
                                    if (selectedChatId == navigator.currentDestination?.contentKey) {
                                        selectedChatId = -1
                                    }
                                },
                                onDeleteChatByIdClick = { chatId ->
                                    homeViewModel.deleteChatById(chatId)
                                    if (selectedChatId == navigator.currentDestination?.contentKey) {
                                        selectedChatId = -1
                                    }
                                },
                                onAddNewChatClick = { chatTitle, systemPrompt, selectedModel ->
                                    homeViewModel.addNewChat(chatTitle, systemPrompt, selectedModel)
                                },
                                onRefreshClick = { mainViewModel.refresh() },
                                onSettingClick = {
                                    navigateToTab(
                                        navController = navController,
                                        route = Screens.SettingScreen.route
                                    )
                                },
                                onLogClick = {
                                    navigateToTab(
                                        navController = navController,
                                        route = Screens.LogScreen.route
                                    )
                                },
                                networkStatus = networkStatus.value,
                                isChatReady = mainState.value.isModelListLoaded and (networkStatus.value == NetworkStatus.CONNECTED),
                                modelList = mainState.value.filteredModelList,
                                onBackClick = { backHandlerCounter ->
                                    if(backHandlerCounter >= 2) {
                                        activity?.finish()
                                    }else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Perform back function again to exit the app!",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            if (selectedChatId == navigator.currentDestination?.contentKey) {
                                ChatScreen(
                                    chatViewModel = chatViewModel,
                                    chatState = chatState,
                                    attachedFilesList = attachedFilesList,
                                    embeddingModel = embeddingModel,
                                    onBackClick = {
                                        if(navigator.scaffoldState.currentState.tertiary == PaneAdaptedValue.Hidden){
                                            chatViewModel.clearStates()
                                            selectedChatId = if(windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED) -2 else -1
                                            scope.launch {
                                                navigator.navigateTo(
                                                    pane = ListDetailPaneScaffoldRole.List,
                                                )
                                            }
                                        }else{
                                            scope.launch {
                                                navigator.navigateBack()
                                            }
                                        }
                                    },
                                    onFileClick = { file ->
                                        fileViewModel.selectFile(file)
                                        fileViewModel.prepareFile(file = file)
                                        scope.launch {
                                            navigator.navigateTo(
                                                pane = ListDetailPaneScaffoldRole.Extra,
                                                contentKey = selectedChatId
                                            )
                                        }
                                    }
                                )
                            } else if( selectedChatId == -1 || windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                                EmptyChatScreen()
                            }
                        }
                    },
                    extraPane = {
                        AnimatedPane {
                            FilesScreen(
                                fileContent = fileContent,
                                file = selectedFile
                            )
                        }
                    },
                )
            }
            composable(route = Screens.LoadingScreen.route){
                LoadingScreen(
                    isLocalSettingLoaded = baseAddress.value.isLocalSettingsLoaded,
                    onDispose = {
                        mainViewModel.refresh()
                        navigateToTab(navController = navController , route = Screens.HomeScreen.route)
                    }
                )
            }
            composable(
                route = Screens.SettingScreen.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                }
                ){
                SettingScreen(
                    savedParameters = listOf(
                        baseAddress.value.ollamaBaseAddress,
                        embeddingModel.value.embeddingModelName,
                        modelParameters.value.temperature.toString(),
                        modelParameters.value.numCtx.toString(),
                        modelParameters.value.presencePenalty.toString(),
                        modelParameters.value.frequencyPenalty.toString(),
                        modelParameters.value.topK.toString(),
                        modelParameters.value.topP.toString(),
                        modelParameters.value.minP.toString(),
                    ),
                    embeddingModelList = mainState.value.embeddingModelList,
                    isEmbeddingModelPulled = { mainViewModel.checkIfEmbeddingModelPulled(it) },
                    onSaveClick = { url, embeddingModelName, modelParameters->
                        mainViewModel.saveOllamaAddress(url = url)
                        if(embeddingModelName.isNotEmpty()) {
                            mainViewModel.saveOllamaEmbeddingModel(modelName = embeddingModelName)
                        }
                        mainViewModel.saveOllamaTuningParameters(modelParameters = modelParameters)
                    },
                    onCheckClick = {url ->
                        mainViewModel.checkOllamaAddress(url)
                    },
                    onFetchEmbeddingModelClick = { mainViewModel.fetchEmbeddingModelList() },
                    onPullEmbeddingModelClick = { mainViewModel.pullEmbeddingModel(it) },
                    ollamaStatus = mainState.value.ollamaStatus,
                    onBackClick = {
                        mainViewModel.refresh()
                        navigateToTab(
                            navController = navController,
                            route = Screens.HomeScreen.route
                        )
                    }
                )
            }
            composable(
                route = Screens.LogScreen.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }
            ){
                LogScreen(
                    logViewModel = logViewModel,
                    onBackClick = {
                        navigateToTab(
                            navController = navController,
                            route = Screens.HomeScreen.route
                        )
                    }
                )
            }
        }
    }
}
private fun navigateToTab(navController: NavController, route: String){
    navController.navigate(route){
        popUpTo(0)
    }
}

