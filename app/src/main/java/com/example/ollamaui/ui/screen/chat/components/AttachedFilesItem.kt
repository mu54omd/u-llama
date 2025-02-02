package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.ui.common.base64ToBitmap
import com.example.ollamaui.ui.screen.common.CustomButton

@Composable
fun AttachedFilesItem(
    item: File,
    index: Int,
    onFilesClick: (File) -> Unit,
    onFilesLongPress: (File) -> Unit,
    onRemoveClick: (Int, Boolean) -> Unit,
    onSelectedItemClick: (File) -> Unit,
    isSelected: Boolean
) {
    val animateColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        label = "Animate Attached File Item"
    )
    Box(
        modifier = Modifier
            .padding(2.dp)
            .clip(shape = RoundedCornerShape(5))
            .background(animateColor)
            .pointerInput(Unit){ detectTapGestures(
                onLongPress = {
                    onFilesLongPress(item)
                },
                onTap = {
                    when{
                        isSelected -> {
                            onSelectedItemClick(item)
                        }
                        !isSelected -> {
                            onFilesClick(item)
                        }
                    }
                }
            ) },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            if(item.isImage) {
                item.attachResult.let {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(100))
                    ) {
                        Image(
                            bitmap = base64ToBitmap(it).asImageBitmap(),
                            contentDescription = "Image Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = item.fileName)
            Spacer(modifier = Modifier.width(10.dp))
            CustomButton(
                onButtonClick = { onRemoveClick(index, item.isImage) },
                icon = R.drawable.baseline_clear_24,
                description = "Remove Attached File",
                containerColor = animateColor,
                iconSize = 20
            )
        }
    }
}