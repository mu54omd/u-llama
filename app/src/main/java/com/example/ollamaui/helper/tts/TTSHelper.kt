package com.example.ollamaui.helper.tts

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

class TTSHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        isInitialized = status == TextToSpeech.SUCCESS
        if (isInitialized) {
            tts.language = Locale.US
        }
    }

    fun speak(text: String, onDone: () -> Unit) {
        if (!isInitialized) return
        val utteranceId = UUID.randomUUID().toString()
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
            }

            override fun onDone(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post { onDone() }
            }

            override fun onError(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post { onDone() }
            }
        }
        )
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts.stop()
    }

    fun shutdown() {
        stop()
        tts.shutdown()
    }
}