package com.example.composemutliplatform

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager


actual class BatteryInfo(private val context: Context) {

    actual fun getBatteryLevel(): Pair<Int, Boolean> {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val batteryPct: Float? = level?.let { lvl ->
            scale?.takeIf { it > 0 }?.let { scl ->
                (lvl / scl.toFloat()) * 100f
            }
        }

        return Pair(batteryPct?.toInt() ?: 0,isCharging)
    }


}