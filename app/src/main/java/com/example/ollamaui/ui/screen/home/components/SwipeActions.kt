package com.example.ollamaui.ui.screen.home.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun SwipeActions(
    onDeleteClick: () -> Unit,
    isSelected: Boolean
) {
    val animatedSize by animateIntAsState(
        if(isSelected) 3 else 5,
        label = "Animated Size"
    )
    Row(
        modifier = Modifier
            .padding(animatedSize.dp)
            .fillMaxWidth()
            .height(85.dp)
            .background(Color(0xFFFF3259), shape = MaterialTheme.shapes.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
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
    OllamaUITheme {
        SwipeActions(
            onDeleteClick = {},
            isSelected = true
        )
    }
}