package com.example.ollamaui.ui.screen.home.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    onSelectedItemClick: () -> Unit,
    onItemLongPress: () -> Unit,
    isSelected: Boolean
) {

    val animatedColor by animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        label = "Animated Color",
    )
    val animatedSize by animateIntAsState(
        if(isSelected) 5 else 10,
        label = "Animated Size"
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
                        when{
                            isSelected -> {
                                onSelectedItemClick()
                            }
                            !isSelected -> {
                                onItemClick()
                            }
                        }
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
                    description = if(isSelected) "Clear Select" else "Delete Chat",
                    onButtonClick = {
                        if(isSelected) {
                            onSelectedItemClick()
                        } else {
                            onDeleteClick()
                        }
                                    },
                    icon = if(isSelected) R.drawable.baseline_clear_24 else R.drawable.baseline_delete_outline_24,
                    containerColor = Color.Transparent,
                    buttonSize = 30
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
            onItemClick = {},
            onItemLongPress = {},
            onSelectedItemClick = {},
            isSelected = true
        )
    }
}