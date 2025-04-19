package com.mu54omd.ullama.ui.screen.loading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.theme.ULlamaTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    isLocalSettingLoaded: Boolean = false,
    onClose: () -> Unit,
    navigateTo: () -> Unit,
) {
    var shouldDisplayDetails by remember(isLocalSettingLoaded) { mutableStateOf(isLocalSettingLoaded) }
    val imageAlpha by animateFloatAsState(
        targetValue = if (isLocalSettingLoaded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
        ),
        label = ""
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "App Logo Light",
            modifier = Modifier
                .size(width = 350.dp, height = 200.dp)
                .graphicsLayer {
                    alpha = imageAlpha
                },
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "u",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.graphicsLayer {
                    alpha = imageAlpha
                }
            )
            AnimatedVisibility(
                visible = shouldDisplayDetails,
                enter = expandHorizontally(animationSpec = tween(delayMillis = 500)),
                exit = shrinkHorizontally(animationSpec = tween(delayMillis = 200))
            ) {
                Text(
                    text = "ser interface for O",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.graphicsLayer {
                        alpha = imageAlpha
                    }
                        .offset(x = 0.dp, y = (-2).dp)
                        .padding(start = 1.dp)
                )
            }
            Text(
                text = "llama",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.graphicsLayer {
                    alpha = imageAlpha
                }
            )
        }

        LaunchedEffect(isLocalSettingLoaded) {
            if (isLocalSettingLoaded) {
                onClose()
                delay(2000)
                shouldDisplayDetails = false
                delay(1000)
                navigateTo()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingScreenPreview() {
    ULlamaTheme {
        LoadingScreen(
            isLocalSettingLoaded = true,
            onClose = {},
            navigateTo = {}
        )
    }
}

