package com.example.core.voice

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceProfileManager {
    private val _profiles = MutableStateFlow<List<VoiceProfile>>(DefaultVoiceProfiles.profiles)
    val profiles: StateFlow<List<VoiceProfile>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<VoiceProfile>(
        DefaultVoiceProfiles.profiles.firstOrNull { it.isDefault } ?: DefaultVoiceProfiles.profiles.first()
    )
    val activeProfile: StateFlow<VoiceProfile> = _activeProfile.asStateFlow()

    fun selectProfile(id: String) {
        val found = _profiles.value.find { it.id == id }
        if (found != null) {
            _activeProfile.value = found
        }
    }

    fun addCustomProfile(profile: VoiceProfile) {
        _profiles.value = _profiles.value + profile
        _activeProfile.value = profile
    }

    fun updateProfile(profile: VoiceProfile) {
        _profiles.value = _profiles.value.map { if (it.id == profile.id) profile else it }
        if (_activeProfile.value.id == profile.id) {
            _activeProfile.value = profile
        }
    }

    fun deleteProfile(id: String) {
        if (_profiles.value.size <= 1) return
        _profiles.value = _profiles.value.filterNot { it.id == id }
        if (_activeProfile.value.id == id) {
            _activeProfile.value = _profiles.value.first()
        }
    }
}
