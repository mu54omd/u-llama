package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun ChatBottomBar(
    modifier: Modifier = Modifier,
    textValue: String,
    onValueChange: (String) -> Unit,
    isSendingFailed: Boolean,
    isResponding: Boolean,
    onSendClick: () -> Unit,
    onClearClick: () -> Unit,
    onAttachClick: () -> Unit,
    isModelSelected: Boolean,
) {
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.98f,),
            MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.98f),
            ),
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .padding(10.dp)
            .navigationBarsPadding()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        brush = brush,
                        cornerRadius = CornerRadius(50f, 50f)
                    )
                }
        ) {
            CustomButton(
                description = "Attach Button",
                icon = R.drawable.baseline_attach_file_24,
                buttonSize = 50,
                iconSize = 25,
                onButtonClick = onAttachClick,
                containerColor = Color.Transparent
            )
            TextField(
                value = textValue,
                onValueChange = onValueChange,
                shape = MaterialTheme.shapes.extraLarge,
                placeholder = { Text(text = "Type your message...") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    ),
                trailingIcon = {
                    if(textValue != ""){
                        CustomButton(
                            description = "Clear Button",
                            onButtonClick = onClearClick,
                            icon = R.drawable.baseline_clear_24,
                            containerColor = Color.Transparent
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                ,
                maxLines = 3,
                )
            CustomButton(
                description = "Send Button",
                icon = when{
                    isSendingFailed -> R.drawable.baseline_refresh_24
                    isResponding -> R.drawable.baseline_stop_24
                    else -> R.drawable.baseline_send_24
                           },
                buttonSize = 50,
                iconSize = 25,
                onButtonClick = onSendClick,
                containerColor = Color.Transparent,
                isButtonEnabled = ((textValue != "")|| isSendingFailed || isResponding) && isModelSelected
            )
        }
    }
}

@Preview
@Composable
private fun ChatBottomBarPreview() {
    OllamaUITheme {
        ChatBottomBar(
            textValue = LoremIpsum(500).values.joinToString(),
            onValueChange = {},
            onAttachClick = {},
            onSendClick = {},
            onClearClick = {},
            isModelSelected = true,
            isSendingFailed = false,
            isResponding = true,
        )
    }
    
}
