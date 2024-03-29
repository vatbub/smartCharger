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

import com.github.vatbub.smartcharge.EntryClass
import com.github.vatbub.smartcharge.SystemTrayManager
import javafx.application.Platform
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import java.awt.TrayIcon
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.logging.*
import kotlin.properties.Delegates

object LoggingHandlers {
    object FileHandlerLock
    object InitLock

    private var handlersInitialized = false
    private val globalJDKLogger = Logger.getLogger("")

    fun initializeIfUninitialized() {
        if (handlersInitialized) return
        synchronized(InitLock) {
            if (handlersInitialized) return

            globalJDKLogger.level = Level.ALL

            while (globalJDKLogger.handlers.isNotEmpty())
                globalJDKLogger.removeHandler(globalJDKLogger.handlers[0])

            globalJDKLogger.addHandler(consoleHandler)
            globalJDKLogger.addHandler(TextFieldHandler)
            globalJDKLogger.addHandler(SystemTrayHandler)
            globalJDKLogger.addHandler(GuiErrorMessageHandler)
            reinitializeFileHandler()

            handlersInitialized = true
        }
    }

    fun reinitializeFileHandler() {
        val oldHandler = internalFileHandler
        if (oldHandler != null) globalJDKLogger.removeHandler(oldHandler)
        internalFileHandler = null
        val newHandler = fileHandler
        if (newHandler != null) globalJDKLogger.addHandler(newHandler)
    }

    var currentLogFile: File? = null
        private set
    private var internalFileHandler: FileHandler? = null
    val fileHandler: FileHandler?
        get() {
            if (internalFileHandler != null) return internalFileHandler
            synchronized(FileHandlerLock) {
                if (internalFileHandler != null) return internalFileHandler
                val logFilePathCopy = LoggingConfiguration.logFilePath
                    ?: return null
                val finalLogLocation = LoggingConfiguration.combineLogFilePathAndName() ?: return null

                try {
                    Files.createDirectories(logFilePathCopy.toPath())
                } catch (e: IOException) {
                    LoggingConfiguration.logFilePath = null
                    logger.warn("File logging disabled due to an IOException", e)
                }

                currentLogFile = File(finalLogLocation)
                internalFileHandler = object : FileHandler(finalLogLocation) {
                    override fun publish(record: LogRecord?) {
                        super.publish(record)
                        flush()
                    }
                }

                internalFileHandler!!.level = LoggingConfiguration.fileLogLevel

                println("Saving the log file as '$finalLogLocation'")

                return internalFileHandler
            }
        }

    val consoleHandler by lazy {
        val handler = object : Handler() {
            override fun publish(record: LogRecord?) {
                if (record == null) return
                if (record.level.intValue() < this.level.intValue()) return
                try {
                    val message = formatter.format(record)
                    if (record.level.intValue() >= Level.WARNING.intValue())
                        System.err.print(message)
                    else
                        print(message)
                } catch (e: Exception) {
                    reportError(null, e, ErrorManager.FORMAT_FAILURE)
                }
            }

            override fun flush() {}

            override fun close() {}

        }
        handler.formatter = OneLineFormatter()
        handler.level = LoggingConfiguration.consoleLogLevel
        handler
    }

    object TextFieldHandler : Handler() {
        private object Lock

        var loggingTextFlow: TextFlow? by Delegates.observable(null) { _, _, newValue -> publishDelayedTextNodes(newTextFlow = newValue) }
        private val delayedTextNodes = mutableListOf<Text>()
        var delayLogUpdates by Delegates.observable(false) { _, _, newValue -> publishDelayedTextNodes(newValue) }

        init {
            this.level = LoggingConfiguration.textFieldLogLevel
            this.formatter = OneLineFormatter()
        }

        private fun publishDelayedTextNodes(newDelayLogUpdatesValue: Boolean = delayLogUpdates, newTextFlow: TextFlow? = loggingTextFlow) = Platform.runLater {
            synchronized(Lock) {
                if (newDelayLogUpdatesValue) return@synchronized
                if (newTextFlow == null) return@synchronized

                newTextFlow.children.addAll(delayedTextNodes)
                delayedTextNodes.clear()
            }
        }

        override fun publish(record: LogRecord?) {
            if (record == null) return
            if (record.level.intValue() < this.level.intValue()) return

            synchronized(Lock) {
                try {
                    val textNode = Text(formatter.format(record))
                    textNode.style = if (record.level.intValue() >= Level.WARNING.intValue())
                        "-fx-fill: RED;-fx-font-weight:bold;"
                    else
                        "-fx-fill: BLACK;-fx-font-weight:normal;"

                    val textFlow = loggingTextFlow
                    if (delayLogUpdates || textFlow == null)
                        delayedTextNodes.add(textNode)
                    else
                        Platform.runLater { textFlow.children.add(textNode) }
                } catch (e: Exception) {
                    reportError(null, e, ErrorManager.FORMAT_FAILURE)
                }
            }
        }

        override fun flush() {}

        override fun close() {}

    }

    object SystemTrayHandler : Handler() {
        init {
            this.level = LoggingConfiguration.trayLogLevel
            this.formatter = SystemTrayFormatter()
        }

        override fun publish(record: LogRecord?) {
            if (record == null) return
            if (record.level.intValue() < this.level.intValue()) return

            val messageType = when (record.level) {
                Level.CONFIG -> TrayIcon.MessageType.NONE
                Level.INFO -> TrayIcon.MessageType.INFO
                Level.WARNING -> TrayIcon.MessageType.WARNING
                Level.SEVERE -> TrayIcon.MessageType.ERROR
                else -> TrayIcon.MessageType.NONE
            }
            SystemTrayManager.showTrayMessage("Smart charge", formatter.format(record), messageType)
        }

        override fun flush() {}

        override fun close() {}

    }

    object GuiErrorMessageHandler : Handler() {
        init {
            this.level = LoggingConfiguration.guiErrorMessageLogLevel
        }

        override fun publish(record: LogRecord?) {
            if (record == null) return
            if (record.level.intValue() < this.level.intValue()) return

            if (EntryClass.instance != null)
                Platform.runLater { EntryClass.instance?.controllerInstance?.showException(record) }
        }

        override fun flush() {}

        override fun close() {}

    }
}
