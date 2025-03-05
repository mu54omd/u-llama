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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.BuildConfig
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.DumbLogModel
import com.example.ollamaui.domain.model.LogModel
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun LogScreen(
    logsState: State<List<LogModel>>,
    onClearLogClick: () -> Unit,
    onBackClick: () -> Unit,
) {

    val textValueLogs = logsState.value.joinToString("\n") { ">>" + it.date + " " + it.type + " " + it.content }
    val logLazyListState = rememberLazyListState(initialFirstVisibleItemIndex = textValueLogs.lastIndex)
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
                onButtonClick = {
                    onClearLogClick()
                }
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
                key = { it.logId }
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

//@Preview(showBackground = true)
//@Composable
//private fun LogScreenPreview() {
//    OllamaUITheme {
//        val logs = remember { mutableStateListOf(DumbLogModel.dumb, DumbLogModel.dumb, DumbLogModel.dumb) }
//        LogScreen(
//            logs = logs,
//            onClearLogClick = {},
//            onBackClick = {}
//        )
//    }
//}