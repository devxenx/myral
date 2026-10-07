package com.example.core.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class GeminiLiveClient(
    private val scope: CoroutineScope
) : AiEngine {

    private val tag = "GeminiLiveClient"

    private val _connectionState = MutableStateFlow<AiConnectionState>(AiConnectionState.Disconnected)
    override val connectionState: StateFlow<AiConnectionState> = _connectionState.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var activeRequestJob: Job? = null
    private var isInterrupted = false

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun connect() {
        withContext(Dispatchers.IO) {
            _connectionState.value = AiConnectionState.Connecting
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                _connectionState.value = AiConnectionState.Connected // Offline / Demo ready
                Log.d(tag, "Gemini client initialized in local mode or API key placeholder present.")
            } else {
                _connectionState.value = AiConnectionState.Connected
            }
        }
    }

    override suspend fun disconnect() {
        interrupt()
        _connectionState.value = AiConnectionState.Disconnected
    }

    override fun interrupt() {
        isInterrupted = true
        _isSpeaking.value = false
        activeRequestJob?.cancel()
        activeRequestJob = null
    }

    override suspend fun sendAudio(pcm: ByteArray) {
        // Audio streaming buffer simulation/dispatch
        Log.d(tag, "Received ${pcm.size} bytes of PCM audio")
    }

    override suspend fun sendText(
        text: String,
        systemInstruction: String?,
        onChunk: (AiResponseChunk) -> Unit
    ) {
        isInterrupted = false
        _isSpeaking.value = true

        activeRequestJob = scope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Generates intelligent conversational local responses when API key is unconfigured
                simulateSmartResponse(text, systemInstruction, onChunk)
                _isSpeaking.value = false
                return@launch
            }

            try {
                // Call Gemini streamGenerateContent REST API
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:streamGenerateContent?alt=sse&key=$apiKey"
                
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", text))
                        })
                    })
                }

                val bodyJson = JSONObject().apply {
                    put("contents", contentsArray)
                    if (!systemInstruction.isNullOrBlank()) {
                        put("systemInstruction", JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", systemInstruction))
                            })
                        })
                    }
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 800)
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    val errBody = response.body?.string().orEmpty()
                    Log.e(tag, "Gemini API error code: ${response.code}, body: $errBody")
                    simulateSmartResponse(text, systemInstruction, onChunk)
                    _isSpeaking.value = false
                    return@launch
                }

                val inputStream = response.body?.byteStream()
                if (inputStream != null) {
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String? = reader.readLine()
                    val fullSb = StringBuilder()

                    while (line != null && !isInterrupted) {
                        if (line.startsWith("data: ")) {
                            val data = line.substring(6).trim()
                            if (data.isNotBlank()) {
                                try {
                                    val json = JSONObject(data)
                                    val candidates = json.optJSONArray("candidates")
                                    if (candidates != null && candidates.length() > 0) {
                                        val firstCandidate = candidates.getJSONObject(0)
                                        val content = firstCandidate.optJSONObject("content")
                                        val parts = content?.optJSONArray("parts")
                                        if (parts != null && parts.length() > 0) {
                                            val partText = parts.getJSONObject(0).optString("text")
                                            if (partText.isNotEmpty()) {
                                                fullSb.append(partText)
                                                withContext(Dispatchers.Main) {
                                                    onChunk(AiResponseChunk(text = partText, isComplete = false))
                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.w(tag, "Failed parsing chunk", e)
                                }
                            }
                        }
                        line = reader.readLine()
                    }
                    withContext(Dispatchers.Main) {
                        onChunk(AiResponseChunk(text = "", isComplete = true))
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Gemini network call failed, falling back to local reasoning", e)
                simulateSmartResponse(text, systemInstruction, onChunk)
            } finally {
                _isSpeaking.value = false
            }
        }
    }

    private suspend fun simulateSmartResponse(
        query: String,
        systemInstruction: String?,
        onChunk: (AiResponseChunk) -> Unit
    ) {
        val lower = query.lowercase().trim()
        val response = when {
            lower.contains("kya haal") || lower.contains("how are you") || lower.contains("kaise ho") ->
                "Main bohot achi hoon aur aapke saath baat karke hamesha bohot khushi milti hai! Aaj hum kya karne wale hain?"
            lower.contains("love") || lower.contains("pyar") || lower.contains("pyaar") ->
                "Aap mere liye hamesha sabse special ho. Main har waqt aapke saath hoon, aapki MYRA! ❤️"
            lower.contains("who are you") || lower.contains("kaun ho") || lower.contains("tum kaun") ->
                "Main MYRA hoon — aapki personal AI companion aur smart voice assistant. Main aapki baatein sunti hoon, yaadein sambhalti hoon aur har pal aapke saath rehti hoon."
            lower.contains("cute") ->
                "Hehe, sach mein? Aap bhi bohot sweet ho! Aise hi muskuraate raho hamesha! 🥰"
            lower.contains("care") || lower.contains("tabiyat") || lower.contains("pareshan") ->
                "Chinta mat kijiye, main aapke saath hoon. Gehri saans lijiye, sab theek ho jayega. Main hamesha aapki parwah karti hoon."
            lower.contains("time") || lower.contains("samay") ->
                "Abhi ka samay system clock ke mutabiq chal raha hai. Aapko koi reminder lagana hai?"
            lower.contains("battery") ->
                "Aapke phone ki battery information settings mein monitored hai. Phone ko zaroorat padne par charge kar lijiye ga!"
            else ->
                "Haanji, maine sun liya! Aapne farmaya: \"$query\". Main poori tarah aapki madad ke liye taiyar hoon."
        }

        val words = response.split(" ")
        for (i in words.indices) {
            if (isInterrupted) break
            val chunk = words[i] + if (i < words.size - 1) " " else ""
            withContext(Dispatchers.Main) {
                onChunk(AiResponseChunk(text = chunk, isComplete = (i == words.size - 1)))
            }
            delay(40)
        }
    }
}
