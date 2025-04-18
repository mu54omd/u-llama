package com.mu54omd.ullama.ui.screen.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.ui.screen.common.CustomButton
import com.mu54omd.ullama.ui.theme.OllamaUITheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuningSlider(
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Parameter",
    explanation: String = "Parameter explanation",
    decimalFilter: String = "%.2f",
    isInteger: Boolean = true,
    startPosition: Float = 0f,
    endPosition: Float = 1f,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
            .height(60.dp)
    ){
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            TooltipBox(
                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                tooltip = {
                    Text(
                        text = explanation,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
                                shape = RoundedCornerShape(30)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(30)
                            )
                            .padding(10.dp)
                    )
                          },
                state = tooltipState,
                focusable = false
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground
                        )
                    Spacer(modifier = Modifier.width(5.dp))
                    CustomButton(
                        onButtonClick = {
                            scope.launch {
                                tooltipState.show()
                            }
                        },
                        icon = R.drawable.baseline_question_mark_24,
                        description = "Parameter explanation",
                        iconSize = 15,
                        buttonSize = 20
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.7f))
            BasicTextField(
                value = if(isInteger) sliderPosition.toInt().toString() else decimalFilter.format(sliderPosition),
                onValueChange = {},
                modifier = Modifier.width(60.dp),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
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
                                color = Color.Transparent,
                                shape = RoundedCornerShape(percent = 30)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(percent = 30)
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
                onValueChange = { onSliderChange(it) },
                valueRange = startPosition..endPosition,
                modifier = Modifier.weight(0.5f),
                interactionSource = interactionSource,
                thumb = {
                    Spacer(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(100)
                            )
                            .size(20.dp)

                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(4.dp),
                        thumbTrackGapSize = 5.dp,
                        trackInsideCornerSize = 20.dp
                    )
                }
            )
        }else{
            Slider(
                value = sliderPosition,
                onValueChange = { onSliderChange(it) },
                valueRange = startPosition..endPosition,
                modifier = Modifier.weight(0.5f),
                interactionSource = interactionSource,
                thumb = {
                    Spacer(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(100)
                            )
                            .size(20.dp)

                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(4.dp),
                        thumbTrackGapSize = 5.dp,
                        trackInsideCornerSize = 20.dp,
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun TuningSliderPreview() {
    OllamaUITheme {
        TuningSlider(
            sliderPosition = 4f,
            onSliderChange = {},
            title = "Parameter",
            startPosition = -10.0f,
            endPosition = 10.0f,
            isInteger = true,
        )
    }
}