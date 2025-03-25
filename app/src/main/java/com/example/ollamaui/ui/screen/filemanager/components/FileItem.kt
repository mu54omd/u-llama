package com.example.ollamaui.ui.screen.filemanager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.common.epochToDate
import com.example.ollamaui.ui.screen.common.CustomButton

@Composable
fun FileItem(
    modifier: Modifier = Modifier,
    fileName: String,
    hash: String,
    fileAddedTime: Long,
    onFileClick: () -> Unit,
    onDeleteFileClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Card(
            onClick = onFileClick,
            modifier = Modifier.weight(0.8f).height(64.dp).padding(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = fileName.substringBeforeLast("."),
                    maxLines = 1,
                    modifier = Modifier.weight(0.1f),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = fileName.substringAfterLast("."),
                    maxLines = 1,
                    modifier = Modifier.weight(0.1f),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = epochToDate(fileAddedTime),
                    maxLines = 1,
                    modifier = Modifier.weight(0.1f),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = hash,
                    maxLines = 1,
                    modifier = Modifier.weight(0.3f),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        CustomButton(
            onButtonClick = onDeleteFileClick,
            description = "Delete File",
            icon = R.drawable.baseline_delete_outline_24
        )
    }
}