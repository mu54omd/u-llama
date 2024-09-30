package com.example.ollamaui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ollamaui.ui.screen.chat.ChatScreen
import com.example.ollamaui.ui.screen.home.HomeScreen
import com.example.ollamaui.ui.screen.nav.AppNavigation
import com.example.ollamaui.ui.theme.OllamaUITheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OllamaUITheme {
                AppNavigation()
            }
        }
    }
}