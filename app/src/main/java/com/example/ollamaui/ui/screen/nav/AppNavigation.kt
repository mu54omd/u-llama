package com.example.ollamaui.ui.screen.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ollamaui.ui.screen.chat.ChatScreen
import com.example.ollamaui.ui.screen.chat.ChatViewModel
import com.example.ollamaui.ui.screen.home.HomeScreen
import com.example.ollamaui.ui.screen.home.HomeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState = homeViewModel.homeState.collectAsStateWithLifecycle().value

    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatState = chatViewModel.chatState.collectAsStateWithLifecycle().value


    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route
    ) {

        composable(route = Screens.HomeScreen.route){
            HomeScreen(
                homeViewModel = homeViewModel,
                homeState = homeState,
                onChatClick = {
                    chatViewModel.loadStates(it)
                    navigateToTab(navController = navController, route = Screens.ChatScreen.route)
                }
            )
        }

        composable(route = Screens.ChatScreen.route){
            ChatScreen(
                chatViewModel = chatViewModel,
                chatState = chatState,
                onBackClick = {
                    chatViewModel.uploadChatToDatabase(chatState.chatModel)
                    navigateToTab(navController = navController, route = Screens.HomeScreen.route)
                    chatViewModel.clearStates()
                }
            )
        }

    }

}
private fun navigateToTab(navController: NavController, route: String){
    navController.navigate(route){
        navController.graph.startDestinationRoute?.let { homeScreen ->
            popUpTo(homeScreen){
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}

