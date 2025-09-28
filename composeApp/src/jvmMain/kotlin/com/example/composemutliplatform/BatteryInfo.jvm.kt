package com.example.composemutliplatform

import oshi.SystemInfo
import oshi.hardware.PowerSource

actual class BatteryInfo {
    actual fun getBatteryLevel(): Pair<Int, Boolean> {
        val systemInfo = SystemInfo()
        val powerSources = systemInfo.hardware.powerSources

        if (powerSources.isEmpty()) {
            // No battery detected
            return Pair(-1, false)
        }

        val ps = powerSources[0] // usually one battery
        val level = (ps.remainingCapacityPercent * 100).toInt()
        val isCharging = ps.isCharging

        return Pair(level, isCharging)
    }
}
