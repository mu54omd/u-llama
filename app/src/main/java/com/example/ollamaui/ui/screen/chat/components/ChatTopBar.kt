package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.screen.common.DropDownList

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    tagError: Int?,
    modelList: List<String>,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(top = 20.dp)
    ) {
        if (tagError == null) {
            DropDownList(
                listItems = modelList,
                onItemClick = onItemClick
            )
        }
    }
}