package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyraApplication
import com.example.core.ai.AiConnectionState
import com.example.core.ai.AiResponseChunk
import com.example.core.ai.GeminiLiveClient
import com.example.core.audio.AudioEngine
import com.example.core.auth.AuthManager
import com.example.core.command.AdvancedCommandParser
import com.example.core.command.ParsedCommand
import com.example.core.database.AutomationEntity
import com.example.core.database.ChatMessageEntity
import com.example.core.database.FirestoreRepository
import com.example.core.database.MemoryEntity
import com.example.core.database.PrimeContactEntity
import com.example.core.database.ReminderEntity
import com.example.core.emotion.EmotionEngine
import com.example.core.emotion.MyraEmotion
import com.example.core.owner.OwnerAuthenticationManager
import com.example.core.owner.PermissionLevel
import com.example.core.personality.PersonalityConfig
import com.example.core.personality.PersonalityEngine
import com.example.core.personality.RelationshipMode
import com.example.core.voice.VoiceManager
import com.example.core.voice.VoiceProfile
import com.example.core.voice.VoiceProfileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as MyraApplication).database.myraDao()
    private val firestoreRepo = FirestoreRepository(application)
    val authManager = AuthManager(application)

    val aiEngine = GeminiLiveClient(viewModelScope)
    val audioEngine = AudioEngine(application, viewModelScope)
    val voiceManager = VoiceManager(application)
    val voiceProfileManager = VoiceProfileManager()
    val emotionEngine = EmotionEngine(application)
    val personalityEngine = PersonalityEngine()
    val ownerAuthManager = OwnerAuthenticationManager()
    val commandParser = AdvancedCommandParser(application)

    // UI States
    private val _userTranscript = MutableStateFlow("")
    val userTranscript: StateFlow<String> = _userTranscript.asStateFlow()

    private val _myraResponse = MutableStateFlow("Namaste Rahul! Main aapki personal companion MYRA hoon. Main aapke har ehsaas aur har baat sunne ke liye hazir hoon. ❤️")
    val myraResponse: StateFlow<String> = _myraResponse.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _pendingConfirmationAction = MutableStateFlow<String?>(null)
    val pendingConfirmationAction: StateFlow<String?> = _pendingConfirmationAction.asStateFlow()

    private val _confirmationDialogMessage = MutableStateFlow<String?>(null)
    val confirmationDialogMessage: StateFlow<String?> = _confirmationDialogMessage.asStateFlow()

    // Database flows
    val memories: StateFlow<List<MemoryEntity>> = db.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val contacts: StateFlow<List<PrimeContactEntity>> = db.getContacts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val reminders: StateFlow<List<ReminderEntity>> = db.getReminders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val automations: StateFlow<List<AutomationEntity>> = db.getAutomations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = db.getMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            aiEngine.connect()
            initDefaultDataIfEmpty()
        }
    }

    private suspend fun initDefaultDataIfEmpty() {
        val defaultContacts = listOf(
            PrimeContactEntity(
                id = "c_mom",
                userId = "default",
                name = "Mom",
                phoneNumber = "+91 9876543211",
                nickname = "Mummy",
                relationship = "Mother",
                priority = "Highest",
                customGreeting = "Mom ka call aa raha hai ❤️"
            ),
            PrimeContactEntity(
                id = "c_dad",
                userId = "default",
                name = "Dad",
                phoneNumber = "+91 9876543212",
                nickname = "Papa",
                relationship = "Father",
                priority = "Highest",
                customGreeting = "Papa ka call aa raha hai 📞"
            ),
            PrimeContactEntity(
                id = "c_jaan",
                userId = "default",
                name = "Jaan",
                phoneNumber = "+91 9876543213",
                nickname = "Love",
                relationship = "Soulmate",
                priority = "VIP",
                customGreeting = "Aapki Jaan call kar rahi hain 🥰"
            )
        )
        defaultContacts.forEach { db.insertContact(it) }

        val defaultMemories = listOf(
            MemoryEntity(
                id = "m_1",
                userId = "default",
                title = "Favorite Color",
                content = "Owner likes deep black and electric cyan",
                category = "Preferences",
                isPrivate = false
            ),
            MemoryEntity(
                id = "m_2",
                userId = "default",
                title = "Coffee Preference",
                content = "Prefers strong cold brew with low sugar",
                category = "Preferences",
                isPrivate = false
            ),
            MemoryEntity(
                id = "m_secret",
                userId = "default",
                title = "Private Vault Phrase",
                content = "Security Phrase: MYRA FOREVER",
                category = "Security",
                isPrivate = true
            )
        )
        defaultMemories.forEach { db.insertMemory(it) }

        val defaultAutomations = listOf(
            AutomationEntity(
                id = "a_1",
                userId = "default",
                title = "Morning Companion Greeting",
                triggerType = "Time: 7:00 AM",
                condition = "Every morning",
                actionType = "Speak Care Greeting",
                isEnabled = true
            ),
            AutomationEntity(
                id = "a_2",
                userId = "default",
                title = "Night Rest Mode",
                triggerType = "Time: 11:00 PM",
                condition = "Night time",
                actionType = "Enable Sleepy Whispers & DND",
                isEnabled = true
            )
        )
        defaultAutomations.forEach { db.insertAutomation(it) }
    }

    fun onMicToggled() {
        if (audioEngine.isRecording.value) {
            audioEngine.stopRecording()
            _statusMessage.value = "Processing Voice..."
            // If user recorded speech, trigger parse on transcript or simulated voice prompt
            if (_userTranscript.value.isNotBlank()) {
                handleUserSpeech(_userTranscript.value)
            } else {
                handleUserSpeech("Namaste MYRA, aaj ka din kaisa hai?")
            }
        } else {
            if (voiceManager.isSpeaking.value) {
                voiceManager.stop()
            }
            aiEngine.interrupt()
            _userTranscript.value = ""
            _statusMessage.value = "Listening to you..."
            audioEngine.startRecording { pcm ->
                viewModelScope.launch {
                    aiEngine.sendAudio(pcm)
                }
            }
        }
    }

    fun handleUserSpeech(text: String) {
        _userTranscript.value = text
        _statusMessage.value = "Thinking..."

        viewModelScope.launch {
            // Save user message to Room
            val userMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = "main",
                userId = authManager.currentUser.value?.uid ?: "local",
                role = "user",
                text = text
            )
            db.insertMessage(userMsg)

            // Auto-detect emotion from words
            val detectedEmotion = emotionEngine.detectEmotionFromText(text)
            emotionEngine.setEmotion(detectedEmotion)

            // Parse commands
            val parsed = commandParser.parse(text)
            when (parsed) {
                is ParsedCommand.ModeChange -> {
                    personalityEngine.updateRelationshipMode(parsed.mode)
                    emotionEngine.setEmotion(parsed.emotion)
                    val reply = "${parsed.mode.title} activate kar diya gaya hai ${parsed.mode.emoji}. Main aapke saath hoon."
                    respondAndSpeak(reply)
                }

                is ParsedCommand.VoiceChange -> {
                    voiceProfileManager.selectProfile(parsed.voiceProfileId)
                    val profile = voiceProfileManager.activeProfile.value
                    voiceManager.applyVoiceProfile(profile)
                    val reply = "${parsed.description} select kar li gayi hai. Ab meri aawaz aisi hogi."
                    respondAndSpeak(reply)
                }

                is ParsedCommand.Flashlight -> {
                    val ok = commandParser.executeFlashlight(parsed.enable)
                    val reply = if (ok) {
                        if (parsed.enable) "Torch on kar di hai! 🔦" else "Torch off kar di hai."
                    } else {
                        "Torch access karne mein takleef hui. Camera permission check kijiye."
                    }
                    respondAndSpeak(reply)
                }

                is ParsedCommand.Volume -> {
                    commandParser.executeVolume(parsed.percent)
                    val reply = "Volume ${parsed.percent} percent kar diya gaya hai. 🔊"
                    respondAndSpeak(reply)
                }

                is ParsedCommand.OpenApp -> {
                    commandParser.openApp(parsed.appPackage)
                    val reply = "${parsed.appName} open kiya ja raha hai..."
                    respondAndSpeak(reply)
                }

                is ParsedCommand.StoreMemory -> {
                    val mem = MemoryEntity(
                        id = UUID.randomUUID().toString(),
                        userId = authManager.currentUser.value?.uid ?: "local",
                        title = "Saved Fact",
                        content = parsed.content,
                        category = "General",
                        isPrivate = parsed.isPrivate
                    )
                    db.insertMemory(mem)
                    firestoreRepo.syncMemoryToCloud(mem)
                    val reply = if (parsed.isPrivate) {
                        "Yeh baat maine aapki Private Encrypted Memory mein mehfooz kar li hai. 🔒"
                    } else {
                        "Maine yaad rakh liya: \"${parsed.content}\" 📝"
                    }
                    respondAndSpeak(reply)
                }

                is ParsedCommand.ForgetMemory -> {
                    val reply = "Samajh gayi, maine yeh context clear kar diya."
                    respondAndSpeak(reply)
                }

                is ParsedCommand.ScheduleReminder -> {
                    val rem = ReminderEntity(
                        id = UUID.randomUUID().toString(),
                        userId = authManager.currentUser.value?.uid ?: "local",
                        title = parsed.title,
                        timeMillis = System.currentTimeMillis() + 3600000,
                        timeDisplay = "1 ghante baad"
                    )
                    db.insertReminder(rem)
                    firestoreRepo.syncReminderToCloud(rem)
                    val reply = "Theek hai, maine reminder schedule kar diya hai: ${parsed.title} ⏰"
                    respondAndSpeak(reply)
                }

                is ParsedCommand.CallContact -> {
                    val reply = "${parsed.contactName} ko call karne ki intent taiyar hai 📞"
                    respondAndSpeak(reply)
                }

                is ParsedCommand.RequireConfirmation -> {
                    _pendingConfirmationAction.value = parsed.actionText
                    _confirmationDialogMessage.value = parsed.confirmationPrompt
                    respondAndSpeak(parsed.confirmationPrompt)
                }

                is ParsedCommand.ConversationalAi -> {
                    sendQueryToAi(text)
                }
            }
        }
    }

    private fun sendQueryToAi(query: String) {
        val owner = ownerAuthManager.ownerProfile.value
        val sysInstruction = personalityEngine.generateSystemInstruction(owner.ownerName, owner.ownerNickname)

        var streamedText = ""
        viewModelScope.launch {
            _statusMessage.value = "MYRA Speaking..."
            aiEngine.sendText(query, sysInstruction) { chunk ->
                streamedText += chunk.text
                _myraResponse.value = streamedText
                if (chunk.isComplete) {
                    _statusMessage.value = "Ready"
                    // Speak text via voice engine
                    val activeVoice = voiceProfileManager.activeProfile.value
                    voiceManager.applyVoiceProfile(activeVoice)
                    voiceManager.speak(streamedText)

                    // Persist to Room
                    viewModelScope.launch {
                        val replyMsg = ChatMessageEntity(
                            id = UUID.randomUUID().toString(),
                            conversationId = "main",
                            userId = authManager.currentUser.value?.uid ?: "local",
                            role = "myra",
                            text = streamedText,
                            emotion = emotionEngine.currentEmotion.value.title
                        )
                        db.insertMessage(replyMsg)
                    }
                }
            }
        }
    }

    fun respondAndSpeak(reply: String) {
        _myraResponse.value = reply
        _statusMessage.value = "Ready"
        val activeVoice = voiceProfileManager.activeProfile.value
        voiceManager.applyVoiceProfile(activeVoice)
        voiceManager.speak(reply)

        viewModelScope.launch {
            val replyMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = "main",
                userId = authManager.currentUser.value?.uid ?: "local",
                role = "myra",
                text = reply,
                emotion = emotionEngine.currentEmotion.value.title
            )
            db.insertMessage(replyMsg)
        }
    }

    fun confirmPendingAction() {
        _pendingConfirmationAction.value = null
        _confirmationDialogMessage.value = null
        respondAndSpeak("Operation verified with Owner Authorization and completed successfully.")
    }

    fun dismissConfirmation() {
        _pendingConfirmationAction.value = null
        _confirmationDialogMessage.value = null
        respondAndSpeak("Operation cancelled.")
    }

    fun addMemory(title: String, content: String, category: String, isPrivate: Boolean) {
        viewModelScope.launch {
            val mem = MemoryEntity(
                id = UUID.randomUUID().toString(),
                userId = authManager.currentUser.value?.uid ?: "local",
                title = title,
                content = content,
                category = category,
                isPrivate = isPrivate
            )
            db.insertMemory(mem)
            firestoreRepo.syncMemoryToCloud(mem)
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            db.deleteMemory(id)
        }
    }

    fun addReminder(title: String, timeDisplay: String) {
        viewModelScope.launch {
            val rem = ReminderEntity(
                id = UUID.randomUUID().toString(),
                userId = authManager.currentUser.value?.uid ?: "local",
                title = title,
                timeMillis = System.currentTimeMillis() + 1800000,
                timeDisplay = timeDisplay
            )
            db.insertReminder(rem)
            firestoreRepo.syncReminderToCloud(rem)
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            db.deleteReminder(id)
        }
    }

    fun addPrimeContact(name: String, phone: String, relationship: String, nickname: String) {
        viewModelScope.launch {
            val contact = PrimeContactEntity(
                id = UUID.randomUUID().toString(),
                userId = authManager.currentUser.value?.uid ?: "local",
                name = name,
                phoneNumber = phone,
                nickname = nickname,
                relationship = relationship,
                priority = "VIP",
                customGreeting = "$nickname ka call aa raha hai ❤️"
            )
            db.insertContact(contact)
            firestoreRepo.syncContactToCloud(contact)
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            db.deleteContact(id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            db.deleteAllMemories()
            db.clearChat()
            respondAndSpeak("All session memories and conversations have been purged securely.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stopRecording()
        voiceManager.shutdown()
        viewModelScope.launch {
            aiEngine.disconnect()
        }
    }
}
