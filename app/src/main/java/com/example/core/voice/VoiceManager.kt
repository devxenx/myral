package com.example.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceManager(context: Context) {
    private val tag = "VoiceManager"
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var currentPitch = 1.05f
    private var currentSpeed = 1.0f

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                val result = tts?.setLanguage(Locale.forLanguageTag("hi-IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.US)
                }
                tts?.setPitch(currentPitch)
                tts?.setSpeechRate(currentSpeed)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            } else {
                Log.e(tag, "TTS Initialization failed with status: $status")
            }
        }
    }

    fun applyVoiceProfile(profile: VoiceProfile) {
        currentPitch = profile.pitch
        currentSpeed = profile.speed
        if (isTtsInitialized) {
            tts?.setPitch(currentPitch)
            tts?.setSpeechRate(currentSpeed)
            try {
                if (profile.language == "hi") {
                    tts?.setLanguage(Locale.forLanguageTag("hi-IN"))
                } else {
                    tts?.setLanguage(Locale.US)
                }
            } catch (e: Exception) {
                Log.w(tag, "Failed to set voice language", e)
            }
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (!isTtsInitialized || text.isBlank()) return
        stop()
        _isSpeaking.value = true
        val utteranceId = "myra_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        if (isTtsInitialized) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isTtsInitialized = false
    }
}
