package com.example.ollamaui.ui.screen.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun TuningSlider(
    modifier: Modifier = Modifier,
    title: String = "Parameter",
    explanation: String = "Parameter explanation",
    decimalFilter: String = "%.2f",
    defaultValue: Float = 0f,
    isInteger: Boolean = true,
    startPosition: Float = 0f,
    endPosition: Float = 1f,
) {
    var isExplanationShowed by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf( defaultValue ) }
    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            .height(110.dp)
    ){
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Text(text = title)
                    Spacer(modifier = Modifier.width(5.dp))
                    CustomButton(
                        onButtonClick = {
                            isExplanationShowed = !isExplanationShowed
                        },
                        icon = R.drawable.baseline_question_mark_24,
                        description = "Parameter explanation",
                        iconSize = 15,
                        buttonSize = 20
                    )
                }
                if (isExplanationShowed) {
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(10)
                            )
                            .padding(10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.7f))
            BasicTextField(
                value = if(isInteger) sliderPosition.toInt().toString() else decimalFilter.format(sliderPosition),
                onValueChange = {},
                modifier = Modifier.width(60.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                enabled = true,
                readOnly = true,
                maxLines = 1,
                decorationBox = {
                    innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(percent = 10)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                shape = RoundedCornerShape(10)
                            )
                    ) {
                        innerTextField()
                    }
                }
            )
        }
        if(isInteger) {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it.toInt().toFloat() },
                valueRange = startPosition..endPosition,
                modifier = Modifier.weight(0.5f),
            )
        }else{
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = startPosition..endPosition,
                modifier = Modifier.weight(0.5f)
            )
        }
    }

}

@Preview
@Composable
private fun TuningSliderPreview() {
    OllamaUITheme {
        TuningSlider(
            title = "Parameter",
            startPosition = -10.0f,
            endPosition = 10.0f,
            isInteger = true,
        )
    }
}