package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.AttachedFileModel
import com.example.ollamaui.ui.common.base64ToBitmap
import com.example.ollamaui.ui.screen.common.CustomButton

@Composable
fun AttachedFiles(
    isVisible: Boolean,
    items: List<AttachedFileModel>,
    onRemoveClick: (Int, Boolean) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible
    ) {
        LazyRow {
            itemsIndexed(items){ index, item ->
                Card(
                    onClick = {},
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))
                        if(item.isImage) {
                            item.attachResult?.let {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(30))
                                ) {
                                    Image(
                                        bitmap = base64ToBitmap(it).asImageBitmap(),
                                        contentDescription = "",
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
                            buttonSize = 35,
                            description = "Remove Attached File"
                        )
                    }
                }
            }
        }
    }

}