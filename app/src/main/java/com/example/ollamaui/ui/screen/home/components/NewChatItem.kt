package com.example.ollamaui.ui.screen.home.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun NewChatItem(
    modifier: Modifier = Modifier,
    modelName: String = "Bot",
    chatTitle: String = "New Chat",
    @DrawableRes chatImage: Int = R.drawable.ic_launcher_foreground,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.large)
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
            .clickable { onItemClick() }

    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100))
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .size(75.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(chatImage),
                    contentDescription = chatTitle,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = modelName, style = MaterialTheme.typography.labelMedium)
                Text(text = chatTitle, style = MaterialTheme.typography.headlineMedium)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                CustomButton(
                    description = "Delete Chat",
                    onButtonClick = onDeleteClick,
                    icon = R.drawable.baseline_delete_outline_24,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            }

        }
    }
}

@Preview
@Composable
private fun NewChatItemPreview() {
    OllamaUITheme {
        NewChatItem(
            onDeleteClick = {},
            onItemClick = {}
        )
    }
}