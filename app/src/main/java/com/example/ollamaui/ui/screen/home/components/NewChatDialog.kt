package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun NewChatDialog(
    modifier: Modifier = Modifier,
    userName: String,
    onUserNameChange: (String) -> Unit,
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
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                val focusManager = LocalFocusManager.current
                CustomTextField(
                    value = chatTitle,
                    onValueChange = onChatTitleChange,
                    label = "Chat Title",
                    maxChar = maxChar,
                    onDone = { focusManager.moveFocus(FocusDirection.Next) },)
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = "Your Name",
                    maxChar = maxChar,
                    onDone = { focusManager.moveFocus(FocusDirection.Next) })
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = botName,
                    onValueChange = onBotNameChange,
                    label = "Bot Name",
                    maxChar = maxChar,
                    onDone = { focusManager.moveFocus(FocusDirection.Next) })
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = systemPrompt,
                    onValueChange = onSystemPromptChange,
                    label = "Describe the bot",
                    maxChar = maxChar * 6,
                    onDone = { if (userName != "" && chatTitle != "") onAcceptClick() },
                    maxLines = 5,
                    minLines = 5,
                    roundCornerPercent = 10
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp)
            ) {
                CustomButton(
                    description = "Accept",
                    onButtonClick = onAcceptClick,
                    icon = R.drawable.baseline_check_24,
                    buttonSize = 50,
                    isButtonEnabled = chatTitle.isNotEmpty() && userName.isNotEmpty(),
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

@Preview(showBackground = true)
@Composable
private fun NewChatDialogPreview() {
    OllamaUITheme {
        NewChatDialog(
            userName = "Me",
            chatTitle = "Title",
            botName = "Bot",
            systemPrompt = "You are a serial killer!",
            maxChar = 50,
            onChatTitleChange = {},
            onCloseClick = {},
            onAcceptClick = {},
            onUserNameChange = {},
            onBotNameChange = {},
            onSystemPromptChange = {},
        )
    }
}