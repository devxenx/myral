package com.example.feature.personality

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.personality.RelationshipMode
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun PersonalityStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentConfig by viewModel.personalityEngine.config.collectAsState()
    val scrollState = rememberScrollState()

    var customPromptText by remember(currentConfig.customSystemPrompt) {
        mutableStateOf(currentConfig.customSystemPrompt)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceDark)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "PERSONALITY STUDIO ✨",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Fine-tune MYRA's relationship, tone, language, and behavior",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Relationship Modes
        Text(
            text = "RELATIONSHIP MODE",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RelationshipMode.values().forEach { mode ->
                val isSelected = currentConfig.relationshipMode == mode
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ElectricViolet.copy(alpha = 0.28f) else SpaceCardBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonPink else GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.personalityEngine.updateRelationshipMode(mode)
                            viewModel.respondAndSpeak("${mode.title} set kar diya gaya hai. Main hamesha aapke kareeb hoon.")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(text = mode.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = mode.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonCyan else Color.White
                            )
                            Text(
                                text = mode.promptDescriptor,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Parameters Card
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonCyan
        ) {
            Column {
                Text(
                    text = "Conversational Attributes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Speaking Style", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Cute", "Caring", "Romantic", "Friendly").forEach { style ->
                        val isSel = currentConfig.speakingStyle.contains(style)
                        OutlinedButton(
                            onClick = {
                                viewModel.personalityEngine.updateConfig(
                                    currentConfig.copy(speakingStyle = style)
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) NeonCyan.copy(alpha = 0.2f) else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = style, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Language Mode", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Hinglish", "Hindi", "English").forEach { lang ->
                        val isSel = currentConfig.language == lang
                        OutlinedButton(
                            onClick = {
                                viewModel.personalityEngine.updateConfig(
                                    currentConfig.copy(language = lang)
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) NeonPink.copy(alpha = 0.2f) else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = lang, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Response Length", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Short", "Medium", "Detailed").forEach { len ->
                        val isSel = currentConfig.responseLength == len
                        OutlinedButton(
                            onClick = {
                                viewModel.personalityEngine.updateConfig(
                                    currentConfig.copy(responseLength = len)
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) RadiantPurple.copy(alpha = 0.2f) else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = len, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Custom Prompt Section
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = RadiantPurple
        ) {
            Column {
                Text(
                    text = "Custom Personality Directives",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add things MYRA should always know or special nicknames to use:",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customPromptText,
                    onValueChange = { customPromptText = it },
                    placeholder = { Text("E.g. Call me 'Babu' when in private, remind me to drink water...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder
                    ),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.personalityEngine.updateConfig(
                            currentConfig.copy(customSystemPrompt = customPromptText)
                        )
                        viewModel.respondAndSpeak("Custom personality prompt successfully saved and updated.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Directives", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
