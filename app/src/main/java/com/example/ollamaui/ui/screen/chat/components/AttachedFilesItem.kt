package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.objectbox.StableFile
import com.example.ollamaui.ui.common.base64ToBitmap

@Composable
fun AttachedFilesItem(
    item: StableFile,
    onFilesClick: (StableFile) -> Unit,
    onFilesLongPress: (StableFile) -> Unit,
    onSelectedItemClick: (StableFile) -> Unit,
    isSelected: Boolean,
    isFileReady: Boolean
) {
    val animateColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
        label = "Animate Attached File Item"
    )
    Box(
        modifier = Modifier
            .requiredHeight(32.dp)
            .padding(2.dp)
            .clip(shape = RoundedCornerShape(100))
            .drawBehind{
                drawRoundRect(
                    color = animateColor,
                )
            }
            .pointerInput(Unit){ detectTapGestures(
                onLongPress = {
                    if(isFileReady) {
                        onFilesLongPress(item)
                    }
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
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
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
            Text(
                text = item.fileName,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.widthIn(max = 150.dp).basicMarquee(),
                maxLines = 1
            )
        }
    }
}