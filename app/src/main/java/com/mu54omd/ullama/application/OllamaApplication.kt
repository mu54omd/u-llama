package com.mu54omd.ullama.application

import android.app.Application
import com.mu54omd.ullama.data.local.objectbox.ObjectBoxStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OllamaApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        ObjectBoxStore.init(this)
    }
}