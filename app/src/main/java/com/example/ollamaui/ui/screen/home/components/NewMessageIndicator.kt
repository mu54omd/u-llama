package com.example.ollamaui.ui.screen.home.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun NewMessageIndicator(
    modifier: Modifier = Modifier,
    isNewMessageReceived: Boolean,
    color: Color,
    size: Int,
) {
    val animatedSize by animateIntAsState(
        targetValue = if(isNewMessageReceived) size else 0,
        animationSpec = tween(),
        label = "Animated Size"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(color = color)
            .size(animatedSize.dp)
    )
}

@Preview
@Composable
private fun NewMessageIndicatorPreview() {
    OllamaUITheme {
        NewMessageIndicator(
            isNewMessageReceived = true,
            color = Color.Green,
            size = 15
        )
    }
}