package com.example.ollamaui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.preferences.LocalUserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userLocalUserManager: LocalUserManager,
):ViewModel() {

    private val _mainState = MutableStateFlow(MainStates())
    val mainState = _mainState.asStateFlow()

    init {
        loadLocalSetting()
    }
    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun saveLocalSetting(url: String){
        viewModelScope.launch {
            userLocalUserManager.saveOllamaUrl(url = url)
            _mainState.update {
                it.copy(isOllamaAddressSet = true, ollamaAddress = url)
            }
            Log.d("TAG:Write", "${mainState.value}")
        }
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    private fun loadLocalSetting(){
        viewModelScope.launch {
            _mainState.update {
                it.copy(
                    isOllamaAddressSet = userLocalUserManager.readOllamaAddressStatus().first(),
                    ollamaAddress = userLocalUserManager.readOllamaUrl().first(),
                    isLocalSettingLoaded = true
                )
            }
            Log.d("TAG:Load", "${mainState.value}")
        }
    }
}