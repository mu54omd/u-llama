package com.mu54omd.ullama.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import com.mu54omd.ullama.ui.theme.ULlamaTheme

@Composable
fun ChatTitle(
    modifier: Modifier = Modifier,
    title: String,
    modelName: String,
    isResponding: Boolean
    ) {
    var isEnable1 by remember { mutableStateOf(false)}
    var isEnable2 by remember { mutableStateOf(false)}
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = modelName,
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
        AnimatedVisibility(
            visible = isResponding,
            enter = slideInVertically(),
            exit = slideOutVertically(),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Typing", style = MaterialTheme.typography.bodySmall)
                PulsingDots()
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
private fun ChatTitlePreview() {
    ULlamaTheme {
        ChatTitle(
            modelName = "Boooooooooooooooooooooooooooooooooot",
            title = "Chaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat",
            isResponding = true
        )
    }
}