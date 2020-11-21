package com.github.vatbub.smartcharge.apps

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
data class RunningApplication constructor(
        val imageName: String,
        val pid: Int,
        val sessionName: String,
        val sessionId: Int,
        val memoryUsage: String,
        val status: ApplicationStatus,
        val userName: String?,
        val cpuTime: Duration,
        val windowTitle: String?
) {
    companion object
}

enum class ApplicationStatus(private val windowsName: String) {
    Unknown("Unknown"),
    Running("Running"),
    NotResponding("Not Responding");

    companion object {
        fun fromWindowsName(windowsName: String): ApplicationStatus =
                values().firstOrNull { it.windowsName == windowsName }
                        ?: throw IllegalArgumentException("No enum constant for windows name $windowsName")
    }
}