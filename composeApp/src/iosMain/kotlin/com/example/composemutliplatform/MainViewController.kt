package com.example.composemutliplatform

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { App(batteryInfo =  remember {BatteryInfo()}) }