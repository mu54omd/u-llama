package com.mu54omd.ullama.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mu54omd.ullama.ui.screen.nav.AppNavigation
import com.mu54omd.ullama.ui.theme.ULlamaTheme
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
            ULlamaTheme {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    mainState = mainState,
                    baseAddress = baseAddress,
                    embeddingModel = embeddingModel,
                    modelParameters = modelParameters
                )
            }
        }
    }
}