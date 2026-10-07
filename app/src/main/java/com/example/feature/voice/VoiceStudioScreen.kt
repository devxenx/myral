package com.example.feature.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.voice.VoiceProfile
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun VoiceStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.voiceProfileManager.profiles.collectAsState()
    val activeProfile by viewModel.voiceProfileManager.activeProfile.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "FEMALE", "MALE", "OWNER")

    val filteredProfiles = remember(profiles, selectedCategory) {
        if (selectedCategory == "ALL") profiles
        else profiles.filter { it.category == selectedCategory }
    }

    var pitchSlider by remember(activeProfile) { mutableStateOf(activeProfile.pitch) }
    var speedSlider by remember(activeProfile) { mutableStateOf(activeProfile.speed) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceDark)
            .padding(16.dp)
    ) {
        Text(
            text = "VOICE STUDIO 🎙️",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Select & customize MYRA's spoken voice and owner voiceprint",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else GlassSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else GlassBorder
                    ),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonCyan else Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fine Tuning Card for Active Profile
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonPink
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active: ${activeProfile.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = {
                            viewModel.voiceManager.applyVoiceProfile(activeProfile)
                            viewModel.voiceManager.speak("Namaste Rahul! Yeh meri preview voice hai.")
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test preview",
                            tint = NeonPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Pitch: ${"%.2f".format(pitchSlider)}x",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = pitchSlider,
                    onValueChange = {
                        pitchSlider = it
                        val updated = activeProfile.copy(pitch = it)
                        viewModel.voiceProfileManager.updateProfile(updated)
                        viewModel.voiceManager.applyVoiceProfile(updated)
                    },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink)
                )

                Text(
                    text = "Speed: ${"%.2f".format(speedSlider)}x",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = speedSlider,
                    onValueChange = {
                        speedSlider = it
                        val updated = activeProfile.copy(speed = it)
                        viewModel.voiceProfileManager.updateProfile(updated)
                        viewModel.voiceManager.applyVoiceProfile(updated)
                    },
                    valueRange = 0.6f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Profiles List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredProfiles) { profile ->
                val isActive = profile.id == activeProfile.id
                VoiceProfileItemCard(
                    profile = profile,
                    isActive = isActive,
                    onSelect = {
                        viewModel.voiceProfileManager.selectProfile(profile.id)
                        viewModel.voiceManager.applyVoiceProfile(profile)
                        viewModel.voiceManager.speak("Aapki MYRA ab is voice mein baat karegi.")
                    },
                    onPreview = {
                        viewModel.voiceManager.applyVoiceProfile(profile)
                        viewModel.voiceManager.speak("Aapka swagat hai, main aapki MYRA hoon.")
                    }
                )
            }
        }
    }
}

@Composable
fun VoiceProfileItemCard(
    profile: VoiceProfile,
    isActive: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) ElectricViolet.copy(alpha = 0.25f) else SpaceCardBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) NeonCyan else GlassBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_card_${profile.id}")
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeonCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Gender: ${profile.gender} • Pitch: ${profile.pitch} • Speed: ${profile.speed}",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            IconButton(
                onClick = onPreview,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Preview Voice",
                    tint = NeonCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
