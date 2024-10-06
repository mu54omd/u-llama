package com.example.ollamaui.ui.screen.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean = true,
    buttonSize: Int = 25,
    iconSize: Int = 25,
    description: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    @DrawableRes icon: Int,
) {
    Button(
        onClick = { onButtonClick() },
        enabled = isButtonEnabled,
        modifier = modifier.size(buttonSize.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ){
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = Modifier.size(iconSize.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun SendButtonPreview() {
    OllamaUITheme {
        CustomButton(
            onButtonClick = {},
            description = "CustomButton",
            icon = R.drawable.baseline_send_24
        )
    }
}