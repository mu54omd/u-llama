package com.example.ollamaui.application

import android.app.Application
import com.example.ollamaui.data.local.objectbox.ObjectBoxStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OllamaApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        ObjectBoxStore.init(this)
    }
}