package com.example.ollamaui.ui.screen.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun CustomSettingBox(
    title: String,
    rowVerticalAlignment: Alignment.Vertical = Alignment.Top,
    rowHorizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    content: @Composable () -> Unit,
) {
    Box {
        Row(
            verticalAlignment = rowVerticalAlignment,
            horizontalArrangement = rowHorizontalArrangement,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 35.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                )
        ) {
            content()
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            modifier = Modifier
                .offset(20.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .padding(10.dp)
        )
    }
}

@Preview(showSystemUi = false)
@Composable
private fun CustomSettingBoxPreview() {
    OllamaUITheme {
        CustomSettingBox(
            title = "Title"
        ) {
            Column(modifier = Modifier.padding(40.dp)) {
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
                Text("Some Item")
            }
        }
    }
}