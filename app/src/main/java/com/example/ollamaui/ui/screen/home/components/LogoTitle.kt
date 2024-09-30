package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun LogoTitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxHeight()
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Preview
@Composable
private fun LogoTitlePreview() {
    OllamaUITheme {
        LogoTitle(
            text = "Title Logo"
        )
    }
}