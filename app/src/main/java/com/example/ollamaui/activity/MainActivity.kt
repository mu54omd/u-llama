package com.example.ollamaui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ollamaui.ui.screen.nav.AppNavigation
import com.example.ollamaui.ui.theme.OllamaUITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val mainState = mainViewModel.mainState.collectAsStateWithLifecycle().value
            val baseAddress = mainViewModel.baseAddress.collectAsStateWithLifecycle().value
            val embeddingModel = mainViewModel.embeddingModel.collectAsStateWithLifecycle().value
            OllamaUITheme {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    mainState = mainState,
                    ollamaAddress = baseAddress.ollamaBaseAddress,
                    isLocalSettingsLoaded = baseAddress.isLocalSettingsLoaded,
                    isEmbeddingModelSet = embeddingModel.isEmbeddingModelSet,
                    embeddingModel = embeddingModel.embeddingModelName
                )
            }
        }
    }
}