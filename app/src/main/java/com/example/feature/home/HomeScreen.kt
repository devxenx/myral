package com.example.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.emotion.MyraEmotion
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToVoiceStudio: () -> Unit,
    onNavigateToEmotionStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val myraResponse by viewModel.myraResponse.collectAsState()
    val userTranscript by viewModel.userTranscript.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isRecording by viewModel.audioEngine.isRecording.collectAsState()
    val amplitude by viewModel.audioEngine.amplitude.collectAsState()
    val currentEmotion by viewModel.emotionEngine.currentEmotion.collectAsState()
    val activeVoice by viewModel.voiceProfileManager.activeProfile.collectAsState()
    val personalityConfig by viewModel.personalityEngine.config.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val pendingConfirmation by viewModel.pendingConfirmationAction.collectAsState()
    val confirmationPrompt by viewModel.confirmationDialogMessage.collectAsState()

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Status Bar: Emotion & Relationship mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Relationship Mode Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GlassSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.testTag("mode_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = personalityConfig.relationshipMode.emoji,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = personalityConfig.relationshipMode.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                    }
                }

                // AI Engine Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isRecording -> NeonPink.copy(alpha = 0.2f)
                        isSpeaking -> NeonCyan.copy(alpha = 0.2f)
                        else -> GlowGreen.copy(alpha = 0.2f)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isRecording) NeonPink else NeonCyan
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) NeonPink else GlowGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusMessage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Glowing AI Orb
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable { viewModel.onMicToggled() },
                contentAlignment = Alignment.Center
            ) {
                OrbAnimationView(
                    size = 210.dp,
                    emotion = currentEmotion,
                    amplitude = amplitude,
                    isListening = isRecording,
                    isSpeaking = isSpeaking
                )
            }

            // Voice & Emotion Quick Select Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GlassSurface,
                    modifier = Modifier
                        .clickable { onNavigateToVoiceStudio() }
                        .testTag("current_voice_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice",
                            tint = NeonPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = activeVoice.name,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GlassSurface,
                    modifier = Modifier
                        .clickable { onNavigateToEmotionStudio() }
                        .testTag("current_emotion_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = currentEmotion.emoji, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentEmotion.title,
                            fontSize = 11.sp,
                            color = currentEmotion.orbColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Transcript Bubble (if user spoke)
            if (userTranscript.isNotBlank()) {
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    borderColor = NeonPink.copy(alpha = 0.5f),
                    testTag = "user_transcript_card"
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = userTranscript,
                            fontSize = 13.sp,
                            color = Color(0xFFF1F5F9),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // MYRA Live Speech Card
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = currentEmotion.orbColor,
                testTag = "myra_response_card"
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MYRA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentEmotion.emoji,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.respondAndSpeak(myraResponse) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.PlayArrow,
                                contentDescription = "Play voice",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = myraResponse,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Voice Action Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickCommandChip(
                    text = "❤️ Love Mode",
                    onClick = { viewModel.handleUserSpeech("MYRA love mode on karo") }
                )
                QuickCommandChip(
                    text = "🥰 Cute Mode",
                    onClick = { viewModel.handleUserSpeech("MYRA cute mode on karo") }
                )
                QuickCommandChip(
                    text = "💕 Care Mode",
                    onClick = { viewModel.handleUserSpeech("MYRA care mode") }
                )
                QuickCommandChip(
                    text = "🔦 Torch",
                    onClick = { viewModel.handleUserSpeech("MYRA torch on") }
                )
                QuickCommandChip(
                    text = "⏰ Reminder",
                    onClick = { viewModel.handleUserSpeech("MYRA kal subah ka reminder lagao") }
                )
                QuickCommandChip(
                    text = "▶️ YouTube",
                    onClick = { viewModel.handleUserSpeech("MYRA YouTube open karo") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Mic FAB Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (isRecording) listOf(NeonPink, CyberMagenta)
                            else listOf(NeonCyan, ElectricViolet)
                        )
                    )
                    .border(
                        2.5.dp,
                        Color.White.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .clickable { viewModel.onMicToggled() }
                    .testTag("mic_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Listening" else "Start Listening",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isRecording) "Tap to send speech" else "Tap orb or mic to speak to MYRA",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Owner Confirmation Dialog for Sensitive Actions
        if (pendingConfirmation != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissConfirmation() },
                title = {
                    Text(
                        text = "Owner Confirmation Required 🛡️",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink
                    )
                },
                text = {
                    Text(
                        text = confirmationPrompt ?: "Sensitive command requested. Proceed?",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmPendingAction() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("Authorize", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.dismissConfirmation() }
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = SpaceCardBg
            )
        }
    }
}

@Composable
fun QuickCommandChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GlassSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder.copy(alpha = 0.5f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}
