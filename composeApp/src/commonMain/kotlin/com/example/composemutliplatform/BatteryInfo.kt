package com.example.composemutliplatform

expect class BatteryInfo {
    fun getBatteryLevel(): Pair<Int, Boolean>
}