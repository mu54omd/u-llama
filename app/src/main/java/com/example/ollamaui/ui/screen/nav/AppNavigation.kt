package com.example.ollamaui.ui.screen.nav

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneMotionDefaults
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ollamaui.activity.MainStates
import com.example.ollamaui.activity.MainViewModel
import com.example.ollamaui.domain.model.chat.ModelParameters
import com.example.ollamaui.ui.screen.chat.ChatScreen
import com.example.ollamaui.ui.screen.chat.ChatViewModel
import com.example.ollamaui.ui.screen.chat.components.EmptyChatScreen
import com.example.ollamaui.ui.screen.home.HomeScreen
import com.example.ollamaui.ui.screen.home.HomeViewModel
import com.example.ollamaui.ui.screen.home.components.LogScreen
import com.example.ollamaui.ui.screen.loading.LoadingScreen
import com.example.ollamaui.ui.screen.log.LogViewModel
import com.example.ollamaui.ui.screen.setting.SettingScreen
import com.example.ollamaui.utils.Constants.OLLAMA_IS_RUNNING
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    mainState: MainStates,
    ollamaAddress: String,
    isEmbeddingModelSet: Boolean,
    embeddingModel: String,
    modelParameters: ModelParameters,
    isLocalSettingsLoaded: Boolean
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val chatsList = homeViewModel.chatsList.collectAsStateWithLifecycle().value
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatState = chatViewModel.chatState.collectAsStateWithLifecycle().value
    val attachedDocsList = chatViewModel.attachedDocs.collectAsStateWithLifecycle().value
    val attachedImagesList = chatViewModel.attachedImages.collectAsStateWithLifecycle().value
    val logViewModel: LogViewModel = hiltViewModel()
    val logs = logViewModel.logs.collectAsStateWithLifecycle().value

    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val scope = rememberCoroutineScope()
    var selectedChatId by rememberSaveable { mutableIntStateOf(-1) }

    val activity = LocalActivity.current


    Scaffold {
        NavHost(
            navController = navController,
            startDestination = Screens.LoadingScreen.route,
            modifier = Modifier
                .background( color = MaterialTheme.colorScheme.background )
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
                                        url = ollamaAddress,
                                        modelParameters = modelParameters
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
                                isChatReady = mainState.isModelListLoaded and (mainState.ollamaStatus == OLLAMA_IS_RUNNING),
                                modelList = mainState.filteredModelList,
                                onBackClick = {
                                    activity?.finish()
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
                                    attachedFilesList = attachedDocsList,
                                    attachedImagesList = attachedImagesList,
                                    embeddingModel = embeddingModel,
                                    isEmbeddingModelSet = isEmbeddingModelSet,
                                    onBackClick = {
                                        chatViewModel.clearStates()
                                        selectedChatId = if(windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.EXPANDED) -2 else -1
                                        scope.launch {
                                            navigator.navigateTo(
                                                pane = ListDetailPaneScaffoldRole.List,
                                            )
                                        }
                                    }
                                )
                            } else if( selectedChatId == -1 || windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                                EmptyChatScreen()
                            }
                        }
                    },
                )
            }
            composable(route = Screens.LoadingScreen.route){
                LoadingScreen(
                    isLocalSettingLoaded = isLocalSettingsLoaded,
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
                        ollamaAddress,
                        embeddingModel,
                        modelParameters.temperature.toString(),
                        modelParameters.numCtx.toString(),
                        modelParameters.presencePenalty.toString(),
                        modelParameters.frequencyPenalty.toString(),
                        modelParameters.topK.toString(),
                        modelParameters.topP.toString(),
                        modelParameters.minP.toString(),
                    ),
                    embeddingModelList = mainState.embeddingModelList,
                    isEmbeddingModelPulled = { mainViewModel.checkIfEmbeddingModelPulled(it) },
                    onSaveClick = { url, embeddingModelName, modelParameters->
                        mainViewModel.saveOllamaAddress(url = url)
                        mainViewModel.saveOllamaEmbeddingModel(modelName = embeddingModelName)
                        mainViewModel.saveOllamaTuningParameters(modelParameters = modelParameters)
                        mainViewModel.refresh()
                    },
                    onCheckClick = {url ->
                        mainViewModel.checkOllamaAddress(url)
                    },
                    onFetchEmbeddingModelClick = { mainViewModel.fetchEmbeddingModelList() },
                    onPullEmbeddingModelClick = { mainViewModel.pullEmbeddingModel(it) },
                    ollamaStatus = mainState.ollamaStatus,
                    onBackClick = {
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
                    logs = logs,
                    onClearLogClick = {
                        logViewModel.deleteLogs()
                    },
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

