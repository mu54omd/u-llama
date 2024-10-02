package com.example.ollamaui.activity

data class MainStates(
    val isOllamaAddressSet: Boolean = false,
    val ollamaAddress: String = "http://localhost:11434",
    val isLocalSettingLoaded: Boolean = false
)
