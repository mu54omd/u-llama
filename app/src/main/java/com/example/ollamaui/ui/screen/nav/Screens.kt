package com.example.ollamaui.ui.screen.nav

sealed class Screens(val route: String) {
    data object HomeScreen: Screens("HomeScreen")
    data object ChatScreen: Screens("ChatScreen")
    data object LoadingScreen: Screens("LoadingScreen")
    data object SettingScreen: Screens("SettingScreen")
}