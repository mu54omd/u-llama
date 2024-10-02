package com.example.ollamaui.ui.screen.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun LoadingScreen(
    isLocalSettingLoaded: Boolean = false,
    onDispose: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        CircularProgressIndicator()
        if(isLocalSettingLoaded){
            onDispose()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingScreenPreview() {
    OllamaUITheme {
        LoadingScreen(
            isLocalSettingLoaded = false,
            onDispose = {}
        )
    }
}

