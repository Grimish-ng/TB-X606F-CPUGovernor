package com.tbx606f.cpugovernor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Fires on BOOT_COMPLETED and reapplies the last saved CPU governor profile.
 * Requires the device to be rooted and the app to have been granted root
 * at least once before rebooting.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        ProfilePrefs.init(context)
        val profile = ProfilePrefs.loadActiveProfile() ?: run {
            Log.d("BootReceiver", "No saved profile, skipping governor restore.")
            return
        }

        Log.d("BootReceiver", "Restoring profile: ${profile.displayName} (${profile.governor})")

        val success = CpuGovernorManager.applyProfile(profile.governor, profile.maxFreqKhz)
        if (success) {
            Log.d("BootReceiver", "Governor restored successfully.")
        } else {
            Log.e("BootReceiver", "Failed to restore governor on boot.")
        }
    }
}
