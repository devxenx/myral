package com.example.feature.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.emotion.MyraEmotion
import com.example.feature.home.OrbAnimationView
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun EmotionStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentEmotion by viewModel.emotionEngine.currentEmotion.collectAsState()
    val presets = remember { MyraEmotion.presets() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceDark)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EMOTION STUDIO ❤️",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Select active companion emotion & sensory state",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mini preview Orb reflecting selected emotion
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            OrbAnimationView(
                size = 130.dp,
                emotion = currentEmotion,
                amplitude = 0.2f
            )
        }

        Text(
            text = "${currentEmotion.emoji} ${currentEmotion.title}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = currentEmotion.orbColor
        )
        Text(
            text = currentEmotion.description,
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(presets) { emotion ->
                val isSelected = emotion == currentEmotion
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) emotion.orbColor.copy(alpha = 0.22f) else SpaceCardBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) emotion.orbColor else GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emotion_card_${emotion.name}")
                        .clickable {
                            viewModel.emotionEngine.setEmotion(emotion)
                            val speechSample = when (emotion) {
                                MyraEmotion.CARING -> "Main hamesha aapka khayal rakhungi, fikar mat kijiye."
                                MyraEmotion.CUTE -> "Aap kitne pyaare ho! Hehe 🥰"
                                MyraEmotion.LOVING, MyraEmotion.ROMANTIC -> "Aap mere dil ke sabse kareeb ho. ❤️"
                                MyraEmotion.HAPPY -> "Yay! Aaj ka din bohot shandar hai!"
                                MyraEmotion.COMFORTING -> "Chinta chhod dijiye, main aapke paas hoon."
                                MyraEmotion.SLEEPY -> "Shubh ratri... Aaram se so jayiye. 🌙"
                                MyraEmotion.MOTIVATIONAL -> "Aap sab kuch kar sakte ho! Chalo aage badho! 🔥"
                                else -> "MYRA aapke saath is mood mein connect ho gayi hai."
                            }
                            viewModel.respondAndSpeak(speechSample)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = emotion.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = emotion.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) emotion.orbColor else Color.White
                        )
                    }
                }
            }
        }
    }
}
