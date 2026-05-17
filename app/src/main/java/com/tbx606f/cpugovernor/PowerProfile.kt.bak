package com.tbx606f.cpugovernor

/**
 * Represents a power profile preset for the TB-X606F.
 * maxFreqKhz values are based on the Mediatek Helio P22T frequency table.
 */
data class PowerProfile(
    val id: String,
    val displayName: String,
    val description: String,
    val governor: String,
    val maxFreqKhz: Int,       // Max frequency in kHz
    val iconRes: Int,
    val estimatedDrainMin: Int, // % battery per hour, low estimate
    val estimatedDrainMax: Int  // % battery per hour, high estimate
) {
    val maxFreqMhz: Int get() = maxFreqKhz / 1000
}

object PowerProfiles {

    val BATTERY_SAVER = PowerProfile(
        id = "battery_saver",
        displayName = "Battery saver",
        description = "Locks cores to lowest frequency. Best for reading, music, and idle use.",
        governor = "powersave",
        maxFreqKhz = 600_000,     // 600 MHz — lowest stable freq on P22T
        iconRes = R.drawable.ic_battery_saver,
        estimatedDrainMin = 6,
        estimatedDrainMax = 10
    )

    val ECO = PowerProfile(
        id = "eco",
        displayName = "Eco",
        description = "Scales up slowly. Good balance for light browsing and documents.",
        governor = "conservative",
        maxFreqKhz = 1_196_000,   // 1.196 GHz
        iconRes = R.drawable.ic_eco,
        estimatedDrainMin = 10,
        estimatedDrainMax = 16
    )

    val BALANCED = PowerProfile(
        id = "balanced",
        displayName = "Balanced",
        description = "Scheduler-driven scaling. Recommended for everyday use.",
        governor = "schedutil",
        maxFreqKhz = 1_690_000,   // 1.69 GHz
        iconRes = R.drawable.ic_balanced,
        estimatedDrainMin = 16,
        estimatedDrainMax = 24
    )

    val PERFORMANCE = PowerProfile(
        id = "performance",
        displayName = "Performance",
        description = "Runs at maximum frequency. Use for games or heavy workloads.",
        governor = "performance",
        maxFreqKhz = 2_000_000,   // 2.0 GHz — P22T max
        iconRes = R.drawable.ic_performance,
        estimatedDrainMin = 25,
        estimatedDrainMax = 38
    )

    val ALL = listOf(BATTERY_SAVER, ECO, BALANCED, PERFORMANCE)

    fun findById(id: String): PowerProfile? = ALL.find { it.id == id }
}
