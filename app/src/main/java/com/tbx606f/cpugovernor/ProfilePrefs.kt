package com.tbx606f.cpugovernor

import android.content.Context
import android.content.SharedPreferences

object ProfilePrefs {

    private const val PREFS_NAME = "cpu_profiles"
    private const val KEY_PROFILE_NAME = "last_profile"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveProfile(profile: PowerProfile) {
        // You can expand this later to save full profile
    }

    fun getLastProfile(): PowerProfile? {
        // For now return Balanced as default
        return Profiles.BALANCED
    }
}
