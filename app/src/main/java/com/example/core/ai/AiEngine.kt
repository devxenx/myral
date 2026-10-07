package com.example.core.ai

import kotlinx.coroutines.flow.StateFlow

sealed interface AiConnectionState {
    object Disconnected : AiConnectionState
    object Connecting : AiConnectionState
    object Connected : AiConnectionState
    object Reconnecting : AiConnectionState
    data class Error(val message: String) : AiConnectionState
}

data class AiResponseChunk(
    val text: String,
    val isComplete: Boolean = false,
    val detectedEmotion: String? = null
)

interface AiEngine {
    val connectionState: StateFlow<AiConnectionState>
    val isSpeaking: StateFlow<Boolean>
    
    suspend fun connect()
    suspend fun disconnect()
    suspend fun sendText(text: String, systemInstruction: String? = null, onChunk: (AiResponseChunk) -> Unit)
    suspend fun sendAudio(pcm: ByteArray)
    fun interrupt()
}
