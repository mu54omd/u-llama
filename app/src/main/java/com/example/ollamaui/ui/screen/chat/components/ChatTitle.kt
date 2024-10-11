package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun ChatTitle(
    modifier: Modifier = Modifier,
    title: String,
    botName: String,
    ) {
    var isEnable1 by remember { mutableStateOf(false)}
    var isEnable2 by remember { mutableStateOf(false)}
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = botName,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee(iterations = if(isEnable1) Int.MAX_VALUE else 0, spacing = MarqueeSpacing(10.dp))
                .pointerInput(Unit){detectTapGestures(onTap = {isEnable1 = !isEnable1})}
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee(iterations = if(isEnable2) Int.MAX_VALUE else 0, spacing = MarqueeSpacing(10.dp))
                .pointerInput(Unit){detectTapGestures(onTap = {isEnable2 = !isEnable2})}

        )
    }

}

@Preview(showSystemUi = true)
@Composable
private fun ChatTitlePreview() {
    OllamaUITheme {
        ChatTitle(
            botName = "Boooooooooooooooooooooooooooooooooot",
            title = "Chaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat"
        )
    }
}