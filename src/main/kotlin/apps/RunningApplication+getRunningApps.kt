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
package com.github.vatbub.smartcharge.apps

import java.nio.charset.Charset
import kotlin.time.*

@ExperimentalTime
fun RunningApplication.Companion.getRunningApps(): List<RunningApplication> {
    // Linux: val process = Runtime.getRuntime().exec("ps -e")
    val process = ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh", "/v").start()
    process.outputStream.close()
    return process.inputStream.bufferedReader(Charset.forName("UTF-8")).use { input ->
        input.readLines()
    }.map {
        val parts = it.split(",")
                .map { part -> part.removePrefix("\"").removeSuffix("\"") }
        RunningApplication(
                imageName = parts[0],
                pid = parts[1].toInt(),
                sessionName = parts[2],
                sessionId = parts[3].toInt(),
                memoryUsage = parts[4],
                status = ApplicationStatus.fromWindowsName(parts[5]),
                userName = parts[6].toNullIfNotApplicable(),
                cpuTime = parts[7].toDuration(),
                windowTitle = parts[8].toNullIfNotApplicable()
        )
    }
}

private fun String.toNullIfNotApplicable(): String? = if (this in notApplicableTranslations) null else this

@ExperimentalTime
private fun String.toDuration(): Duration {
    val parts = this.split(":")
    return parts[0].toInt().hours + parts[0].toInt().minutes + parts[0].toInt().seconds
}

private val notApplicableTranslations = listOf("Nicht zutreffend", "Not applicable")
