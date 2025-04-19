package com.mu54omd.ullama.ui.screen.home.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.R
import com.mu54omd.ullama.helper.network.NetworkStatus
import com.mu54omd.ullama.ui.screen.common.CustomButton
import com.mu54omd.ullama.ui.theme.ULlamaTheme
import com.mu54omd.ullama.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onSettingClick: () -> Unit,
    onFileManagerClick: () -> Unit,
    onLogClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeselectClick: () -> Unit,
    onSelectClick: () -> Unit,
    isSelectedChatsEmpty: Boolean,
    chatsListSize: Int,
    networkStatus: NetworkStatus
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .height(TOP_BAR_HEIGHT)
                .padding(start = 10.dp, end = 10.dp),
            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                CustomButton(
                    description = "Setting Button",
                    onButtonClick = onSettingClick,
                    icon = R.drawable.baseline_settings_24,
                    buttonSize = 40,
                    containerColor = MaterialTheme.colorScheme.background
                )
                CustomButton(
                    description = "File Manager Button",
                    onButtonClick = onFileManagerClick,
                    icon = R.drawable.baseline_folder_24,
                    buttonSize = 40,
                    containerColor = MaterialTheme.colorScheme.background
                )
            }
            LogoTitle(
                lightLogo = R.drawable.icon,
                darkLogo = R.drawable.icon,
                networkStatus = networkStatus,
                modifier = Modifier.align(Alignment.Center),
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Crossfade(
                    targetState = isSelectedChatsEmpty, label = "HomeTopBar Button",
                ) { isSelectedChatsEmpty ->
                    when(isSelectedChatsEmpty) {
                        false ->
                            Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    CustomButton(
                                        description = "DeselectAll Button",
                                        onButtonClick = onDeselectClick,
                                        icon = R.drawable.baseline_deselect_24,
                                        buttonSize = 40,
                                        containerColor = MaterialTheme.colorScheme.background
                                    )
                                    CustomButton(
                                        description = "Delete Button",
                                        onButtonClick = onDeleteClick,
                                        icon = R.drawable.baseline_delete_outline_24,
                                        buttonSize = 40,
                                        containerColor = MaterialTheme.colorScheme.background
                                    )
                                }
                        true ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                                ) {
                                CustomButton(
                                    description = "SelectAll Button",
                                    onButtonClick = onSelectClick,
                                    icon = R.drawable.baseline_select_all_24,
                                    buttonSize = 40,
                                    containerColor = MaterialTheme.colorScheme.background,
                                    isButtonEnabled = chatsListSize > 0
                                )
                                CustomButton(
                                    description = "About Button",
                                    onButtonClick = onLogClick,
                                    icon = R.drawable.baseline_info_outline_24,
                                    buttonSize = 40,
                                    containerColor = MaterialTheme.colorScheme.background,
                                )
                            }
                    }
                }
            }
        }
        HorizontalDivider(thickness = 2.dp)
    }
}

@Preview
@Composable
private fun HomeTopBarPreview() {
    ULlamaTheme {
        HomeTopBar(
            onLogClick = {},
            onSettingClick = {},
            onFileManagerClick = {},
            onDeleteClick = {},
            onDeselectClick = {},
            onSelectClick = {},
            isSelectedChatsEmpty = true,
            chatsListSize = 0,
            networkStatus = NetworkStatus.CONNECTED
        )
    }
}