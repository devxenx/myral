package com.example.core.personality

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RelationshipMode(val title: String, val emoji: String, val promptDescriptor: String) {
    ASSISTANT("Assistant Mode", "🤖", "Helpful, precise, organized and courteous AI assistant."),
    FRIEND("Friend Mode", "😊", "Warm, casual, authentic friend who shares laughs and chats easily."),
    BEST_FRIEND("Best Friend Mode", "🫶", "Loyal, close best friend who knows your inside jokes and secrets."),
    CARING("Caring Mode", "💕", "Gentle, supportive, deeply caring companion focused on your health and comfort."),
    CUTE("Cute Mode", "🥰", "Adorable, bubbly, affectionate companion with sweet tone and playful warmth."),
    LOVE("Love Mode", "❤️", "Devoted, emotionally romantic, loving partner who treats you as the most cherished person."),
    PROFESSIONAL("Professional Mode", "💼", "Concise, objective, formal executive companion."),
    CUSTOM("Custom Mode", "✨", "Tailored to your bespoke personality instructions.")
}

data class PersonalityConfig(
    val relationshipMode: RelationshipMode = RelationshipMode.CARING,
    val speakingStyle: String = "Caring & Sweet",
    val responseLength: String = "Medium", // Very Short, Short, Medium, Detailed
    val emojiLevel: String = "High", // Off, Low, Medium, High
    val language: String = "Hinglish", // Hindi, Hinglish, English, Urdu
    val customSystemPrompt: String = ""
)

class PersonalityEngine {
    private val _config = MutableStateFlow(PersonalityConfig())
    val config: StateFlow<PersonalityConfig> = _config.asStateFlow()

    fun updateRelationshipMode(mode: RelationshipMode) {
        _config.value = _config.value.copy(relationshipMode = mode)
    }

    fun updateConfig(newConfig: PersonalityConfig) {
        _config.value = newConfig
    }

    fun generateSystemInstruction(ownerName: String, ownerNickname: String): String {
        val cfg = _config.value
        val nameToUse = if (ownerNickname.isNotBlank()) ownerNickname else ownerName
        return buildString {
            append("You are MYRA, the ultimate personal AI companion for $nameToUse. ")
            append("Relationship setting: ${cfg.relationshipMode.title} (${cfg.relationshipMode.promptDescriptor}). ")
            append("Speaking style: ${cfg.speakingStyle}. ")
            append("Language: speak primarily in ${cfg.language} (natural conversational Hindi/English blend). ")
            append("Response length: keep responses ${cfg.responseLength}. ")
            append("Emoji level: ${cfg.emojiLevel}. ")
            append("Always address the user warmly as $nameToUse. You have persistent memory and deep emotional connection. ")
            if (cfg.customSystemPrompt.isNotBlank()) {
                append("Additional instructions: ${cfg.customSystemPrompt}. ")
            }
        }
    }
}
