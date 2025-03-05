package com.example.ollamaui.ui.screen.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ollamaui.BuildConfig
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.log.LogViewModel
import com.example.ollamaui.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun LogScreen(
    logViewModel: LogViewModel,
    onBackClick: () -> Unit,
) {
    val logsState = logViewModel.logs.collectAsStateWithLifecycle()
    val logLazyListState = rememberLazyListState()
    LaunchedEffect(logsState) {
        snapshotFlow { logsState.value }
            .collect{ logs ->
                if(logs.isNotEmpty()){
                    logLazyListState.animateScrollToItem(logsState.value.size-1)

                }
            }
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp).height(TOP_BAR_HEIGHT)
        ){
            Image(
                painter = painterResource(if(isSystemInDarkTheme()) R.drawable.icon_dark else R.drawable.icon_light),
                contentDescription = "App logo",
                modifier = Modifier.size(width = 96.dp, height = 64.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Ollama UI",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "A simple interface for Ollama",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Version: ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)
        ) {
            CustomButton(
                description = "Clear Log",
                iconSize = 15,
                icon = R.drawable.baseline_delete_outline_24,
                onButtonClick = logViewModel::deleteLogs
            )
        }
        LazyColumn(
            state = logLazyListState,
            modifier = Modifier
                .padding(10.dp)
                .background(
                    color = if(isSystemInDarkTheme()) Color.DarkGray else Color.White,
                    shape = MaterialTheme.shapes.small
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(5.dp)
        ) {
            items(
                items = logsState.value,
                key = { log -> log.logId }
            ) { log ->
                BasicTextField(
                    value = ">>" + log.date + " " + log.type + " " + log.content,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }
    }
    BackHandler {
        onBackClick()
    }
}