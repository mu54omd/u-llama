package com.example.ollamaui.ui.screen.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.objectbox.StableFile
import com.example.ollamaui.ui.common.base64ToBitmap

@Composable
fun FilePreviewScreen(
    fileContent: State<List<String>>,
    file: State<StableFile>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
    )
    {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10))
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (file.value.isImage) {
                Image(
                    bitmap = base64ToBitmap(file.value.attachResult).asImageBitmap(),
                    contentDescription = "Image Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.Center)
                ) {
                    items(
                        items = fileContent.value
                    ) {
                        Text(text = it)
                    }
                }
            }
        }
        Card(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = file.value.fileName, textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp))
        }
    }
}