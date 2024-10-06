package com.example.ollamaui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ollamaui.ui.screen.loading.LoadingScreen
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
            OllamaUITheme {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    mainState = mainState,
                    isOllamaAddressSet = baseAddress.isOllamaAddressSet,
                    ollamaAddress = baseAddress.ollamaBaseAddress,
                    isLocalSettingsLoaded = baseAddress.isLocalSettingsLoaded
                )

            }
        }
    }
}