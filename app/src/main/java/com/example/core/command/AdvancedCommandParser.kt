package com.example.core.command

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import com.example.core.emotion.MyraEmotion
import com.example.core.personality.RelationshipMode

sealed interface ParsedCommand {
    data class ModeChange(val mode: RelationshipMode, val emotion: MyraEmotion) : ParsedCommand
    data class VoiceChange(val voiceProfileId: String, val description: String) : ParsedCommand
    data class Flashlight(val enable: Boolean) : ParsedCommand
    data class Volume(val percent: Int) : ParsedCommand
    data class OpenApp(val appPackage: String, val appName: String) : ParsedCommand
    data class StoreMemory(val content: String, val isPrivate: Boolean) : ParsedCommand
    data class ForgetMemory(val query: String) : ParsedCommand
    data class ScheduleReminder(val title: String, val timeHint: String) : ParsedCommand
    data class CallContact(val contactName: String) : ParsedCommand
    data class RequireConfirmation(val actionText: String, val confirmationPrompt: String) : ParsedCommand
    data class ConversationalAi(val query: String) : ParsedCommand
}

class AdvancedCommandParser(private val context: Context) {
    private val tag = "AdvancedCommandParser"

    fun parse(rawInput: String): ParsedCommand {
        val input = rawInput.trim()
        val lower = input.lowercase()

        // 1. Safety Financial Confirmation Check
        if (lower.contains("payment") || lower.contains("rupaye") || lower.contains("paise bhej") || lower.contains("₹") || lower.contains("transfer")) {
            return ParsedCommand.RequireConfirmation(
                actionText = input,
                confirmationPrompt = "Financial action detected: Payment transaction ke liye Owner PIN confirmation zaroori hai. Kya aap confirm karte hain?"
            )
        }

        // 2. Mode Switches
        if (lower.contains("love mode") || lower.contains("mohabbat mode") || lower.contains("pyaar mode")) {
            return ParsedCommand.ModeChange(RelationshipMode.LOVE, MyraEmotion.LOVING)
        }
        if (lower.contains("cute mode")) {
            return ParsedCommand.ModeChange(RelationshipMode.CUTE, MyraEmotion.CUTE)
        }
        if (lower.contains("care mode") || lower.contains("caring mode")) {
            return ParsedCommand.ModeChange(RelationshipMode.CARING, MyraEmotion.CARING)
        }
        if (lower.contains("professional mode") || lower.contains("work mode")) {
            return ParsedCommand.ModeChange(RelationshipMode.PROFESSIONAL, MyraEmotion.CALM)
        }
        if (lower.contains("friend mode") || lower.contains("dost mode")) {
            return ParsedCommand.ModeChange(RelationshipMode.FRIEND, MyraEmotion.HAPPY)
        }

        // 3. Voice Switch
        if (lower.contains("meri voice") || lower.contains("owner voice")) {
            return ParsedCommand.VoiceChange("owner_voice_1", "Owner Voice Profile 1")
        }
        if (lower.contains("girl voice") || lower.contains("cute voice") || lower.contains("ladki ki voice")) {
            return ParsedCommand.VoiceChange("fem_cute", "Cute Girl Voice")
        }
        if (lower.contains("soft voice") || lower.contains("soft girl")) {
            return ParsedCommand.VoiceChange("fem_soft", "Soft Girl Voice")
        }
        if (lower.contains("deep voice") || lower.contains("male voice")) {
            return ParsedCommand.VoiceChange("male_deep", "Deep Male Voice")
        }

        // 4. Flashlight / Torch
        if (lower.contains("torch on") || lower.contains("light on") || lower.contains("light jala") || lower.contains("flashlight on")) {
            return ParsedCommand.Flashlight(true)
        }
        if (lower.contains("torch off") || lower.contains("light off") || lower.contains("light band") || lower.contains("flashlight off")) {
            return ParsedCommand.Flashlight(false)
        }

        // 5. Volume control
        val volumeMatch = Regex("""volume\s+(\d+)\s*(?:percent|%|pratishat)?""").find(lower)
        if (volumeMatch != null) {
            val pct = volumeMatch.groupValues[1].toIntOrNull() ?: 50
            return ParsedCommand.Volume(pct.coerceIn(0, 100))
        }

        // 6. Apps Open
        if (lower.contains("youtube") && (lower.contains("kholo") || lower.contains("open") || lower.contains("chalao"))) {
            return ParsedCommand.OpenApp("com.google.android.youtube", "YouTube")
        }
        if (lower.contains("whatsapp") && (lower.contains("kholo") || lower.contains("open") || lower.contains("chalao") || lower.contains("message"))) {
            return ParsedCommand.OpenApp("com.whatsapp", "WhatsApp")
        }
        if (lower.contains("chrome") || lower.contains("browser")) {
            return ParsedCommand.OpenApp("com.android.chrome", "Chrome")
        }

        // 7. Call Prime Contact
        if (lower.contains("call") || lower.contains("phone milao") || lower.contains("phone lagao")) {
            val contactName = when {
                lower.contains("mummy") || lower.contains("mom") || lower.contains("maa") -> "Mom"
                lower.contains("papa") || lower.contains("dad") -> "Dad"
                lower.contains("jaan") -> "Jaan"
                lower.contains("bhai") || lower.contains("brother") -> "Brother"
                else -> "Contact"
            }
            return ParsedCommand.CallContact(contactName)
        }

        // 8. Memory Commands
        if (lower.contains("remember") || lower.contains("yaad rakh")) {
            val content = input.replace(Regex("(?i)^(?:myra\\s+)?(?:remember(?:\\s+that)?|yaad\\s+rakho(?:\\s+ki)?)\\s*"), "").trim()
            val isPrivate = lower.contains("private") || lower.contains("secret")
            return ParsedCommand.StoreMemory(content.ifBlank { input }, isPrivate)
        }
        if (lower.contains("forget") || lower.contains("bhul ja")) {
            val query = input.replace(Regex("(?i)^(?:myra\\s+)?(?:forget|bhul\\s+jao)\\s*"), "").trim()
            return ParsedCommand.ForgetMemory(query)
        }

        // 9. Reminder
        if (lower.contains("reminder") || lower.contains("yaad dila")) {
            return ParsedCommand.ScheduleReminder(title = input, timeHint = "Scheduled task")
        }

        // Default to natural conversational AI
        return ParsedCommand.ConversationalAi(input)
    }

    fun executeFlashlight(enable: Boolean): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
            cameraManager.setTorchMode(cameraId, enable)
            true
        } catch (e: Exception) {
            Log.w(tag, "Flashlight control unavailable or permission missing", e)
            false
        }
    }

    fun executeVolume(percent: Int): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val target = ((percent / 100f) * maxVol).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, AudioManager.FLAG_SHOW_UI)
            true
        } catch (e: Exception) {
            Log.w(tag, "Volume control error", e)
            false
        }
    }

    fun openApp(packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                // Fallback to Play Store if not installed
                val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(marketIntent)
                true
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to launch app $packageName", e)
            false
        }
    }
}
