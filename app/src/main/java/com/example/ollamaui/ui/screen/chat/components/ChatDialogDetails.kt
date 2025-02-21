package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ChatDialogDetails(
    isVisible: Boolean,
    isFromMe: Boolean,
    date: String,
    time: String,
) {
    val finalDate = when (date) {
        LocalDate.now().toString() -> { "Today" }
        LocalDate.now().minusDays(1).toString() -> {"Yesterday"}
        else -> date
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier.align(alignment = if(isFromMe) Alignment.CenterEnd else Alignment.CenterStart),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Text(
                text = "$finalDate ${time.split(".")[0]}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}