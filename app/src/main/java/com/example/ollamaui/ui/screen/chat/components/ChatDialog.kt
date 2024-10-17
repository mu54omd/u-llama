package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.ui.common.isFromMe
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun ChatDialog(
    messageModel: MessageModel,
    userName: String,
    botName: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onSelectedItemClick: () -> Unit,
    onLongPressItem: () -> Unit,
    isSelected: Boolean,
    isVisible: Boolean,
) {
    val isFromMe = isFromMe(messageModel)
    val animatedColorMyMessage by animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
        label = "Animated Color My Message",
    )
    val animatedColorBotMessage by animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        label = "Animated Color Bot Message",
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
            Box(
                modifier = Modifier
                    .align(if (isFromMe(messageModel)) Alignment.End else Alignment.Start)
                    .clip(
                        RoundedCornerShape(
                            topStart = 48f,
                            topEnd = 48f,
                            bottomStart = if (isFromMe) 48f else 0f,
                            bottomEnd = if (isFromMe) 0f else 48f
                        )
                    )
                    .pointerInput(Unit){
                        detectTapGestures(
                            onTap = {
                                when{
                                    !isSelected -> {
                                        onItemClick()
                                    }
                                    isSelected -> {
                                        onSelectedItemClick()
                                    }
                                }
                            },
                            onLongPress = {
                                onLongPressItem()
                            }
                        )
                    }
                    .background(color = if (isFromMe) animatedColorMyMessage else animatedColorBotMessage)
                    .padding(16.dp)

            ) {
                Column(
                    horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
                ) {
                    Text(text = messageModel.content)
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        ChatDialogDetails(isVisible = isVisible, isFromMe = isFromMe, userName = userName, botName = botName)
    }

}

@Preview
@Composable
private fun ChatDialogPreview() {
    OllamaUITheme {
        Column {
            ChatDialog(
                messageModel = MessageModel(
                    content = "Hi. How are you???",
                    role = "system",
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Hello! I'm fine.",
                    role = "assistant"
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Hello! I'm fine.",
                    role = "user"
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
        }
    }
}