package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.DropDownList
import com.example.ollamaui.ui.screen.home.components.LogoTitle
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    chatTitle: String,
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(start = 5.dp, end = 5.dp),
        ) {
            CustomButton(
                description = "Back Icon",
                onButtonClick = onBackClick,
                icon = R.drawable.baseline_arrow_back_24,
                buttonSize = 50
            )
            Spacer(modifier = Modifier.width(20.dp))
            LogoTitle(
                text = chatTitle
            )
        }
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun ChatTopBarPreview() {
    OllamaUITheme {
        ChatTopBar(
            chatTitle = "Title",
            onBackClick = {}
        )
    }
}