package com.tbx606f.cpugovernor

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists the active profile ID so the BootReceiver can restore it after reboot.
 */
object ProfilePrefs {

    private const val PREFS_NAME = "cpu_governor_prefs"
    private const val KEY_ACTIVE_PROFILE = "active_profile_id"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveActiveProfile(profileId: String) {
        prefs.edit().putString(KEY_ACTIVE_PROFILE, profileId).apply()
    }

    fun loadActiveProfile(): PowerProfile? {
        val id = prefs.getString(KEY_ACTIVE_PROFILE, null) ?: return null
        return PowerProfiles.findById(id)
    }
}
