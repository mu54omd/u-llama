package com.example.ollamaui.ui.screen.loading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.theme.OllamaUITheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    isLocalSettingLoaded: Boolean = false,
    onClose: () -> Unit,
    navigateTo: () -> Unit,
) {
    val imageAlpha by animateFloatAsState(
        targetValue = if (isLocalSettingLoaded)  1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
        ),
        label = ""
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (imageAlpha == 1f)  1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
        ),
        label = ""
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ){

            Image(
                painter = painterResource(if(isSystemInDarkTheme()) R.drawable.icon_dark else R.drawable.icon_light),
                contentDescription = "App Logo Light",
                modifier = Modifier
                    .size(width = 350.dp, height = 200.dp)
                    .graphicsLayer{
                        alpha = imageAlpha
                    },
            )

        Row {
            Text(
                text = "Ollama UI",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.graphicsLayer{
                    alpha = textAlpha
                }
            )
        }
        LaunchedEffect(isLocalSettingLoaded) {
            if(isLocalSettingLoaded){
                onClose()
                delay(1500)
                navigateTo()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingScreenPreview() {
    OllamaUITheme {
        LoadingScreen(
            isLocalSettingLoaded = true,
            onClose = {},
            navigateTo = {}
        )
    }
}

