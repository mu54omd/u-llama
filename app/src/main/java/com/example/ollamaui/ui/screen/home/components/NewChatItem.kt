package com.example.ollamaui.ui.screen.home.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun NewChatItem(
    modifier: Modifier = Modifier,
    modelName: String,
    botName: String,
    userName: String,
    chatTitle: String,
    isNewMessageReceived: Boolean,
    newMessageStatus: Int,
    @DrawableRes chatImage: Int,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit,
    onSelectedItemClick: () -> Unit,
    onItemLongPress: () -> Unit,
    isSelected: Boolean
) {

    val animatedColor by animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "Animated Color",
    )
    val animatedSize by animateIntAsState(
        if(isSelected) 5 else 10,
        label = "Animated Size"
    )
    val indicatorAnimation by animateColorAsState(
        if(isNewMessageReceived) {
            if(newMessageStatus == 1) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.error
        } else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "Indicator",
    )

    Box(
        modifier = modifier
            .padding(start = animatedSize.dp, end = animatedSize.dp, top = animatedSize.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.large)
            .background(color = animatedColor)
            .pointerInput(Unit){
                detectTapGestures(
                    onLongPress = {
                        onItemLongPress()
                    } ,
                    onTap = {
                        onItemClick()
                    }
                )
            }
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100))
                    .background(color = indicatorAnimation)
                    .size(75.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(chatImage),
                    contentDescription = "Chat Image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(color = MaterialTheme.colorScheme.outline)
                        .size(65.dp),
                )

            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$botName: ($modelName)",
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(spacing = MarqueeSpacing(10.dp))
                    )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = chatTitle,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                if (isSelected) {
                    CustomButton(
                        description = "Clear Select",
                        onButtonClick = { onSelectedItemClick() },
                        icon = R.drawable.baseline_clear_24,
                        containerColor = Color.Transparent,
                        buttonSize = 30
                    )
                }
            }

        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun NewChatItemPreview() {
    OllamaUITheme {
        NewChatItem(
            onDeleteClick = {},
            onItemClick = {},
            onItemLongPress = {},
            onSelectedItemClick = {},
            isSelected = false,
            userName = "User",
            botName = "Bot",
            modelName = "llama3.1",
            chatTitle = "Title",
            chatImage = R.drawable.avatar_man_01,
            isNewMessageReceived = false,
            newMessageStatus = 2
        )
    }
}