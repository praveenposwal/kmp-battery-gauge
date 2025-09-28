package com.example.composemutliplatform

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeMutliplatform",
    ) {
        App(batteryInfo =  remember {BatteryInfo()})
    }
}