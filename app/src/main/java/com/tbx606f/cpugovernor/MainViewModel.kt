package com.tbx606f.cpugovernor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    sealed class RootState {
        object Checking : RootState()
        object Granted : RootState()
        object Denied : RootState()
    }

    sealed class ApplyState {
        object Idle : ApplyState()
        object Applying : ApplyState()
        data class Success(val profile: PowerProfile) : ApplyState()
        data class Error(val message: String) : ApplyState()
    }

    private val _rootState = MutableLiveData<RootState>(RootState.Checking)
    val rootState: LiveData<RootState> = _rootState

    private val _selectedProfile = MutableLiveData<PowerProfile>(PowerProfiles.BALANCED)
    val selectedProfile: LiveData<PowerProfile> = _selectedProfile

    private val _activeProfile = MutableLiveData<PowerProfile?>(null)
    val activeProfile: LiveData<PowerProfile?> = _activeProfile

    private val _coreInfos = MutableLiveData<List<CpuGovernorManager.CoreInfo>>(emptyList())
    val coreInfos: LiveData<List<CpuGovernorManager.CoreInfo>> = _coreInfos

    private val _cpuTemp = MutableLiveData<Int>(-1)
    val cpuTemp: LiveData<Int> = _cpuTemp

    private val _applyState = MutableLiveData<ApplyState>(ApplyState.Idle)
    val applyState: LiveData<ApplyState> = _applyState

    private val _availableGovernors = MutableLiveData<List<String>>(emptyList())
    val availableGovernors: LiveData<List<String>> = _availableGovernors

    private var pollingJob: Job? = null

    init {
        checkRoot()
    }

    fun checkRoot() {
        _rootState.value = RootState.Checking
        viewModelScope.launch(Dispatchers.IO) {
            val hasRoot = CpuGovernorManager.checkRootAccess()
            _rootState.postValue(if (hasRoot) RootState.Granted else RootState.Denied)
            if (hasRoot) {
                loadInitialState()
                startPolling()
            }
        }
    }

    private fun loadInitialState() {
        val governors = CpuGovernorManager.readAvailableGovernors()
        _availableGovernors.postValue(governors)

        val cores = CpuGovernorManager.readAllCores()
        _coreInfos.postValue(cores)

        // Detect currently active profile by matching governor of core 0
        val currentGov = cores.firstOrNull()?.governor
        val matchedProfile = PowerProfiles.ALL.find { it.governor == currentGov }
        _activeProfile.postValue(matchedProfile)
        if (matchedProfile != null) {
            _selectedProfile.postValue(matchedProfile)
        }
    }

    fun selectProfile(profile: PowerProfile) {
        _selectedProfile.value = profile
    }

    fun applySelectedProfile() {
        val profile = _selectedProfile.value ?: return
        _applyState.value = ApplyState.Applying

        viewModelScope.launch(Dispatchers.IO) {
            val success = CpuGovernorManager.applyProfile(profile.governor, profile.maxFreqKhz)
            if (success) {
                _activeProfile.postValue(profile)
                _applyState.postValue(ApplyState.Success(profile))
                // Save to prefs for BootReceiver
                ProfilePrefs.saveActiveProfile(profile.id)
            } else {
                _applyState.postValue(ApplyState.Error("Failed to write to CPU sysfs. Is root access granted?"))
            }
            delay(2000)
            _applyState.postValue(ApplyState.Idle)
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val cores = CpuGovernorManager.readAllCores()
                _coreInfos.postValue(cores)
                val temp = CpuGovernorManager.readCpuTemperature()
                _cpuTemp.postValue(temp)
                delay(2000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
