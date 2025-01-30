package com.example.ollamaui.ui.screen.common

import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.derivedStateOf
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
fun CustomDropDownList(
    modifier: Modifier = Modifier,
    label: String = "Selected Model",
    defaultValue: String = "",
    width: Int = 200,
    listItems: List<String>,
    onItemClick: (String) -> Unit,
) {
    var isExpanded by remember{ mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(defaultValue) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(top = 5.dp, bottom = 5.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded}
        ) {
            TextField(
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).width(width.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                label = { Text(label) }
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false},
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.height(100.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer
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
        Column(modifier = Modifier.fillMaxSize()) {
            CustomDropDownList(
                defaultValue = "a",
                listItems = listOf("a", "b", "c", "d", "b", "c", "d", "b", "c", "d"),
                onItemClick = {}
            )
        }
    }
}