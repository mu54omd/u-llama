package com.example.ollamaui.ui.screen.home.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun LogoTitle(
    modifier: Modifier = Modifier,
    @DrawableRes logo: Int,
    text: String,
) {
    var isVisible by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()

    ) {
            Image(
                painter = painterResource(logo),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .size(width = 96.dp, height = 64.dp)
                    .pointerInput(isVisible){detectTapGestures { isVisible = !isVisible }}
            )
        AnimatedVisibility(visible = isVisible) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview
@Composable
private fun LogoTitlePreview() {
    OllamaUITheme {
        LogoTitle(
            logo = R.drawable.icon,
            text = "Title Logo"
        )
    }
}