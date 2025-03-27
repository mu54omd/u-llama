package com.example.ollamaui.ui.screen.log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun LogItem(
    id: Int,
    date: String,
    type: String,
    content: String,
) {
    val bgColor = when(type){
        "ERROR" -> MaterialTheme.colorScheme.errorContainer
        "SUCCESS" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    Column (
        modifier = Modifier
            .padding(5.dp)
            .background(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
            )
    ) {
            Row(
                modifier = Modifier.background(color = bgColor).fillMaxWidth().wrapContentHeight(),
            ) {
                Text(
                    text = id.toString(),
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = type,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = date,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column {

                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                    Text(
                        text = content,
                        modifier = Modifier.padding(5.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }



    }
}

@Preview
@Composable
private fun LogItemPreview() {
    OllamaUITheme {
        Surface {
            LogItem(
                id = 1012,
                date = "2025-03-12",
                type = "START",
                content = "Log content"
            )
        }
    }
}