package com.example.ollamaui.ui.screen.loading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.theme.OllamaUITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoadingScreen(
    isLocalSettingLoaded: Boolean = false,
    onDispose: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ){
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier.size(250.dp)
        )
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            Text(text = "Initializing")
            Text(text = "...")
        }
        LaunchedEffect(isLocalSettingLoaded) {
            delay(3000L)
            if(isLocalSettingLoaded){
                onDispose()
            }
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

