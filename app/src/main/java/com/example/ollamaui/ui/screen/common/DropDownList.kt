package com.example.ollamaui.ui.screen.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownList(
    modifier: Modifier = Modifier,
    listItems: List<String>,
    onItemClick: (String) -> Unit,
) {
    var isExpanded by remember{ mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
        .fillMaxWidth()
        .padding(20.dp)){
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded}
        ) {
            TextField(
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).width(200.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                )
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false},
                shape = MaterialTheme.shapes.medium,
            ) {
                listItems.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        onClick = {
                            selectedItem = item
                            isExpanded = false
                            onItemClick(item)
                        },
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun DropDownListPreview() {
    OllamaUITheme {
        DropDownList(
            listItems = listOf("a", "b", "c", "d"),
            onItemClick = {}
        )
    }
}