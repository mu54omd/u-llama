package com.mu54omd.ullama.ui.screen.log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.ui.theme.ULlamaTheme

@Composable
fun LogItem(
    date: String,
    type: String,
    content: String,
) {
    val bgColor = when (type) {
        "ERROR" -> MaterialTheme.colorScheme.error
        "SUCCESS" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
    Row(
        modifier = Modifier
            .padding(5.dp)
            .height(60.dp)
            .fillMaxWidth()
            .background(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
            )
    ) {
        Column(
            modifier = Modifier
                .background(color = bgColor)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = type.substring(0,2),
                modifier = Modifier.padding(5.dp),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.background),
                fontWeight = FontWeight.Bold
            )
        }
        VerticalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.background)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(5.dp),
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.background)
            Text(
                text = content,
                modifier = Modifier.padding(5.dp),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Preview
@Composable
private fun LogItemPreview() {
    ULlamaTheme {
        Surface {
            Column {
                LogItem(
                    date = "2025-03-12",
                    type = "START",
                    content = "Short Log content"
                )
                LogItem(
                    date = "2025-03-12",
                    type = "SUCCESS",
                    content = "Long Log content that fit to box!"
                )
                LogItem(
                    date = "2025-03-12",
                    type = "ERROR",
                    content = "Very Long Log content that can't fit in to place holder and some part of it have to be hidden!"
                )
            }
        }
    }
}