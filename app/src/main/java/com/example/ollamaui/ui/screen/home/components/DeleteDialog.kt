package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    chatTitle: String,
    userName: String,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { onCloseClick() }
    ) {
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .size(200.dp, 100.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(150.dp).align(Alignment.TopCenter).padding(top = 20.dp)
            ) {
                Text(text = "Are you sure you want to delete this chat?" ,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            ) {
                CustomButton(
                    description = "Accept",
                    onButtonClick = onAcceptClick,
                    icon = R.drawable.baseline_check_24,
                    buttonSize = 25,
                    iconSize = 20,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
                Spacer(modifier = Modifier.width(30.dp))
                CustomButton(
                    description = "Close",
                    onButtonClick = onCloseClick,
                    icon = R.drawable.baseline_clear_24,
                    buttonSize = 25,
                    iconSize = 20
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogPreview() {
    OllamaUITheme {
        DeleteDialog(
            chatTitle = "newChat",
            userName = "author",
            onCloseClick = {},
            onAcceptClick = {},
        )
    }
}