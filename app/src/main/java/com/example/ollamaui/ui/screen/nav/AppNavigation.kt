package com.example.ollamaui.ui.screen.nav

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ollamaui.activity.MainStates
import com.example.ollamaui.activity.MainViewModel
import com.example.ollamaui.ui.screen.chat.ChatScreen
import com.example.ollamaui.ui.screen.chat.ChatViewModel
import com.example.ollamaui.ui.screen.home.HomeScreen
import com.example.ollamaui.ui.screen.home.HomeViewModel
import com.example.ollamaui.ui.screen.loading.LoadingScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    mainState: MainStates,
    isOllamaAddressSet: Boolean,
    ollamaAddress: String,
    isLocalSettingsLoaded: Boolean
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val chatsList = homeViewModel.chatsList.collectAsStateWithLifecycle().value
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatState = chatViewModel.chatState.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {},
        bottomBar = {},
        snackbarHost = {},
        modifier = Modifier
    ) {
        NavHost(
            navController = ,
            startDestination = Screens.LoadingScreen.route,
            modifier = Modifier
                .background( color = MaterialTheme.colorScheme.background )
                .safeDrawingPadding()
        ) {
            composable(route = Screens.HomeScreen.route) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    mainViewModel = mainViewModel,
                    chatsList = chatsList,
                    onChatClick = {
                        chatViewModel.loadStates(chatModel = it, url = ollamaAddress)
                        navigateToTab(
                            navController = navController,
                            route = Screens.ChatScreen.route
                        )
                    },
                    isOllamaAddressSet = isOllamaAddressSet,
                    ollamaAddress = ollamaAddress,
                    onSaveOllamaAddressClick = {
                        mainViewModel.saveLocalSetting(it)
                    },
                    isModelListLoaded = mainState.isModelListLoaded,
                    modelList = mainState.modelList,
                    tagError = mainState.tagError,
                    statusError = mainState.statusError,
                    statusThrowable = mainState.statusThrowable,
                )
            }

            composable(route = Screens.ChatScreen.route) {
                ChatScreen(
                    chatViewModel = chatViewModel,
                    chatState = chatState,
                    onBackClick = {
                        chatViewModel.clearStates()
                        navigateToTab(
                            navController = navController,
                            route = Screens.HomeScreen.route
                        )
                    }
                )
            }
            composable(route = Screens.LoadingScreen.route){
                LoadingScreen(
                    isLocalSettingLoaded = isLocalSettingsLoaded,
                    onDispose = {
                        navigateToTab(navController = navController , route = Screens.HomeScreen.route)
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

