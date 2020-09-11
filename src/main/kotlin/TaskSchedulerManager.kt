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
package com.github.vatbub.smartcharge

import com.github.vatbub.javaautostart.AutoStartLaunchConfig
import com.github.vatbub.smartcharge.logging.logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream

// Docs: https://stackoverflow.com/questions/101647/how-to-schedule-a-task-to-run-when-shutting-down-windows
data class TaskSchedulerManager(val taskName: String) {
    fun createOnEventTask(launchConfig: AutoStartLaunchConfig,
                          eventChannel: String,
                          eventName: String) {
        runCommandWithExceptionWhenExitCodeIsNotZero(
                "schtasks /create " +
                        "/tn \"$taskName\" " +
                        "/tr \"${launchConfig.asCommand.replace("\"", "'")}\" " +
                        "/sc onevent /ec $eventChannel " +
                        "/mo *[$eventName]"
        )
    }

    fun taskExists(): Boolean {
        val command = "schtasks /Query /TN \"$taskName\""
        val exitCode = runCommandAndLogOutput(command)
                .waitFor()
        logger.info("Subprocess $command finished with exit code $exitCode")
        return exitCode == 0
    }

    fun deleteTask() {
        runCommandWithExceptionWhenExitCodeIsNotZero("schtasks /Delete /tn \"$taskName\" /F")
    }

    private fun runCommandWithExceptionWhenExitCodeIsNotZero(command: String) {
        val exitCode = runCommandAndLogOutput(command).waitFor()
        logger.info("Subprocess $command finished with exit code $exitCode")
        if (exitCode == 0) return
        throw IllegalStateException("Sub-process exited with non-zero exit code. Exit code was: $exitCode")
    }

    private fun runCommandAndLogOutput(command: String): Process {
        logger.info("Starting subprocess $command")
        return Runtime.getRuntime().exec(command).apply {
            inputStream.log()
            errorStream.log()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun InputStream.log() {
        GlobalScope.launch {
            this@log.bufferedReader().use { inputReader ->
                var nextLine = inputReader.readLine()
                while (nextLine != null) {
                    logger.info("[Command output] $nextLine")
                    nextLine = inputReader.readLine()
                }
            }
        }
    }
}
