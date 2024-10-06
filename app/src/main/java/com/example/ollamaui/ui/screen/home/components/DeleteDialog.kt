package com.example.ollamaui.ui.screen.home.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
    yourName: String,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { onCloseClick() }
    ) {
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.large)
                .size(200.dp, 175.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Text(text = "Delete confirmation", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Chat title: $chatTitle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Author: $yourName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomButton(
                        description = "Accept",
                        onButtonClick = onAcceptClick,
                        icon = R.drawable.baseline_check_24,
                        buttonSize = 50,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    CustomButton(
                        description = "Close",
                        onButtonClick = onCloseClick,
                        icon = R.drawable.baseline_clear_24,
                        buttonSize = 50,
                    )
                }
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
            yourName = "author",
            onCloseClick = {},
            onAcceptClick = {},
        )
    }
}