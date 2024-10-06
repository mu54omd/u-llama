package com.example.ollamaui.ui.screen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun CustomDropUpMenu(
    modifier: Modifier = Modifier,
    listItems: List<String> = emptyList(),
    onItemClick: (String) -> Unit,
    ) {
    val width = listItems.maxByOrNull { it.length }?.length?.times(11) ?: 100
    Box(
        modifier = modifier
            .size(width = width.dp, height = 150.dp)
            .clip(shape = MaterialTheme.shapes.large)
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(2.dp).verticalScroll(state = rememberScrollState())
        ) {
            listItems.forEach { item ->
                Row {
                    TextButton(
                        onClick = {onItemClick(item)},
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(text = item)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CustomDropUpMenuPreview() {
    OllamaUITheme {
        Column {
            CustomDropUpMenu(
                listItems = listOf("first item", "second item", "third item", "fourth item", "11111111111"),
                onItemClick = {},
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomDropUpMenu(
                listItems = emptyList(),
                onItemClick = {},
            )
        }
    }
}