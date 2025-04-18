package com.mu54omd.ullama.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.screen.common.CustomButton
import com.mu54omd.ullama.ui.theme.OllamaUITheme

@Composable
fun ChatBottomBar(
    modifier: Modifier = Modifier,
    textValue: String,
    onValueChange: (String) -> Unit,
    isSendingFailed: Boolean,
    isResponding: Boolean,
    onSendClick: () -> Unit,
    onClearClick: () -> Unit,
    onLineCountChange: (Int) -> Unit,
    onAttachClick: () -> Unit,
    isModelSelected: Boolean,
) {
    val brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.inversePrimary,
        ),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(75f, 75f)
                )
            }
    ) {
        CustomButton(
            description = "Attach Button",
            icon = R.drawable.baseline_attach_file_24,
            buttonSize = 50,
            iconSize = 25,
            onButtonClick = onAttachClick,
            containerColor = Color.Transparent,
        )
        CustomTextField(
            value = textValue,
            onValueChange = onValueChange,
            placeHolder = "Type your message...",
            onLineCountChange = onLineCountChange,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            AnimatedVisibility(
                visible = textValue != "",
                enter = scaleIn(),
                exit = scaleOut()
                ) {
                CustomButton(
                    description = "Clear Button",
                    onButtonClick = onClearClick,
                    icon = R.drawable.baseline_clear_24,
                    containerColor = Color.Transparent,
                )
            }
            CustomButton(
                description = "Send Button",
                icon = when {
                    isSendingFailed -> R.drawable.baseline_refresh_24
                    isResponding -> R.drawable.baseline_stop_24
                    else -> R.drawable.baseline_send_24
                },
                buttonSize = 50,
                iconSize = 25,
                onButtonClick = onSendClick,
                containerColor = Color.Transparent,
                isButtonEnabled = ((textValue != "") || isSendingFailed || isResponding) && isModelSelected,
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
//            textValue = "",
            onValueChange = {},
            onAttachClick = {},
            onSendClick = {},
            onClearClick = {},
            onLineCountChange = {},
            isModelSelected = true,
            isSendingFailed = false,
            isResponding = true,
        )
    }

}
