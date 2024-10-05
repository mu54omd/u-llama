package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.Message
import com.example.ollamaui.ui.theme.OllamaUITheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@Composable
fun ChatDialog(
    message: Message,
    modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .align(if (message.isFromMe) Alignment.End else Alignment.Start)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (message.isFromMe) 48f else 0f,
                        bottomEnd = if (message.isFromMe) 0f else 48f
                    )
                )
                .clickable {
                    isVisible = !isVisible
                }
                .background(color = if(message.isFromMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = if(message.isFromMe) Alignment.End else Alignment.Start
            ) {
                Text(text = message.text)
                AnimatedVisibility(
                    visible = isVisible
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        )
                    ) {
                        Text(
                            text = message.author.name,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatDialogPreview() {
    OllamaUITheme {
        Column {
            ChatDialog(
                message = Message(
                    text = "Hi. How are you???",
                    author = Author(
                        id = 1,
                        name = "Musa"
                    )
                )
            )
            ChatDialog(
                message = Message(
                    text = "Hello! I'm fine.",
                    author = Author(
                        id = 0,
                        name = "Musashi"
                    )
                )
            )
        }
    }

}