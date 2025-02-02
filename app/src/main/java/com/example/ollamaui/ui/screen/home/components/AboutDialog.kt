package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.BuildConfig
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun AboutDialog(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onCloseClick() }
    ) {
        Box(
            modifier = modifier
            .clip(shape = MaterialTheme.shapes.large)
            .size(300.dp, 200.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Ollama UI", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "A simple interface for Ollama", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Version: ${BuildConfig.VERSION_NAME}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutDialogPreview() {
    OllamaUITheme {
        AboutDialog(
            onCloseClick = {}
        )
    }
}