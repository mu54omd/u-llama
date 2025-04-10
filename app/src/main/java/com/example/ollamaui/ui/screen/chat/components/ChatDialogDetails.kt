package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import java.time.LocalDate

@Composable
fun ChatDialogDetails(
    isVisible: Boolean,
    isFromMe: Boolean,
    isLastBotMessage: Boolean,
    isLastUserMessage: Boolean,
    date: String,
    time: String,
    onReproduceResponse: () -> Unit,
    onDeleteLastMessage: () -> Unit,
    onEditLastMessage: () -> Unit,
    isResponding: Boolean
) {
    val finalDate = when (date) {
        LocalDate.now().toString() -> { "" }
        LocalDate.now().minusDays(1).toString() -> { "Yesterday" }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when{
                    isLastUserMessage -> {
                        CustomButton(
                            onButtonClick = onDeleteLastMessage,
                            description = "delete last message",
                            icon = R.drawable.baseline_delete_outline_24,
                            iconSize = 18,
                            isButtonEnabled = !isResponding
                        )
                        CustomButton(
                            onButtonClick = onEditLastMessage,
                            description = "edit last message",
                            icon = R.drawable.baseline_edit_24,
                            iconSize = 18,
                            isButtonEnabled = !isResponding
                        )
                        Text(
                            text = "$finalDate ${time.substringBeforeLast(":")}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                    isLastBotMessage -> {
                        Text(
                            text = "$finalDate ${time.substringBeforeLast(":")}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(5.dp)
                        )
                        CustomButton(
                            onButtonClick = onReproduceResponse,
                            description = "reproduce response",
                            icon = R.drawable.baseline_sync_24,
                            iconSize = 18,
                            isButtonEnabled = !isResponding
                        )
                    }
                    else -> {
                        Text(
                            text = "$finalDate ${time.substringBeforeLast(":")}",
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
fun ChatDialogDetailsPreview() {
    Surface {
        Column {
            ChatDialogDetails(
                isVisible = true,
                isFromMe = true,
                isLastBotMessage = false,
                isLastUserMessage = false,
                date = "Date",
                time = "Time",
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
            ChatDialogDetails(
                isVisible = true,
                isFromMe = false,
                isLastBotMessage = false,
                isLastUserMessage = false,
                date = "Date",
                time = "Time",
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
            ChatDialogDetails(
                isVisible = true,
                isFromMe = true,
                isLastBotMessage = false,
                isLastUserMessage = true,
                date = "Date",
                time = "Time",
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
            ChatDialogDetails(
                isVisible = true,
                isFromMe = false,
                isLastBotMessage = true,
                isLastUserMessage = false,
                date = "Date",
                time = "Time",
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
        }
    }
}