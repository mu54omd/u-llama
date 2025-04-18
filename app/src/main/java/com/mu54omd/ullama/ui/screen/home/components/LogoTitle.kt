package com.mu54omd.ullama.ui.screen.home.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.helper.network.NetworkStatus
import com.mu54omd.ullama.ui.theme.OllamaUITheme
import kotlinx.coroutines.delay

@Composable
fun LogoTitle(
    modifier: Modifier = Modifier,
    @DrawableRes lightLogo: Int,
    @DrawableRes darkLogo: Int,
    networkStatus: NetworkStatus,
) {
    val animatedColor by animateColorAsState(
        targetValue = when (networkStatus) {
            NetworkStatus.CONNECTED -> MaterialTheme.colorScheme.tertiary
            NetworkStatus.DISCONNECTED -> MaterialTheme.colorScheme.error
            NetworkStatus.UNKNOWN -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "Animated title Color"
    )
    var title = remember {
        mutableStateOf(
            when (networkStatus) {
                NetworkStatus.CONNECTED -> NetworkStatus.CONNECTED.name
                NetworkStatus.DISCONNECTED -> NetworkStatus.DISCONNECTED.name
                NetworkStatus.UNKNOWN -> "CONNECTING"
            }
        )
    }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(networkStatus) {
        title.value = when (networkStatus) {
            NetworkStatus.CONNECTED -> "CONNECTED"
            NetworkStatus.DISCONNECTED -> "DISCONNECTED"
            NetworkStatus.UNKNOWN -> "CONNECTING"
        }
        isVisible = true
    }
    LaunchedEffect(isVisible) {
        delay(3000)
        isVisible = false
        if(title.value != "Ollama UI"){
            delay(50)
            title.value = "Ollama UI"
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
            Image(
                painter = painterResource(if(isSystemInDarkTheme()) darkLogo else lightLogo),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .height(64.dp)
                    .aspectRatio(2f)
                    .pointerInput(isVisible) { detectTapGestures { isVisible = !isVisible } },
            )
        AnimatedVisibility(visible = isVisible) {
            Text(
                text = title.value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = animatedColor
                        )
                    }
                    .padding(start = 3.dp, end = 3.dp)

            )
        }
    }
}

@Preview
@Composable
private fun LogoTitlePreview() {
    OllamaUITheme {
        LogoTitle(
            lightLogo = R.drawable.icon_light,
            darkLogo = R.drawable.icon_dark,
            networkStatus = NetworkStatus.CONNECTED
        )
    }
}