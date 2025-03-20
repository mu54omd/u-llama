package com.example.ollamaui.ui.screen.common

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    modifierIcon: Modifier = Modifier,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean = true,
    buttonSize: Int = 25,
    iconSize: Int = 25,
    description: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    elevation: Int = 0,
    @DrawableRes icon: Int,
) {
    Button(
        onClick = onButtonClick,
        enabled = isButtonEnabled,
        modifier = modifier.size(buttonSize.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = containerColor,
            disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(red = 0.5f, blue = 0.5f, green = 0.5f)
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = elevation.dp)
    ){
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = modifierIcon.size(iconSize.dp),
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SendButtonPreview() {
    OllamaUITheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CustomButton(
                onButtonClick = {},
                description = "CustomButton",
                icon = R.drawable.baseline_send_24,
                iconSize = 25,
                buttonSize = 50,
                isButtonEnabled = true,
                elevation = 9
            )
        }
    }
}