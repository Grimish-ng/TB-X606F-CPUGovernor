package com.tbx606f.cpugovernor

data class PowerProfile(
    val name: String,
    val governor: String,
    val minFreq: Int,
    val maxFreq: Int,
    val estimatedDrain: String
)

object Profiles {
    val BATTERY_SAVER = PowerProfile("Battery Saver", "powersave", 600000, 600000, "6–10%/hr")
    val ECO = PowerProfile("Eco", "conservative", 600000, 1196000, "10–16%/hr")
    val BALANCED = PowerProfile("Balanced", "schedutil", 600000, 1690000, "16–24%/hr")
    val PERFORMANCE = PowerProfile("Performance", "performance", 1200000, 2000000, "25–38%/hr")

    val ALL = listOf(BATTERY_SAVER, ECO, BALANCED, PERFORMANCE)
}
