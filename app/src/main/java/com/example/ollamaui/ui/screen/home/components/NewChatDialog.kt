package com.example.ollamaui.ui.screen.home.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.CustomDropDownList
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
    modelList: List<String>,
    onModelClick: (String) -> Unit,
    avatarList: List<Int> = emptyList(),
    onAvatarClick: (Int) -> Unit,
    @DrawableRes selectedAvatar: Int
    ) {
    var isAvatarSelectionDialogVisible by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { onCloseClick() },
    ) {
        var isModelSelected by remember { mutableStateOf(false) }
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.large)
                .size(300.dp, 500.dp)
                .padding(top = 2.dp, bottom = 2.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            CustomDropDownList(
                listItems = modelList,
                onItemClick = {
                    onModelClick(it)
                    isModelSelected = true
                              },
                modifier = Modifier.align(alignment = Alignment.TopCenter)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(top = 20.dp)
            ) {
                val focusManager = LocalFocusManager.current
                CustomTextField(
                    value = chatTitle,
                    onValueChange = onChatTitleChange,
                    label = "Chat Title",
                    maxChar = maxChar,
                    onDone = { focusManager.moveFocus(FocusDirection.Down) },)
                Spacer(modifier = Modifier.height(2.dp))
                CustomTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = "Your Name",
                    maxChar = maxChar,
                    onDone = { focusManager.moveFocus(FocusDirection.Down) })
                Spacer(modifier = Modifier.height(2.dp))
                CustomTextField(
                    value = botName,
                    onValueChange = onBotNameChange,
                    label = "Bot Name",
                    maxChar = maxChar,
                    onDone = { focusManager.moveFocus(FocusDirection.Down) },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(100))
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    isAvatarSelectionDialogVisible = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(selectedAvatar),
                                contentDescription = "Avatar",
                            )
                        }
                    }
                    )
                Spacer(modifier = Modifier.height(2.dp))
                CustomTextField(
                    value = systemPrompt,
                    onValueChange = onSystemPromptChange,
                    label = "Describe the bot",
                    maxChar = maxChar * 6,
                    onDone = { if (userName.isNotEmpty() && chatTitle.isNotEmpty() && isModelSelected) onAcceptClick() },
                    maxLines = 5,
                    minLines = 5,
                    roundCornerPercent = 10
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            ) {
                CustomButton(
                    description = "Accept",
                    onButtonClick = onAcceptClick,
                    icon = R.drawable.baseline_check_24,
                    buttonSize = 50,
                    isButtonEnabled = chatTitle.isNotEmpty() && userName.isNotEmpty() && isModelSelected,
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
    AvatarSelection(
        isAvatarSelectionDialogVisible = isAvatarSelectionDialogVisible,
        avatarList = avatarList,
        onDismiss = {isAvatarSelectionDialogVisible = false},
        onAvatarClick = {
            onAvatarClick(it)
            isAvatarSelectionDialogVisible = false
        }
    )
}

@Composable
private fun AvatarSelection(
    avatarList: List<Int>,
    isAvatarSelectionDialogVisible: Boolean,
    onDismiss: () -> Unit,
    onAvatarClick: (Int) -> Unit
){
    AnimatedVisibility(visible = isAvatarSelectionDialogVisible) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(5))
                    .size(width = 200.dp, height = 200.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(avatarList) { avatar ->
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(100))
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                                .clickable { onAvatarClick(avatar) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(avatar),
                                contentDescription = "Avatar",
                                modifier = Modifier.size(60.dp)
                                ,
                            )
                        }
                    }
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
            modelList = listOf("model 1", "model 2"),
            onModelClick = {},
            avatarList = listOf(R.drawable.avatar_man_01),
            onAvatarClick = {},
            selectedAvatar = R.drawable.avatar_woman_04
        )
    }
}

@Preview
@Composable
private fun AvatarSelectionPreview() {
    OllamaUITheme {
        AvatarSelection(
            isAvatarSelectionDialogVisible = true,
            onDismiss = {},
            avatarList = listOf(
                R.drawable.avatar_man_01, R.drawable.avatar_man_02, R.drawable.avatar_man_03, R.drawable.avatar_man_04,
                R.drawable.avatar_woman_01, R.drawable.avatar_woman_02, R.drawable.avatar_woman_03, R.drawable.avatar_woman_04
        ),
            onAvatarClick = {}
        )
    }
    
}