package com.example.ollamaui.ui.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme
import kotlinx.coroutines.delay

@Composable
fun SettingDialog(
    modifier: Modifier = Modifier,
    httpValue: String,
    embeddingModel: String,
    onHttpValueChange: (String) -> Unit,
    onEmbeddingModelValueChange: (String) -> Unit,
    onCheckAddressClick: (String) -> Unit,
    isOllamaAddressCorrect: Boolean,
    onPullEmbeddingModelClick: (String) -> Unit,
    isEmbeddingModelPulled: Boolean,
    isEmbeddingModelPulling: Boolean,
    statusError: NetworkError?,
    pullError: NetworkError?,
    tagError: NetworkError?,
    onClose: () -> Unit,
) {
    var isAddressChecking by remember { mutableStateOf(false) }
    var isAddressChanged by remember { mutableStateOf(false) }
    var isEmbeddingModelChanged by remember { mutableStateOf(false) }
    val isAddressIncorrect by remember(statusError) { derivedStateOf { statusError != null } }
    val isPullFailed by remember(pullError) { derivedStateOf { pullError != null } }

    val animatedHeight by animateIntAsState(
        targetValue = when{
            (isAddressIncorrect && isPullFailed) -> 400
            (isAddressIncorrect xor isPullFailed) -> 250
            else -> 200
        },
        label = "Animated Height"
    )

    Dialog(
        onDismissRequest = { onClose() }
    ) {
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.large)
                .size(width = 300.dp, height = animatedHeight.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = httpValue,
                    onValueChange = {
                        onHttpValueChange(it)
                        isAddressChanged = true
                                    },
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    label = { Text(text = "Ollama address") },
                    isError = isAddressIncorrect,
                    shape = RoundedCornerShape(30),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { isAddressChecking = true }
                    ),
                    singleLine = true,
                    trailingIcon = {
                        if(httpValue != "") {
                            Box(contentAlignment = Alignment.Center) {
                                if (!isOllamaAddressCorrect || isAddressChanged) {
                                    if (!isAddressChecking) {
                                        CustomButton(
                                            icon = R.drawable.baseline_refresh_24,
                                            description = "Check Address",
                                            onButtonClick = {
                                                isAddressChecking = true
                                                            },
                                            buttonSize = 38
                                        )
                                    } else {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_check_24),
                                        contentDescription = "Correct Address"
                                    )
                                }
                                LaunchedEffect(isAddressChecking) {
                                    onCheckAddressClick(httpValue)
                                    delay(1000)
                                    isAddressChecking = false
                                    isAddressChanged = false
                                }
                            }
                        }
                    }
                )
                AnimatedVisibility(visible = isAddressIncorrect) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        statusError?.let {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(it.error.message),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "${it.t.message}",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                TextField(
                    value = embeddingModel,
                    onValueChange = {
                        onEmbeddingModelValueChange(it)
                        isEmbeddingModelChanged = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    label = { Text(text = "Embedding Model") },
                    isError = isPullFailed,
                    shape = RoundedCornerShape(30),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onPullEmbeddingModelClick(embeddingModel)
                            isEmbeddingModelChanged = false
                        }
                    ),
                    trailingIcon = {
                        if(isOllamaAddressCorrect && embeddingModel != ""){
                            if(!isEmbeddingModelPulled || isEmbeddingModelChanged) {
                                if(!isEmbeddingModelPulling) {
                                    CustomButton(
                                        onButtonClick = {
                                            onPullEmbeddingModelClick(embeddingModel)
                                            isEmbeddingModelChanged = false
                                        },
                                        description = "Pull Model",
                                        icon = R.drawable.baseline_cloud_download_24,
                                        buttonSize = 38
                                    )
                                }else{
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }else{
                                Icon(
                                    painter = painterResource(R.drawable.baseline_check_24),
                                    contentDescription = "Embedding Model Pulled"
                                )
                            }

                        }
                    },
                    enabled = isOllamaAddressCorrect
                )
                AnimatedVisibility(visible = isPullFailed) {
                    Box(modifier = Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                        pullError?.let {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(it.error.message),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "${it.t.message}",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingDialogPreview() {
    OllamaUITheme {
        SettingDialog(
            httpValue = "ss",
            embeddingModel = "all-minilm",
            onHttpValueChange = {},
            onEmbeddingModelValueChange = {},
            isOllamaAddressCorrect = true,
            isEmbeddingModelPulled = false,
            onCheckAddressClick = {},
            onPullEmbeddingModelClick = { },
            isEmbeddingModelPulling = false,
            onClose = {},
            statusError = null,
            tagError = null,
            pullError = null
        )
    }
}