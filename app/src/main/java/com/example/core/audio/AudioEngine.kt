package com.example.core.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class AudioEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val tag = "AudioEngine"

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private var recordJob: Job? = null
    private var audioRecord: AudioRecord? = null

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(2048)

    @SuppressLint("MissingPermission")
    fun startRecording(onPcmChunk: (ByteArray) -> Unit) {
        if (_isRecording.value) return

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.w(tag, "RECORD_AUDIO permission not granted")
            return
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(tag, "AudioRecord initialization failed")
                return
            }

            audioRecord?.startRecording()
            _isRecording.value = true

            recordJob = scope.launch(Dispatchers.IO) {
                val buffer = ByteArray(minBufferSize)
                while (isActive && _isRecording.value) {
                    val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readBytes > 0) {
                        val chunk = buffer.copyOf(readBytes)
                        onPcmChunk(chunk)

                        // Calculate RMS Amplitude
                        var sum = 0.0
                        for (i in 0 until readBytes step 2) {
                            if (i + 1 < readBytes) {
                                val sample = ((chunk[i + 1].toInt() shl 8) or (chunk[i].toInt() and 0xFF)).toShort()
                                sum += sample * sample
                            }
                        }
                        val rms = sqrt(sum / (readBytes / 2.0)).toFloat()
                        val normalized = (rms / 32768f).coerceIn(0f, 1f)
                        _amplitude.value = normalized
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starting recording", e)
            stopRecording()
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        _amplitude.value = 0f
        recordJob?.cancel()
        recordJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping audioRecord", e)
        }
        audioRecord = null
    }
}
