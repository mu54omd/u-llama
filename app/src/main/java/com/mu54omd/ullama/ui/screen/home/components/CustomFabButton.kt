package com.mu54omd.ullama.ui.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.screen.common.CustomButton

@Composable
fun CustomFabButton(
    modifier: Modifier = Modifier,
    isModelListLoaded: Boolean = false,
    isFabVisible: Boolean,
    onButtonClick: () -> Unit
) {
    // Independent rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isModelListLoaded) 360f else 0f, // Rotates when toggling
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "Rotation Animation"
    )

    // Independent scaling animation
    val scale by animateFloatAsState(
        targetValue = if (isModelListLoaded) 1f else 0.8f, // Adds a pop effect
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Scale Animation"
    )
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = isFabVisible,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            CustomButton(
                onButtonClick = onButtonClick,
                description = "FAB",
                icon = if(isModelListLoaded) R.drawable.baseline_add_24 else R.drawable.baseline_refresh_24,
                iconSize = 40,
                buttonSize = 60,
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                modifierIcon = Modifier.size(40.dp).graphicsLayer {
                    rotationZ = rotation
                    scaleX = scale
                    scaleY = scale
                }
            )
        }
    }
}