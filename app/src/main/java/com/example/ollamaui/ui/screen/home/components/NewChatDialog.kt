package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun NewChatDialog(
    modifier: Modifier = Modifier,
    yourName: String,
    onYourNameChange: (String) -> Unit,
    botName: String,
    onBotNameChange: (String) -> Unit,
    systemPrompt: String,
    onSystemPromptChange: (String) -> Unit,
    chatTitle: String,
    onChatTitleChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit,
    maxChar: Int,
    ) {
    Dialog(
        onDismissRequest = { onCloseClick() },
    ) {
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.large)
                .size(300.dp, 500.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CustomTextField(value = chatTitle, onValueChange = onChatTitleChange, label = "Chat Title", maxChar = maxChar, onDone = {})
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(value = yourName, onValueChange = onYourNameChange, label = "Your Name", maxChar = maxChar, onDone = {})
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(value = botName, onValueChange = onBotNameChange, label = "Bot Name", maxChar = maxChar, onDone = {})
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(value = systemPrompt, onValueChange = onSystemPromptChange, label = "Describe the bot", maxChar = maxChar*6, onDone = {}, maxLines = 5)
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomButton(
                        description = "Accept",
                        onButtonClick = onAcceptClick,
                        icon = R.drawable.baseline_check_24,
                        buttonSize = 50,
                        isButtonEnabled = chatTitle.isNotEmpty() && yourName.isNotEmpty(),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    CustomButton(
                        description = "Close",
                        onButtonClick = onCloseClick,
                        icon = R.drawable.baseline_clear_24,
                        buttonSize = 50,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewChatDialogPreview() {
    OllamaUITheme {
        NewChatDialog(
            yourName = "Me",
            chatTitle = "Title",
            botName = "Bot",
            systemPrompt = "You are a serial killer!",
            maxChar = 50,
            onChatTitleChange = {},
            onCloseClick = {},
            onAcceptClick = {},
            onYourNameChange = {},
            onBotNameChange = {},
            onSystemPromptChange = {}
        )
    }
}