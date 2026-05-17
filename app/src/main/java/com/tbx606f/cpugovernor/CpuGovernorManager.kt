package com.tbx606f.cpugovernor

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object CpuGovernorManager {

    private const val TAG = "CpuGovernorManager"
    private val policies = listOf("policy0", "policy4")

    fun applyProfile(profile: PowerProfile): Boolean {
        return try {
            var success = true
            policies.forEach { policy ->
                val base = "/sys/devices/system/cpu/cpufreq/$policy"
                success = success &&
                    execute("echo ${profile.governor} > $base/scaling_governor") &&
                    execute("echo ${profile.maxFreq} > $base/scaling_max_freq") &&
                    execute("echo ${profile.minFreq} > $base/scaling_min_freq")
            }
            Log.d(TAG, "Profile ${profile.name} applied: $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "applyProfile failed", e)
            false
        }
    }

    fun disableThermalThrottling(): Boolean =
        execute("for z in /sys/class/thermal/thermal_zone*/mode; do echo disabled > \$z; done")

    fun enableThermalThrottling(): Boolean =
        execute("for z in /sys/class/thermal/thermal_zone*/mode; do echo enabled > \$z; done")

    private fun execute(cmd: String): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor() == 0
        } catch (e: Exception) { false }
    }
}
