package com.example.ollamaui.ui.screen.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.chat.DefaultModelParameters
import com.example.ollamaui.domain.model.chat.ModelParameters
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.CustomDropDownList
import com.example.ollamaui.ui.screen.setting.components.CustomSettingBox
import com.example.ollamaui.ui.screen.setting.components.TuningSlider
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun SettingScreen(
    savedParameters: List<String>,
    ollamaStatus: String,
    embeddingModelList: List<String>,
    isEmbeddingModelPulled: (String) -> Boolean,
    onSetAsDefaultClick: () -> Unit,
    onResetClick: () -> Unit,
    onSaveClick: (url: String,embeddingModel: String, tuningParameters: ModelParameters) -> Unit,
    onCheckClick: (url: String) -> Unit,
    onPullEmbeddingModelClick: (String) -> Unit,
    onFetchEmbeddingModelClick: () -> Unit,
    onBackClick: () -> Unit,
) {

    val ipRegex = remember { Regex("\\d{0,3}\\.?\\d{0,3}\\.?\\d{0,3}\\.?\\d{0,3}") }
    val portRegex = remember { Regex("\\d{0,5}") }
    var ipAddress by rememberSaveable { mutableStateOf(savedParameters[0].split("//")[1].split(":")[0]) }
    var port by rememberSaveable { mutableStateOf(savedParameters[0].split("//")[1].split(":")[1]) }
    var selectedEmbeddingModel by rememberSaveable { mutableStateOf(savedParameters[1]) }
    var networkStatus by remember { mutableStateOf("") }
    var isChecked by rememberSaveable { mutableStateOf(false) }
    var isSelectedModelPulled by remember { mutableStateOf(isEmbeddingModelPulled(selectedEmbeddingModel)) }
    val sliderPositions = remember {
        mutableStateListOf<Float>(
            savedParameters[2].toFloat(),
            savedParameters[3].toFloat(),
            savedParameters[4].toFloat(),
            savedParameters[5].toFloat(),
            savedParameters[6].toFloat(),
            savedParameters[7].toFloat(),
            savedParameters[8].toFloat()
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 30.dp)
            .fillMaxSize()
    ) {
        CustomSettingBox(
            title = "Ollama Address",
        ) {
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { input ->
                    if(ipRegex.matches(input)) {
                        ipAddress = input
                    }
                },
                label = { Text(text = "Ip address") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                singleLine = true,
                supportingText = {
                    if(isChecked) {
                        Text(text = ollamaStatus)
                    }
                },
                modifier = Modifier
                    .weight(0.3f)
                    .padding(start = 10.dp, top = 20.dp, bottom = 20.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedTextField(
                value = port,
                onValueChange = { input -> if(portRegex.matches(input)) port = input },
                label = { Text(text = "Port") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    ),
                singleLine = true,
                modifier = Modifier
                    .weight(0.2f)
                    .padding(top = 20.dp , end = 10.dp, bottom = 20.dp)
            )
            Button(
                onClick = {
                    onCheckClick("http://$ipAddress:$port")
                    networkStatus = ollamaStatus
                    isChecked = true
                },
                modifier = Modifier.padding(top = 30.dp, end = 10.dp)
            ) {
                Text("Check")
            }
        }
        CustomSettingBox(
            title = "Embedding Model",
            rowHorizontalArrangement = Arrangement.Start
        ) {
            CustomDropDownList(
                listItems = embeddingModelList,
                onItemClick = {
                    selectedEmbeddingModel = it
                    isSelectedModelPulled = isEmbeddingModelPulled(selectedEmbeddingModel)
                              },
                defaultValue = selectedEmbeddingModel,
                modifier = Modifier.padding(start = 10.dp, top = 20.dp, bottom = 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 40.dp, end = 10.dp)
            ) {
                CustomButton(
                    onButtonClick = {
                        onFetchEmbeddingModelClick()
                    },
                    description = "re-fetch",
                    icon = R.drawable.baseline_refresh_24,
                    containerColor = Color.Transparent
                )
                CustomButton(
                    onButtonClick = {
                        onPullEmbeddingModelClick(selectedEmbeddingModel)
                    },
                    description = "pull",
                    icon = R.drawable.baseline_cloud_download_24,
                    containerColor = Color.Transparent
                )
                AnimatedVisibility(
                    visible = isSelectedModelPulled,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_check_24),
                        contentDescription = "isModelPulled",
                    )
                }
            }
        }
        CustomSettingBox(
            title = "Tuning Parameters",
        ) {
            Column(
                modifier = Modifier.padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                TuningSlider(
                    title = "Temperature",
                    sliderPosition = sliderPositions[0],
                    onSliderChange = { sliderPositions[0] = it },
                    explanation = "Regulates the unpredictability of output.",
                    isInteger = false,
                    startPosition = 0f,
                    endPosition = 2f,
                )
                TuningSlider(
                    title = "Context size",
                    sliderPosition = sliderPositions[1],
                    onSliderChange = { sliderPositions[1] = it },
                    explanation = "Sets the size of the context window\nused to generate the next token.",
                    isInteger = true,
                    startPosition = 1f,
                    endPosition = 8192f,
                )
                TuningSlider(
                    title = "Frequency penalty",
                    sliderPosition = sliderPositions[2],
                    onSliderChange = { sliderPositions[2] = it },
                    explanation = "Discourages repetition proportionally\nto how frequently they appear.",
                    isInteger = false,
                    startPosition = 0f,
                    endPosition = 2f
                )
                TuningSlider(
                    title = "Presence penalty",
                    sliderPosition = sliderPositions[3],
                    onSliderChange = { sliderPositions[3] = it },
                    explanation = "Discourages repetition based on\nif they have occurred or not.",
                    isInteger = false,
                    startPosition = 0f,
                    endPosition = 2f
                )
                TuningSlider(
                    title = "Top K",
                    sliderPosition = sliderPositions[4],
                    onSliderChange = { sliderPositions[4] = it },
                    explanation = "Reduces the probability of generating nonsense.\nHigher value will give more diverse answers",
                    isInteger = true,
                    startPosition = 0f,
                    endPosition = 100f,
                )
                TuningSlider(
                    title = "Top P ",
                    sliderPosition = sliderPositions[5],
                    onSliderChange = { sliderPositions[5] = it },
                    explanation = "Manage the randomness of their output.\nHigher value will lead to more diverse text.",
                    isInteger = false,
                    startPosition = 0f,
                    endPosition = 2f,
                )
                TuningSlider(
                    title = "Min P",
                    sliderPosition = sliderPositions[6],
                    onSliderChange = { sliderPositions[6] = it },
                    explanation = "Aims to ensure a balance of quality and variety.",
                    isInteger = false,
                    startPosition = 0f,
                    endPosition = 2f,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    onSetAsDefaultClick()
                }
            ) {
                Text(text = "Set as default")
            }
            Button(
                onClick = {
                    ipAddress = "127.0.0.1"
                    port = "11434"
                    selectedEmbeddingModel = ""
                    sliderPositions[0] = DefaultModelParameters.default.temperature
                    sliderPositions[1] = DefaultModelParameters.default.numCtx.toFloat()
                    sliderPositions[2] = DefaultModelParameters.default.presencePenalty
                    sliderPositions[3] = DefaultModelParameters.default.frequencyPenalty
                    sliderPositions[4] = DefaultModelParameters.default.topK.toFloat()
                    sliderPositions[5] = DefaultModelParameters.default.topP
                    sliderPositions[6] = DefaultModelParameters.default.minP
                }
            ) {
                Text(text = "Reset")
            }

            Button(
                onClick = {
                    onSaveClick(
                        "http://$ipAddress:$port",
                        selectedEmbeddingModel,
                        ModelParameters(
                            temperature = sliderPositions[0],
                            numCtx = sliderPositions[1].toInt(),
                            presencePenalty = sliderPositions[2],
                            frequencyPenalty = sliderPositions[3],
                            topK = sliderPositions[4].toInt(),
                            topP = sliderPositions[5],
                            minP = sliderPositions[6]
                        )
                    )
                }
            ) {
                Text(text = "Save")
            }
        }
    }
    BackHandler {
        onBackClick()
    }
}

@Preview
@Composable
private fun SettingScreenPreview() {
    OllamaUITheme {
        SettingScreen(
            savedParameters = listOf("http://127.0.0.1:11434", "al-minilm","1.0","2048", "1.0", "1.5", "40", "1.0", "0.9"),
            embeddingModelList = listOf("all-minilm", "llama3.2"),
            isEmbeddingModelPulled = { true },
            ollamaStatus = "Ollama is Running",
            onSetAsDefaultClick = {},
            onResetClick = {},
            onSaveClick = {_,_,_-> },
            onBackClick = {},
            onCheckClick = {},
            onPullEmbeddingModelClick = {},
            onFetchEmbeddingModelClick = {}
        )
    }
}