package com.example.composemutliplatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return platform.name
    }
}