package com.example.composemutliplatform

import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState

actual class BatteryInfo {
    actual fun getBatteryLevel(): Pair<Int, Boolean> {
        val device = UIDevice.currentDevice
        device.batteryMonitoringEnabled = true

        val isCharging = when (device.batteryState) {
            UIDeviceBatteryState.UIDeviceBatteryStateCharging -> true
            else -> false
        }

        val level = device.batteryLevel
        val batteryPct =  if (level >= 0.0) {
            (level * 100).toInt()
        } else {
            0 // unavailable
        }
        return Pair(batteryPct,isCharging)
    }
}