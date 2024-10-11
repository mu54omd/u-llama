package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    isModelSelected: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 10.dp)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(thickness = 1.dp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            CustomButton(
                description = "Attach Button",
                icon = R.drawable.baseline_attach_file_24,
                buttonSize = 50,
                iconSize = 25,
                onButtonClick = onAttachClick
            )
            TextField(
                value = textValue,
                onValueChange = onValueChange,
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                trailingIcon = {
                    if(textValue != ""){
                        CustomButton(
                            description = "Clear Button",
                            onButtonClick = onClearClick,
                            icon = R.drawable.baseline_clear_24,
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                ,
                maxLines = 5,
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
                isButtonEnabled = ((textValue != "")|| isSendingFailed) && isModelSelected
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
            isResponding = true
        )
    }
    
}
