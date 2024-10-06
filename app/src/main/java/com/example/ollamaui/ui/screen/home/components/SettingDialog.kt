package com.example.ollamaui.ui.screen.home.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun SettingDialog(
    modifier: Modifier = Modifier,
    httpValue: String,
    onValueChange: (String) -> Unit,
    onAcceptClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onCloseClick() }
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
                TextField(
                    value = httpValue,
                    onValueChange = onValueChange,
                    label = { Text(text = "Ollama address") },
                    shape = RoundedCornerShape(30),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onAcceptClick() }
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomButton(
                        description = "Accept",
                        onButtonClick = onAcceptClick,
                        icon = R.drawable.baseline_check_24,
                        buttonSize = 50,
                        isButtonEnabled = httpValue.isNotEmpty(),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    CustomButton(
                        description = "Close",
                        onButtonClick = onCloseClick,
                        icon = R.drawable.baseline_clear_24,
                        buttonSize = 50,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
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
            onCloseClick = {},
            onAcceptClick = {},
            onValueChange = {}
        )
    }
}