package com.example.ollamaui.ui.screen.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.CustomDropDownList
import com.example.ollamaui.ui.screen.setting.components.CustomSettingBox
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun SettingScreen(
    savedParameters: List<String>,
    ollamaStatus: String,
    embeddingModelList: List<String>,
    isEmbeddingModelPulled: (String) -> Boolean,
    onResetClick: () -> Unit,
    onSaveClick: (url: String,embeddingModel: String) -> Unit,
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
    var isChecked by remember { mutableStateOf(false) }
    var isSelectedModelPulled by remember { mutableStateOf(isEmbeddingModelPulled(selectedEmbeddingModel)) }

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
                supportingText = { if(isChecked) Text(text = ollamaStatus) },
                modifier = Modifier
                    .weight(0.4f)
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
            Text(text = "Under construction", modifier = Modifier.padding(30.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {}
            ) {
                Text(text = "Reset")
            }

            Button(
                onClick = {
                    onSaveClick("http://$ipAddress:$port", selectedEmbeddingModel)
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
            savedParameters = listOf("http://127.0.0.1:11434", "al-minilm"),
            embeddingModelList = listOf("al-minilm", "llama3.2"),
            isEmbeddingModelPulled = { true },
            ollamaStatus = "Ollama is Running",
            onResetClick = {},
            onSaveClick = {_,_-> },
            onBackClick = {},
            onCheckClick = {},
            onPullEmbeddingModelClick = {},
            onFetchEmbeddingModelClick = {}
        )
    }
}