package com.mu54omd.ullama.ui.screen.log

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mu54omd.ullama.BuildConfig
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.screen.common.CustomButton
import com.mu54omd.ullama.ui.screen.log.components.LogItem
import com.mu54omd.ullama.utils.Constants.TOP_BAR_HEIGHT
import kotlinx.coroutines.launch

@Composable
fun LogScreen(
    logViewModel: LogViewModel,
    onBackClick: () -> Unit,
) {
    val logsState = logViewModel.logs.collectAsStateWithLifecycle()
    val logLazyListState = rememberLazyListState()
    var isAutoScrollEnabled by remember { mutableStateOf(true) }
    var isProgrammaticScroll by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snapshotFlow { logLazyListState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (isScrollInProgress && !isProgrammaticScroll) {
                    isAutoScrollEnabled = false
                }
            }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp).height(TOP_BAR_HEIGHT)
        ){
            Image(
                painter = painterResource(R.drawable.ullama_icon),
                contentDescription = "App logo",
                modifier = Modifier.size(48.dp).clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.app_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Version: ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            CustomButton(
                description = "Clear Log",
                icon = R.drawable.baseline_delete_outline_24,
                onButtonClick = logViewModel::deleteLogs,
            )
        }
        HorizontalDivider()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
        ) {
            LaunchedEffect(Unit) {
                snapshotFlow { logsState.value }
                    .collect{ logs ->
                        if(isAutoScrollEnabled && logs.isNotEmpty() && !logLazyListState.isScrollInProgress){
                            isProgrammaticScroll = true
                            logLazyListState.scrollToItem(logsState.value.size - 1 )
                            isProgrammaticScroll = false
                        }
                    }
            }
            LazyColumn(
                state = logLazyListState,
            ) {
                items(
                    items = logsState.value,
                    key = { log -> log.logId }
                ) { log ->
                    LogItem(date = log.date, type = log.type, content = log.content)

                }
            }
            val showScrollButton by remember {
                derivedStateOf { isAutoScrollEnabled }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(50.dp)
                    .offset(x = (-10).dp, y = (-10).dp)
                    .align(
                        BiasAlignment(1f, 1f)
                    )
            ) {
                AnimatedVisibility(
                    visible = !showScrollButton,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    CustomButton(
                        icon = R.drawable.baseline_expand_more_24,
                        iconSize = 30,
                        buttonSize = 50,
                        description = "Tap to scroll",
                        onButtonClick = {
                            scope.launch {
                                isProgrammaticScroll = true
                                logLazyListState.scrollToItem(logsState.value.size - 1)
                                isProgrammaticScroll = false
                                isAutoScrollEnabled = true
                            }
                        },
                    )
                }
            }
        }
    }
    BackHandler {
        onBackClick()
    }
}