package com.example.core.owner

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

enum class PermissionLevel(val level: Int, val title: String) {
    PUBLIC(0, "Level 0: Public (General Conversation)"),
    USER(1, "Level 1: Standard User (Basic App Controls)"),
    TRUSTED(2, "Level 2: Trusted (Personal Memory & Contacts)"),
    OWNER(3, "Level 3: Owner (Settings, Voice Studio & Private Memory)"),
    OWNER_CONFIRMATION(4, "Level 4: Owner Confirmation (Data Purge & Security Reset)")
}

data class OwnerProfile(
    val ownerName: String = "Rahul",
    val ownerNickname: String = "Babu",
    val ownerPhone: String = "+91 9876543210",
    val ownerEmail: String = "owner@myra.ai",
    val pinHash: String = hashPin("1234"),
    val securityPhrase: String = "MYRA FOREVER",
    val enrolledVoiceProfiles: Int = 2
) {
    companion object {
        fun hashPin(pin: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}

class OwnerAuthenticationManager {
    private val _ownerProfile = MutableStateFlow(OwnerProfile())
    val ownerProfile: StateFlow<OwnerProfile> = _ownerProfile.asStateFlow()

    private val _isOwnerUnlocked = MutableStateFlow(true) // Unlocked for owner convenience once signed in
    val isOwnerUnlocked: StateFlow<Boolean> = _isOwnerUnlocked.asStateFlow()

    fun verifyPin(inputPin: String): Boolean {
        val hashed = OwnerProfile.hashPin(inputPin)
        val success = hashed == _ownerProfile.value.pinHash
        if (success) {
            _isOwnerUnlocked.value = true
        }
        return success
    }

    fun verifySecurityPhrase(phrase: String): Boolean {
        return phrase.trim().equals(_ownerProfile.value.securityPhrase.trim(), ignoreCase = true)
    }

    fun lockOwnerMode() {
        _isOwnerUnlocked.value = false
    }

    fun updateOwnerProfile(
        name: String,
        nickname: String,
        phone: String,
        email: String,
        newPin: String?,
        securityPhrase: String
    ) {
        val current = _ownerProfile.value
        val newHash = if (!newPin.isNullOrBlank()) OwnerProfile.hashPin(newPin) else current.pinHash
        _ownerProfile.value = current.copy(
            ownerName = name,
            ownerNickname = nickname,
            ownerPhone = phone,
            ownerEmail = email,
            pinHash = newHash,
            securityPhrase = securityPhrase
        )
    }

    fun canExecuteAction(requiredLevel: PermissionLevel): Boolean {
        return when (requiredLevel) {
            PermissionLevel.PUBLIC, PermissionLevel.USER -> true
            PermissionLevel.TRUSTED, PermissionLevel.OWNER, PermissionLevel.OWNER_CONFIRMATION -> _isOwnerUnlocked.value
        }
    }
}
