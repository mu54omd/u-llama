package com.mu54omd.ullama.ui.screen.chat.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mu54omd.ullama.domain.model.MessageModel
import com.mu54omd.ullama.ui.common.filterAssistantMessage
import com.mu54omd.ullama.ui.common.filterUserMessage
import com.mu54omd.ullama.ui.common.isFromMe
import com.mu54omd.ullama.ui.theme.ULlamaTheme
import com.mu54omd.ullama.utils.Constants.USER_ROLE
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle

@Composable
fun ChatDialog(
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onSelectedItemClick: () -> Unit,
    onLongPressItem: () -> Unit,
    isSelected: Boolean,
    isDetailsVisible: Boolean,
    isSendingFailed: Boolean,
    isLastMinusOneMessage: Boolean,
    isLastMessage: Boolean,
    onReproduceResponse: () -> Unit,
    onDeleteLastMessage: () -> Unit,
    onEditLastMessage: () -> Unit,
    isResponding: Boolean,
    onReadClick: (String) -> Unit,
    onStopClick: () -> Unit,
    isReading: Boolean
) {
    val isFromMe by remember { derivedStateOf { isFromMe(messageModel) } }
    val animatedColorMessage by animateColorAsState(
        when(isFromMe){
            true -> {
                if(isSelected) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                else if(isSendingFailed && isLastMessage) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.surfaceVariant
            }
            false -> {
                if(isSelected) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.background
            }
        },
        label = "Animated Color Message",
    )
    val animatedOffset by animateIntAsState(
        targetValue = if(isSelected) {if(isFromMe) -30 else 30 } else 0,
        label = "Animated Offset chat dialog"
    )
    val textBgColor = if(!isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    val richTextStyle = RichTextStyle(
        codeBlockStyle = CodeBlockStyle(
            textStyle = MaterialTheme.typography.bodySmall,
            padding = 6.sp,
            modifier = Modifier.background(
                color = textBgColor)
        ),
        stringStyle = RichTextStringStyle(
            codeStyle = SpanStyle(
                background = textBgColor,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp
            )
        )
    )
    var botMessage = ""
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 10.dp)
    ) {
            Box(
                modifier = Modifier
                    .align(if (isFromMe) Alignment.End else Alignment.Start)
                    .offset { IntOffset(animatedOffset, 0) }
                    .widthIn(min = 120.dp)
                    .border(
                        width = (1.5).dp,
                        color = when{
                            isFromMe -> MaterialTheme.colorScheme.surface
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        shape =
                            RoundedCornerShape(
                                topStart = if (isFromMe) 50f else 0f,
                                topEnd = if (isFromMe) 0f else 50f,
                                bottomStart = 50f,
                                bottomEnd = 50f
                            )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isFromMe) 50f else 0f,
                            topEnd = if (isFromMe) 0f else 50f,
                            bottomStart = 50f,
                            bottomEnd = 50f
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                when {
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
                    .drawBehind {
                        drawRoundRect(
                            color = animatedColorMessage,
                        )
                    }
                    .padding(16.dp)

            ) {
                Column(
                    horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
                ) {
                    if(messageModel.role == USER_ROLE) {
                        Text(text = filterUserMessage(messageModel.content)?:messageModel.content )
                    }else{
                        val thinkingText = filterAssistantMessage(assistantMessage = messageModel.content)
                        if(thinkingText.first != null){
                            botMessage = messageModel.content
                            RichText(
                                style = richTextStyle
                            ) {
                                val parser = remember { CommonmarkAstNodeParser() }
                                val astNode = remember(parser) {
                                    parser.parse(messageModel.content)
                                }
                                BasicMarkdown(astNode)
                            }
                        }else{
                            botMessage = thinkingText.third!!
                            if(thinkingText.second?.isNotEmpty() == true) {
                                Text(
                                    text = thinkingText.second!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .drawBehind {
                                            val strokeWidth = 1.dp.toPx()
                                            drawLine(
                                                color = Color.LightGray,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                        .padding(top = 5.dp, bottom = 5.dp)
                                )
                            }
                            RichText(
                                style = richTextStyle
                            ) {
                                val parser = remember { CommonmarkAstNodeParser() }
                                val astNode = remember(parser) {
                                    parser.parse(thinkingText.third!!)
                                }
                                BasicMarkdown(astNode)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        ChatDialogDetails(
            isVisible = isDetailsVisible,
            isFromMe = isFromMe,
            date = messageModel.date,
            time = messageModel.time,
            isLastBotMessage = isLastMessage && !isFromMe,
            isLastUserMessage = (isLastMessage || isLastMinusOneMessage ) && isFromMe,
            onReproduceResponse = onReproduceResponse,
            onEditLastMessage = onEditLastMessage,
            onDeleteLastMessage = onDeleteLastMessage,
            onReadClick = { onReadClick(botMessage) },
            onStopClick = { onStopClick() },
            isResponding = isResponding,
            isReading = isReading
        )
    }

}
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ChatDialogPreview() {
    ULlamaTheme {
        Column {
            ChatDialog(
                messageModel = MessageModel(
                    content = "<think> User ask some question and we should answer that</think> Hi. How are you???",
                    role = "system",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMessage = true,
                isLastMinusOneMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false,
                onStopClick = {},
                onReadClick = {},
                isReading = true
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "<think>\n\n</think> Hi. How are you???",
                    role = "assistant",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMessage = true,
                isLastMinusOneMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false,
                onStopClick = {},
                onReadClick = {},
                isReading = true
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Let's start with the basic syntax of Kotlin:\n\n**Variables**\n\nIn Kotlin, variables are declared using the `var` keyword. We can declare a variable and assign it a value in one line like this:\n```\nval name = \"Musa\"\nprintln(name)\n```\nHere, `name` is a local variable (declared inside the function), which is stored on the stack.\n\n",
                    role = "assistant",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMinusOneMessage = true,
                isLastMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false,
                onStopClick = {},
                onReadClick = {},
                isReading = true
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Using this data: {/n/n/n/some data}. Respond to this prompt: {some prompt}.",
                    role = "user",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMinusOneMessage = true,
                isLastMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false,
                onStopClick = {},
                onReadClick = {},
                isReading = true
            )
        }
    }
}