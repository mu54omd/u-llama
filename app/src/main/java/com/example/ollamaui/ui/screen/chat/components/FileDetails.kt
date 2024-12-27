package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.ui.common.base64ToBitmap

@Composable
fun FileDetails(
    onDismissRequest: () -> Unit,
    file: File
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(shape = RoundedCornerShape(5))
                .background(color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(10.dp).fillMaxWidth()
            ) {
                file.attachResult.let {
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(10))
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (file.isImage) {
                            Image(
                                bitmap = base64ToBitmap(it).asImageBitmap(),
                                contentDescription = "Image Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = it,
                                modifier = Modifier.padding(5.dp).verticalScroll(rememberScrollState()),
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
                Text(text = file.fileName)
            }
        }
    }
}