/*-
 * #%L
 * Smart Charge
 * %%
 * Copyright (C) 2016 - 2020 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package com.github.vatbub.smartcharge.profiles

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
