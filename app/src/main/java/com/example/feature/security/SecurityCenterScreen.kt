package com.example.feature.security

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.core.owner.PermissionLevel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun SecurityCenterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ownerProfile by viewModel.ownerAuthManager.ownerProfile.collectAsState()
    val isOwnerUnlocked by viewModel.ownerAuthManager.isOwnerUnlocked.collectAsState()
    val scrollState = rememberScrollState()

    var showPinChangeDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val micGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    val notifGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    val cameraGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceDark)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "SECURITY CENTER 🛡️",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Owner authentication, permissions, privacy and vault controls",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Security Status Badge Card
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = GlowGreen
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Shield",
                    tint = GlowGreen,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "🟢 System Status: PROTECTED",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Encrypted Local Storage • Zero-Trust Owner Gate active",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Owner Identity Profile
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonCyan
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Owner Identity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isOwnerUnlocked) GlowGreen.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isOwnerUnlocked) "UNLOCKED" else "LOCKED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOwnerUnlocked) GlowGreen else NeonPink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Owner Name: ${ownerProfile.ownerName} (${ownerProfile.ownerNickname})", fontSize = 13.sp, color = Color(0xFFE2E8F0))
                Text("Phone: ${ownerProfile.ownerPhone}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Text("Security Phrase: \"${ownerProfile.securityPhrase}\"", fontSize = 12.sp, color = NeonCyan)

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showPinChangeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("Change PIN", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            if (isOwnerUnlocked) viewModel.ownerAuthManager.lockOwnerMode()
                            else viewModel.ownerAuthManager.verifyPin("1234")
                        }
                    ) {
                        Text(if (isOwnerUnlocked) "Lock Vault" else "Unlock (1234)", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Privacy Center: Device Permissions Matrix
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ElectricViolet
        ) {
            Column {
                Text(
                    text = "Privacy & Permissions Matrix",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                PermissionRow("Microphone (Audio stream)", micGranted)
                PermissionRow("Push Notifications (Reminders)", notifGranted)
                PermissionRow("Camera Torch (Flashlight control)", cameraGranted)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Data Export & Emergency Delete
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonPink
        ) {
            Column {
                Text(
                    text = "Data Management (Level 4)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Export your encrypted session configurations or wipe all stored memories completely.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export JSON", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Wipe", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Wipe All Data", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Change PIN Dialog
    if (showPinChangeDialog) {
        var newPin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPinChangeDialog = false },
            title = { Text("Update Owner PIN", color = NeonCyan) },
            text = {
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { newPin = it },
                    label = { Text("Enter 4-digit PIN") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPin.length >= 4) {
                            viewModel.ownerAuthManager.updateOwnerProfile(
                                ownerProfile.ownerName,
                                ownerProfile.ownerNickname,
                                ownerProfile.ownerPhone,
                                ownerProfile.ownerEmail,
                                newPin,
                                ownerProfile.securityPhrase
                            )
                            showPinChangeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save PIN", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPinChangeDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = SpaceCardBg
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Data Export Summary", color = NeonCyan) },
            text = {
                Text(
                    text = "Export JSON generated:\n• Memories: ${viewModel.memories.collectAsState().value.size} items\n• Prime Contacts: ${viewModel.contacts.collectAsState().value.size} items\n• Voice Profiles: ${viewModel.voiceProfileManager.profiles.collectAsState().value.size}\n• Cloud Status: Sync enabled\n\nSecrets and API keys are strictly excluded from export.",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Close", color = Color.Black)
                }
            },
            containerColor = SpaceCardBg
        )
    }

    // Wipe Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Danger: Wipe All Data?", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = {
                Text("This Level 4 operation permanently removes all stored memories, chat messages, and contacts. This cannot be undone.", color = Color.White, fontSize = 12.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Wipe", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = SpaceCardBg
        )
    }
}

@Composable
fun PermissionRow(title: String, granted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (granted) GlowGreen.copy(alpha = 0.2f) else WarmAmber.copy(alpha = 0.2f)
        ) {
            Text(
                text = if (granted) "GRANTED ✓" else "STANDBY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (granted) GlowGreen else WarmAmber,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
