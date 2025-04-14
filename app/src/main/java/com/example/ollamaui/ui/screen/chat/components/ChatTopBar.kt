package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    modelName: String,
    chatTitle: String,
    onBackClick: () -> Unit,
    onCopyClick: () -> Unit,
    onDeselectClick: () -> Unit,
    onShowAttachedFileClick: () -> Unit,
    onHideAttachedFileClick: () -> Unit,
    isCopyButtonEnabled: Boolean,
    isResponding: Boolean,
    isAnyFileAttached: Boolean
) {
    var isAttachedFilesListEnabled by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .background(color = Color.Transparent)
            .padding(start = 5.dp, end = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10),
                )
                .height(TOP_BAR_HEIGHT)
                .padding(start = 5.dp, end = 5.dp),
        ) {
            CustomButton(
                description = "Back Icon",
                onButtonClick = onBackClick,
                icon = R.drawable.baseline_arrow_back_24,
                buttonSize = 40,
                containerColor = Color.Transparent
            )
            Spacer(modifier = Modifier.width(10.dp))
            ChatTitle(
                title = chatTitle,
                modelName = modelName,
                isResponding = isResponding,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.requiredSize(width = 120.dp, height = 40.dp)
            ) {
                AnimatedVisibility(isAnyFileAttached) {
                    Crossfade(
                        targetState = isAttachedFilesListEnabled,
                        label = "Show/Hide imported file"
                    ) { isEnabled ->
                        when (isEnabled) {
                            false -> {
                                CustomButton(
                                    description = "show attached files Icon",
                                    onButtonClick = {
                                        isAttachedFilesListEnabled = true
                                        onShowAttachedFileClick()
                                    },
                                    icon = R.drawable.baseline_source_24,
                                    buttonSize = 40,
                                    containerColor = Color.Transparent,
                                    isButtonEnabled = true
                                )
                            }

                            true -> {
                                CustomButton(
                                    description = "hide attached files Icon",
                                    onButtonClick = {
                                        isAttachedFilesListEnabled = false
                                        onHideAttachedFileClick()
                                    },
                                    icon = R.drawable.baseline_hide_source_24,
                                    buttonSize = 40,
                                    containerColor = Color.Transparent,
                                    isButtonEnabled = true
                                )
                            }
                        }
                    }
                }
                CustomButton(
                    description = "Deselect Icon",
                    onButtonClick = onDeselectClick,
                    icon = R.drawable.baseline_deselect_24,
                    buttonSize = 40,
                    containerColor = Color.Transparent,
                    isButtonEnabled = isCopyButtonEnabled
                )
                CustomButton(
                    description = "Copy Icon",
                    onButtonClick = onCopyClick,
                    icon = R.drawable.baseline_content_copy_24,
                    buttonSize = 40,
                    containerColor = Color.Transparent,
                    isButtonEnabled = isCopyButtonEnabled
                )
            }

        }
        HorizontalDivider(thickness = 2.dp)
    }
}


@Preview()
@Composable
private fun ChatTopBarPreview() {
    OllamaUITheme {
        Surface {
            ChatTopBar(
                modelName = "Very very very very very long name",
                chatTitle = "Very very very very very long title",
                onBackClick = {},
                onCopyClick = {},
                onDeselectClick = {},
                onShowAttachedFileClick = {},
                onHideAttachedFileClick = {},
                isCopyButtonEnabled = false,
                isResponding = true,
                isAnyFileAttached = true
            )
        }
    }
}