package com.example.ollamaui.ui.screen.home.components

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.shimmerEffect
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun NetworkErrorDialog(
    modifier: Modifier = Modifier,
    statusError: Int?,
    statusThrowable: String?,
    onSettingClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {}
    ) {
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.large)
                .size(300.dp, 200.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Something happened!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(2.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(2.dp)
                ) {
                    Text("Type", fontWeight = FontWeight.Bold)
                    if(statusError == null){
                        Box(
                            modifier = Modifier
                                .clip(shape = MaterialTheme.shapes.large)
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                .size(width = 150.dp, height = 20.dp)
                                .shimmerEffect()
                        )
                    }else {
                            Text(
                                text = stringResource(statusError),
                                textAlign = TextAlign.Center
                            )
                    }
                    Text("Details", fontWeight = FontWeight.Bold)
                    if(statusThrowable == null){
                        Box(
                            modifier = Modifier
                                .clip(shape = MaterialTheme.shapes.large)
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                .size(width = 150.dp, height = 20.dp)
                                .shimmerEffect()
                        )
                    }else {
                        Text(
                            text = statusThrowable,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomButton(
                        description = "Open Settings",
                        onButtonClick = onSettingClick,
                        icon = R.drawable.baseline_settings_24,
                        buttonSize = 40,
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    CustomButton(
                        description = "Retry",
                        onButtonClick = onRetryClick,
                        icon = R.drawable.baseline_refresh_24,
                        buttonSize = 40,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    CustomButton(
                        description = "Close",
                        onButtonClick = onCloseClick,
                        icon = R.drawable.baseline_clear_24,
                        buttonSize = 40,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkErrorDialogPreview() {
    OllamaUITheme {
        NetworkErrorDialog(
            statusError = null,
            statusThrowable = null,
            onSettingClick = {},
            onRetryClick = {},
            onCloseClick = {}
        )
    }
}