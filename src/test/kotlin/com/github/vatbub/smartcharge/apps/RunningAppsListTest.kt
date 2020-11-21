package com.github.vatbub.smartcharge.apps

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class RunningAppsListTest {
    @Test
    fun printRunningAppsTest() = assertDoesNotThrow {
        RunningApplication.getRunningApps().forEach { println(it.imageName) }
    }

    @Test
    fun printRunningAppsWithWindowTitleTest() = assertDoesNotThrow {
        RunningApplication.getRunningApps()
                .filter { it.windowTitle != null }
                .forEach { println("${it.imageName} - ${it.windowTitle}") }
    }
}