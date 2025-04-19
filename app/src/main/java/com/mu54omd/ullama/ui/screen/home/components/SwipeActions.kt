package com.mu54omd.ullama.ui.screen.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.screen.common.CustomButton
import com.mu54omd.ullama.ui.theme.ULlamaTheme

@Composable
fun SwipeActions(
    onDeleteClick: () -> Unit,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.96f else 0.97f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Scale Animation"
    )
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(60.dp)
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(16.dp)
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFFFF3259)
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        CustomButton(
            icon = R.drawable.baseline_delete_outline_24,
            description = "Delete chat",
            iconSize = 30,
            onButtonClick = onDeleteClick,
            modifier = Modifier.padding(10.dp),
            containerColor = Color.Transparent
        )
    }
}

@Preview
@Composable
private fun SwipeActionsPreview() {
    ULlamaTheme {
        Surface {
            SwipeActions(
                onDeleteClick = {},
                isSelected = false
            )
        }
    }
}