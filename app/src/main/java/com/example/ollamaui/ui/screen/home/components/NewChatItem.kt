package com.example.ollamaui.ui.screen.home.components

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme
import kotlin.math.roundToInt

const val ANIMATION_DURATION = 350
const val MIN_DRAG_AMOUNT = 30


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun NewChatItem(
    modifier: Modifier = Modifier,
    modelName: String,
    chatTitle: String,
    lastMessage: String,
    isNewMessageReceived: Boolean,
    newMessageStatus: Int,
    onItemClick: () -> Unit,
    onSelectedItemClick: () -> Unit,
    onItemLongPress: () -> Unit,
    isSelected: Boolean,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
) {
    val animatedColor by animateColorAsState(
        targetValue = if(isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
        label = "Animated Color",
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f, // Adds a pop effect
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Scale Animation"
    )

    val indicatorAnimation by animateColorAsState(
        targetValue = if(isNewMessageReceived) {
            if(newMessageStatus == 1) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
        } else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(delayMillis = 100),
        label = "Indicator",
    )

    /////////////////////////////////////////
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }

    val transition = rememberTransition(transitionState = transitionState, label = "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) cardOffset else 0f },
        )

    val marqueeSpacing = remember { MarqueeSpacing(10.dp) }
    /////////////////////////////////////////

    Box(
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .offset {
                IntOffset(-offsetTransition.roundToInt(), 0)
            }
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(16.dp)
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                drawRoundRect(
                    color = animatedColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    )
            }
            .pointerInput(Unit){
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount >= MIN_DRAG_AMOUNT -> onCollapse()
                        dragAmount < -MIN_DRAG_AMOUNT -> onExpand()
                    }
                }
            }
            .combinedClickable(
                onClick = {
                    onItemClick()
                },
                onLongClick = {
                    onItemLongPress()
                }
            )
    ){

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 5.dp)
        ) {

            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100))
                    .drawBehind {
                        drawRoundRect(
                            color = indicatorAnimation
                        )
                    }
                    .border(width = 5.dp, color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(100))
                    .size(75.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = modelName,
                    modifier = Modifier,
                    maxLines = 1,
                    style = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant).padding(2.dp)
                ) {
                    Text(
                        text = chatTitle,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee().background(color = MaterialTheme.colorScheme.primaryContainer)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = modelName,
                        style = MaterialTheme.typography.labelSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(spacing = marqueeSpacing)
                    )
                }
                AnimatedVisibility(visible = lastMessage != "") {
                    Text(
                        text = lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.size(45.dp)
            ) {
                AnimatedVisibility(
                    visible = isSelected,
                    modifier = Modifier.padding(end = 10.dp),
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    CustomButton(
                        description = "Clear Select",
                        onButtonClick = { onSelectedItemClick() },
                        icon = R.drawable.baseline_clear_24,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
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
            modelName = "llama3.1:1b",
            chatTitle = "Title",
            lastMessage = "Last Message will place here despite of its length! The sentences maybe become so large that it can't show completely!",
            onItemClick = {},
            onItemLongPress = {},
            onSelectedItemClick = {},
            isSelected = true,
            isNewMessageReceived = true,
            newMessageStatus = 1,
            isRevealed = false,
            cardOffset = 80f,
            onExpand = {},
            onCollapse = {},
        )
    }
}