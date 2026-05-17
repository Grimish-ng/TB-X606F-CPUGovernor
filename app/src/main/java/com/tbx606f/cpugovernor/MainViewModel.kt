package com.tbx606f.cpugovernor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _selectedProfile = MutableStateFlow<PowerProfile?>(null)
    val selectedProfile: StateFlow<PowerProfile?> = _selectedProfile

    private val _isRooted = MutableStateFlow(false)
    val isRooted: StateFlow<Boolean> = _isRooted

    init {
        checkRoot()
    }

    private fun checkRoot() {
        viewModelScope.launch {
            _isRooted.value = try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", "echo test")).waitFor() == 0
            } catch (e: Exception) { false }
        }
    }

    fun applyProfile(profile: PowerProfile) {
        viewModelScope.launch {
            val success = CpuGovernorManager.applyProfile(profile)
            if (success) {
                _selectedProfile.value = profile
                ProfilePrefs.saveProfile(profile)
            }
        }
    }

    fun toggleThermal(enabled: Boolean) {
        if (enabled) {
            CpuGovernorManager.disableThermalThrottling()
        } else {
            CpuGovernorManager.enableThermalThrottling()
        }
    }
}
