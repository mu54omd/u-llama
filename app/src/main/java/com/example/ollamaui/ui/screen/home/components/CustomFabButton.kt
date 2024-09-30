package com.example.ollamaui.ui.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.CustomDropUpMenu

@Composable
fun CustomFabButton(
    modifier: Modifier = Modifier,
    isModelListLoaded: Boolean = false,
    modelList: List<String> = emptyList(),
    fabListVisible: Boolean,
    onItemClick: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        AnimatedVisibility(fabListVisible) {
            CustomDropUpMenu(
                listItems = modelList,
                onItemClick = onItemClick
            )
        }
        Spacer(Modifier.height(5.dp))
        CustomButton(
            description = "New Chat",
            onButtonClick = onButtonClick,
            icon = if(isModelListLoaded) { if(fabListVisible) R.drawable.baseline_expand_more_24 else R.drawable.baseline_add_24} else R.drawable.baseline_refresh_24,
            iconSize = 40,
            buttonSize = 60,
        )


    }
}