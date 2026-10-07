package com.example.core.voice

data class VoiceProfile(
    val id: String,
    val name: String,
    val category: String, // FEMALE, MALE, CUSTOM, OWNER
    val voiceProvider: String,
    val voiceId: String,
    val language: String = "hi",
    val gender: String = "Female",
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val emotionStyle: String = "Natural",
    val personality: String = "Caring Companion",
    val isOwnerVoice: Boolean = false,
    val isDefault: Boolean = false
)

object DefaultVoiceProfiles {
    val profiles = listOf(
        // Female Profiles
        VoiceProfile(
            id = "fem_cute",
            name = "🥰 Cute Girl",
            category = "FEMALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-cute",
            gender = "Female",
            pitch = 1.35f,
            speed = 1.05f,
            emotionStyle = "Playful",
            isDefault = true
        ),
        VoiceProfile(
            id = "fem_soft",
            name = "🌸 Soft Girl",
            category = "FEMALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-soft",
            gender = "Female",
            pitch = 1.20f,
            speed = 0.92f,
            emotionStyle = "Gentle"
        ),
        VoiceProfile(
            id = "fem_sweet",
            name = "🍬 Sweet Girl",
            category = "FEMALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-sweet",
            gender = "Female",
            pitch = 1.15f,
            speed = 1.0f,
            emotionStyle = "Sweet"
        ),
        VoiceProfile(
            id = "fem_romantic",
            name = "❤️ Romantic Female",
            category = "FEMALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-romantic",
            gender = "Female",
            pitch = 1.10f,
            speed = 0.90f,
            emotionStyle = "Affectionate"
        ),
        VoiceProfile(
            id = "fem_calm",
            name = "🍃 Calm Girl",
            category = "FEMALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-calm",
            gender = "Female",
            pitch = 1.05f,
            speed = 0.88f,
            emotionStyle = "Serene"
        ),
        VoiceProfile(
            id = "fem_prof",
            name = "💼 Professional Female",
            category = "FEMALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-prof",
            gender = "Female",
            pitch = 1.0f,
            speed = 1.05f,
            emotionStyle = "Polite"
        ),
        // Male Profiles
        VoiceProfile(
            id = "male_deep",
            name = "🎙️ Deep Male",
            category = "MALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-deep",
            gender = "Male",
            pitch = 0.75f,
            speed = 0.90f,
            emotionStyle = "Calm"
        ),
        VoiceProfile(
            id = "male_friendly",
            name = "🤝 Friendly Male",
            category = "MALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-friend",
            gender = "Male",
            pitch = 0.95f,
            speed = 1.0f,
            emotionStyle = "Warm"
        ),
        VoiceProfile(
            id = "male_prof",
            name = "💼 Professional Male",
            category = "MALE",
            voiceProvider = "AndroidTTS",
            voiceId = "hi-in-x-mprof",
            gender = "Male",
            pitch = 0.88f,
            speed = 1.02f,
            emotionStyle = "Formal"
        ),
        // Owner Voice Profile
        VoiceProfile(
            id = "owner_voice_1",
            name = "👑 MY OWN VOICE (Profile 1)",
            category = "OWNER",
            voiceProvider = "OwnerEnrolled",
            voiceId = "owner-voice-profile-1",
            gender = "Custom",
            pitch = 1.0f,
            speed = 1.0f,
            isOwnerVoice = true,
            emotionStyle = "Personal"
        )
    )
}
