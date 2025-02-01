package com.example.ollamaui.ui.screen.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.CustomDropDownList
import com.example.ollamaui.ui.screen.setting.components.CustomSettingBox
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun SettingScreen(

    onBackClick: () -> Unit,
) {
    val ipRegex = remember { Regex("\\d{0,3}\\.?\\d{0,3}\\.?\\d{0,3}\\.?\\d{0,3}") }
    val portRegex = remember { Regex("\\d{0,5}") }
    var ipAddress by rememberSaveable { mutableStateOf("") }
    var port by remember { mutableStateOf("") }

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
                supportingText = { Text("Network Response will show here.") },
                modifier = Modifier
                    .weight(0.75f)
                    .padding(start = 10.dp, top = 10.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            OutlinedTextField(
                value = port,
                onValueChange = { input -> if(portRegex.matches(input)) port = input },
                label = { Text(text = "Port") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    ),
                supportingText = { Text("") },
                singleLine = true,
                modifier = Modifier
                    .weight(0.25f)
                    .padding(top = 10.dp , end = 10.dp, bottom = 20.dp)
            )

        }
        CustomSettingBox(
            title = "Embedding Model",
            rowHorizontalArrangement = Arrangement.Start
        ) {
            CustomDropDownList(
                listItems = listOf("a",  "b"),
                onItemClick = {},
                modifier = Modifier.padding(start = 10.dp, top = 20.dp, bottom = 10.dp)
            )
        }
        CustomSettingBox(
            title = "Tuning Parameters",
        ) {
            Text(text = "Under construction", modifier = Modifier.padding(30.dp))
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
        SettingScreen {

        }
    }
}