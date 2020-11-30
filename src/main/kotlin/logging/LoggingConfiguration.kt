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
package com.github.vatbub.smartcharge.logging

import com.github.vatbub.smartcharge.appDataFolder
import com.github.vatbub.smartcharge.appId
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import kotlin.properties.Delegates

object LoggingConfiguration {
    private val launchDate = Date()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    private val launchTimestamp = dateFormat.format(launchDate)

    var fileLogLevel: Level by Delegates.observable(Level.ALL) { _, _, newValue -> LoggingHandlers.fileHandler?.level = newValue }
    var consoleLogLevel: Level by Delegates.observable(Level.INFO) { _, _, newValue -> LoggingHandlers.consoleHandler.level = newValue }
    var textFieldLogLevel: Level by Delegates.observable(Level.INFO) { _, _, newValue -> LoggingHandlers.TextFieldHandler.level = newValue }
    var trayLogLevel: Level by Delegates.observable(Level.WARNING) { _, _, newValue -> LoggingHandlers.SystemTrayHandler.level = newValue }
    var guiErrorMessageLogLevel: Level by Delegates.observable(Level.WARNING) { _, _, newValue -> LoggingHandlers.GuiErrorMessageHandler.level = newValue }
    var logFilePath: File? by Delegates.observable(appDataFolder.toPath().resolve("Logs").toFile()) { _, _, _ -> LoggingHandlers.reinitializeFileHandler() }
    var logFileName by Delegates.observable("log_" + appId + "_DateTime.xml") { _, _, _ -> LoggingHandlers.reinitializeFileHandler() }

    fun combineLogFilePathAndName(): String? {
        val logFilePathCopy = logFilePath
                ?: return null
        return "${logFilePathCopy.absolutePath}${File.separator}${logFileName.replace("DateTime", launchTimestamp)}"
    }
}
