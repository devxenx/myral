package com.example.core.emotion

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MyraEmotion(
    val title: String,
    val emoji: String,
    val description: String,
    val orbColor: Color,
    val pitchMultiplier: Float,
    val speedMultiplier: Float
) {
    NEUTRAL("Neutral", "✨", "Balanced and attentive", NeonCyan, 1.0f, 1.0f),
    HAPPY("Happy", "😊", "Energetic, cheerful, and positive", EmotionHappyColor, 1.15f, 1.05f),
    EXCITED("Excited", "🎉", "Thrilled, enthusiastic and lively", NeonPink, 1.25f, 1.12f),
    CARING("Caring", "💕", "Warm, gentle, and deeply supportive", EmotionCareColor, 1.05f, 0.95f),
    LOVING("Love", "❤️", "Affectionate, devoted, and warm", EmotionLoveColor, 1.1f, 0.92f),
    ROMANTIC("Mohabbat", "🥰", "Intimate, poetic, deeply fond", EmotionMohabbatColor, 1.08f, 0.90f),
    CUTE("Cute", "😘", "Playful, charming, and endearing", EmotionCuteColor, 1.30f, 1.05f),
    SAD("Sad", "😢", "Gentle, listening with empathy", EmotionComfortColor, 0.90f, 0.85f),
    COMFORTING("Comfort", "🫂", "Soft, reassuring, safe haven", EmotionComfortColor, 0.95f, 0.88f),
    WORRIED("Worried", "😟", "Concerned for your wellbeing", GentleRose, 1.05f, 1.02f),
    PROUD("Proud", "🌟", "Admiring, cheering your victory", WarmAmber, 1.15f, 1.08f),
    PLAYFUL("Playful", "😜", "Teasing, smiling, fun-loving", NeonPink, 1.22f, 1.08f),
    CALM("Cool", "😎", "Confident, serene, peaceful", EmotionCoolColor, 0.95f, 0.95f),
    ANGRY("Defensive", "⚡", "Protective and alert", Color(0xFFEF4444), 1.05f, 1.15f),
    APOLOGETIC("Apologetic", "🥺", "Humble and sincere", Color(0xFFA78BFA), 0.95f, 0.92f),
    MOTIVATIONAL("Energetic", "🔥", "Inspiring strength and fire", EmotionEnergeticColor, 1.20f, 1.15f),
    SLEEPY("Night", "🌙", "Quiet, soft whispers for restful night", EmotionNightColor, 0.85f, 0.80f),
    SURPRISED("Surprised", "😲", "Astonished and wide-eyed", NeonCyan, 1.25f, 1.1f);

    companion object {
        fun presets(): List<MyraEmotion> = listOf(
            CARING, CUTE, LOVING, ROMANTIC, HAPPY, COMFORTING, CALM, NEUTRAL, SLEEPY, MOTIVATIONAL
        )
    }
}

class EmotionEngine(private val context: Context) {
    private val _currentEmotion = MutableStateFlow(MyraEmotion.CARING)
    val currentEmotion: StateFlow<MyraEmotion> = _currentEmotion.asStateFlow()

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun setEmotion(emotion: MyraEmotion, triggerHaptic: Boolean = true) {
        _currentEmotion.value = emotion
        if (triggerHaptic) {
            performEmotionHaptic(emotion)
        }
    }

    fun detectEmotionFromText(text: String): MyraEmotion {
        val lower = text.lowercase()
        return when {
            lower.contains("bura") || lower.contains("sad") || lower.contains("udas") || 
            lower.contains("pareshan") || lower.contains("dard") || lower.contains("kharab tha") ||
            lower.contains("cry") || lower.contains("rona") -> MyraEmotion.COMFORTING

            lower.contains("love") || lower.contains("pyaar") || lower.contains("pyar") || 
            lower.contains("jaan") || lower.contains("dil") || lower.contains("mohabbat") -> MyraEmotion.LOVING

            lower.contains("cute") || lower.contains("sweet") || lower.contains("pyari") || 
            lower.contains("sundar") || lower.contains("pyaari") -> MyraEmotion.CUTE

            lower.contains("happy") || lower.contains("khush") || lower.contains("result") || 
            lower.contains("jeet") || lower.contains("win") || lower.contains("party") -> MyraEmotion.HAPPY

            lower.contains("sone") || lower.contains("sleep") || lower.contains("neend") || 
            lower.contains("goodnight") || lower.contains("shubh ratri") || lower.contains("raat") -> MyraEmotion.SLEEPY

            lower.contains("gym") || lower.contains("workout") || lower.contains("energy") || 
            lower.contains("taqat") || lower.contains("aag") || lower.contains("chalo") -> MyraEmotion.MOTIVATIONAL

            lower.contains("chill") || lower.contains("relax") || lower.contains("shaant") -> MyraEmotion.CALM

            else -> _currentEmotion.value
        }
    }

    private fun performEmotionHaptic(emotion: MyraEmotion) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (emotion) {
                    MyraEmotion.LOVING, MyraEmotion.ROMANTIC ->
                        VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 120), -1)
                    MyraEmotion.CUTE, MyraEmotion.HAPPY ->
                        VibrationEffect.createWaveform(longArrayOf(0, 50, 40, 50), -1)
                    MyraEmotion.MOTIVATIONAL, MyraEmotion.EXCITED ->
                        VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
                    else ->
                        VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(60)
            }
        } catch (_: Exception) {
            // Ignore if vibration permissions or hardware is restricted
        }
    }
}
