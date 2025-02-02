package com.example.ollamaui.data.local.objectbox

import android.content.Context
import android.util.Log
import com.example.ollamaui.domain.model.objectbox.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.Admin

object ObjectBoxStore {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder().androidContext(context).build()
        val started = Admin(store).start(context);
        Log.i("cTAG", "Started: $started")
    }
}