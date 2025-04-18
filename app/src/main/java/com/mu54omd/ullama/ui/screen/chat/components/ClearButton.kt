package com.mu54omd.ullama.ui.screen.chat.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.theme.ULlamaTheme

@Composable
fun ClearButton(
    onClearClick: () -> Unit
) {
    Button(
        onClick = { onClearClick() },
        modifier = Modifier.size(25.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ){
        Icon(
            painter = painterResource(R.drawable.baseline_clear_24),
            contentDescription = "Clear Icon",
            modifier = Modifier.size(25.dp)
        )
    }
}

@Preview
@Composable
private fun SendButtonPreview() {
    ULlamaTheme {
        ClearButton(
            onClearClick = {}
        )
    }
}