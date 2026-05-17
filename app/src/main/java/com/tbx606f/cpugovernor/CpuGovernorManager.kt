package com.tbx606f.cpugovernor

/**
 * Handles all root shell commands for reading and writing CPU governor settings.
 * The TB-X606F (Lenovo Tab M10 HD 2nd Gen) has a Mediatek Helio P22T with 8 cores.
 */
object CpuGovernorManager {

    private const val CPU_PATH = "/sys/devices/system/cpu"
    const val CORE_COUNT = 8

    // Governors known to be present on Mediatek Helio P22T
    val AVAILABLE_GOVERNORS = listOf(
        "powersave", "conservative", "userspace", "ondemand", "schedutil", "performance"
    )

    data class CoreInfo(
        val index: Int,
        val governor: String,
        val currentFreqMhz: Int,
        val maxFreqMhz: Int,
        val minFreqMhz: Int,
        val online: Boolean
    )

    /**
     * Executes a command as root using `su -c`.
     * Returns Pair(stdout, success).
     */
    fun runAsRoot(command: String): Pair<String, Boolean> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val output = process.inputStream.bufferedReader().readText().trim()
            val error = process.errorStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()
            if (exitCode == 0) Pair(output, true) else Pair(error, false)
        } catch (e: Exception) {
            Pair(e.message ?: "Unknown error", false)
        }
    }

    /**
     * Checks whether the app has root access by running a simple privileged command.
     */
    fun checkRootAccess(): Boolean {
        val (_, success) = runAsRoot("id")
        return success
    }

    /**
     * Reads the current state of all 8 CPU cores.
     */
    fun readAllCores(): List<CoreInfo> {
        val cores = mutableListOf<CoreInfo>()
        for (i in 0 until CORE_COUNT) {
            val basePath = "$CPU_PATH/cpu$i"
            val onlinePath = "$basePath/online"
            val freqPath = "$basePath/cpufreq"

            // cpu0 is always online and may not have an 'online' file
            val online = if (i == 0) {
                true
            } else {
                val (onlineStr, _) = runAsRoot("cat $onlinePath 2>/dev/null || echo 1")
                onlineStr.trim() == "1"
            }

            if (!online) {
                cores.add(CoreInfo(i, "offline", 0, 0, 0, false))
                continue
            }

            val (governor, _) = runAsRoot("cat $freqPath/scaling_governor 2>/dev/null || echo unknown")
            val (curFreqStr, _) = runAsRoot("cat $freqPath/scaling_cur_freq 2>/dev/null || echo 0")
            val (maxFreqStr, _) = runAsRoot("cat $freqPath/scaling_max_freq 2>/dev/null || echo 0")
            val (minFreqStr, _) = runAsRoot("cat $freqPath/scaling_min_freq 2>/dev/null || echo 0")

            val curFreqMhz = (curFreqStr.trim().toLongOrNull() ?: 0L) / 1000L
            val maxFreqMhz = (maxFreqStr.trim().toLongOrNull() ?: 0L) / 1000L
            val minFreqMhz = (minFreqStr.trim().toLongOrNull() ?: 0L) / 1000L

            cores.add(
                CoreInfo(
                    index = i,
                    governor = governor.trim(),
                    currentFreqMhz = curFreqMhz.toInt(),
                    maxFreqMhz = maxFreqMhz.toInt(),
                    minFreqMhz = minFreqMhz.toInt(),
                    online = true
                )
            )
        }
        return cores
    }

    /**
     * Applies a governor and max frequency to all online cores.
     * Returns true if all writes succeeded.
     */
    fun applyProfile(governor: String, maxFreqKhz: Int): Boolean {
        val sb = StringBuilder()
        for (i in 0 until CORE_COUNT) {
            val freqPath = "$CPU_PATH/cpu$i/cpufreq"
            sb.append("echo $governor > $freqPath/scaling_governor 2>/dev/null; ")
            sb.append("echo $maxFreqKhz > $freqPath/scaling_max_freq 2>/dev/null; ")
        }
        val (_, success) = runAsRoot(sb.toString())
        return success
    }

    /**
     * Reads the device CPU temperature (Mediatek thermal zone path).
     */
    fun readCpuTemperature(): Int {
        // Mediatek Helio P22T thermal zone — try common paths
        val paths = listOf(
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/proc/cooler/tzts_0"
        )
        for (path in paths) {
            val (temp, success) = runAsRoot("cat $path 2>/dev/null")
            if (success) {
                val raw = temp.trim().toLongOrNull() ?: continue
                // Most MTK zones report in millidegrees
                return if (raw > 1000) (raw / 1000).toInt() else raw.toInt()
            }
        }
        return -1
    }

    /**
     * Lists governors actually available on this device.
     */
    fun readAvailableGovernors(): List<String> {
        val (output, success) = runAsRoot(
            "cat $CPU_PATH/cpu0/cpufreq/scaling_available_governors 2>/dev/null"
        )
        return if (success && output.isNotBlank()) {
            output.trim().split(" ").filter { it.isNotBlank() }
        } else {
            AVAILABLE_GOVERNORS
        }
    }
}
