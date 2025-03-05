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
            val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()
            val baseAddress = mainViewModel.baseAddress.collectAsStateWithLifecycle()
            val embeddingModel = mainViewModel.embeddingModel.collectAsStateWithLifecycle()
            val modelParameters = mainViewModel.tuningParameters.collectAsStateWithLifecycle()
            OllamaUITheme {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    mainState = mainState.value,
                    ollamaAddress = baseAddress.value.ollamaBaseAddress,
                    isLocalSettingsLoaded = baseAddress.value.isLocalSettingsLoaded,
                    isEmbeddingModelSet = embeddingModel.value.isEmbeddingModelSet,
                    embeddingModel = embeddingModel.value.embeddingModelName,
                    modelParameters = modelParameters.value
                )
            }
        }
    }
}