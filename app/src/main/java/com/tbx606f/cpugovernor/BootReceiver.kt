package com.tbx606f.cpugovernor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val lastProfile = ProfilePrefs.getLastProfile()
            lastProfile?.let {
                CpuGovernorManager.applyProfile(it)
            }
        }
    }
}
