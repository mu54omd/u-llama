package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton

@Composable
fun ChatBottomBar(
    modifier: Modifier = Modifier,
    textValue: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onClearClick: () -> Unit,
    onAttachClick: () -> Unit,
    isModelSelected: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = modifier.padding(start = 10.dp, end = 20.dp, top = 10.dp)
        ) {
            CustomButton(
                description = "Attach Button",
                icon = R.drawable.baseline_attach_file_24,
                buttonSize = 50,
                iconSize = 25,
                onButtonClick = onAttachClick
            )
            Spacer(modifier = Modifier.width(5.dp))
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
                modifier = Modifier.weight(1f),
                maxLines = 5
                )
            Spacer(modifier = Modifier.width(10.dp))
            CustomButton(
                description = "Send Button",
                icon = R.drawable.baseline_send_24,
                buttonSize = 50,
                iconSize = 25,
                onButtonClick = onSendClick,
                isButtonEnabled = (textValue != "") && isModelSelected
            )
        }
    }
}
