package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun DotsPulsing(
    numberOfDots: Int = 3,
    dotSize: Dp = 7.dp,
    dotColor: Color = MaterialTheme.colorScheme.primaryContainer,
    delayUnit: Int = 200,
    spaceBetween: Dp = 2.dp
) {
    val duration = numberOfDots * delayUnit
    @Composable
    fun Dot(scale: Float) {
        Spacer(
            Modifier
                .size(dotSize)
                .scale(scale)
                .background(
                    color = dotColor,
                    shape = CircleShape
                )
        )
    }
    val infiniteTransition = rememberInfiniteTransition(label = "")

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(animation = keyframes {
            durationMillis = delayUnit * numberOfDots
            0f at delay using LinearEasing
            1f at delay + delayUnit using LinearEasing
            0f at delay + duration
        }), label = ""
    )

    val scales = arrayListOf<State<Float>>()
    for (i in 0 until numberOfDots) {
        scales.add(animateScaleWithDelay(delay = i * delayUnit))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        scales.forEach {
            Dot(it.value)
            Spacer(Modifier.width(spaceBetween))
        }
    }
}


@Preview
@Composable
private fun ChatLoadingPreview() {
    OllamaUITheme {
        DotsPulsing()
    }
}