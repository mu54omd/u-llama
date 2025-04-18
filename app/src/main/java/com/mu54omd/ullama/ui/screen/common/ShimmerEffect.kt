package com.mu54omd.ullama.ui.screen.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.ui.theme.OllamaUITheme

@Composable
fun Modifier.shimmerEffect(
    enable: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    initialDelay: Int = 0
) = composed {
    if(enable) {
        val transition = rememberInfiniteTransition(label = "")
        val alpha = transition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500,
                    delayMillis = initialDelay,
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        ).value
        background(color = color.copy(alpha = alpha))
    }else{
        Modifier
    }
}

@Preview
@Composable
private fun ShimmerEffectPreview() {
    OllamaUITheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = MaterialTheme.shapes.medium)
                        .shimmerEffect(
                            enable = true,
                            MaterialTheme.colorScheme.errorContainer,
                            initialDelay = 30,
                        )
                )
            }
        }
    }
}