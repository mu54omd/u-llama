package com.mu54omd.ullama.ui.screen.chat.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.domain.model.objectbox.StableFile
import com.mu54omd.ullama.ui.common.base64ToBitmap

@Composable
fun AttachedFilesItem(
    item: StableFile,
    onFilesClick: (StableFile) -> Unit,
    onFilesLongPress: (StableFile) -> Unit,
    onSelectedItemClick: (StableFile) -> Unit,
    isSelected: Boolean,
    isFileReady: Boolean
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline,
        animationSpec = tween(),
        label = "Animate border color of Attached File Item"
    )
    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 2.dp,
        animationSpec = tween(),
        label = "Animate border width of Attached File Item"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(),
        label = "Animate scale of Attached File Item"
    )
    Box(
        modifier = Modifier
            .requiredHeight(36.dp)
            .padding(4.dp)
            .graphicsLayer{
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clip(shape = RoundedCornerShape(100))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .drawBehind {
                drawRoundRect(
                    color = animatedColor,
                    style = Stroke(width = animatedWidth.toPx()),
                    cornerRadius = CornerRadius(x = 50f ,y = 50f)
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (isFileReady) {
                            onFilesLongPress(item)
                        }
                    },
                    onTap = {
                        when {
                            isSelected -> {
                                onSelectedItemClick(item)
                            }

                            !isSelected -> {
                                onFilesClick(item)
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            if(item.isImage) {
                Image(
                    bitmap = base64ToBitmap(item.attachResult).asImageBitmap(),
                    contentDescription = "Image Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .size(24.dp)
                )
            }
            Text(
                text = item.fileName,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .widthIn(max = 150.dp)
                    .basicMarquee(),
                maxLines = 1,
            )
        }
    }
}