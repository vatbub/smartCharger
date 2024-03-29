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

import com.beust.jcommander.JCommander
import com.github.vatbub.javaautostart.AutoStartManager
import com.github.vatbub.smartcharge.Charger.ChargerState.Off
import com.github.vatbub.smartcharge.logging.LoggingHandlers
import com.github.vatbub.smartcharge.logging.exceptionHandler
import com.github.vatbub.smartcharge.logging.logger
import de.jangassen.MenuToolkit
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.image.Image
import javafx.stage.Stage
import org.apache.commons.lang3.SystemUtils
import java.awt.Taskbar
import java.awt.Toolkit
import java.awt.TrayIcon
import kotlin.system.exitProcess

const val appId = "com.github.vatbub.smartCharge"
const val scheduledTaskName = "Smart Charge Before Shutdown Hook"

class EntryClass private constructor(callLaunch: Boolean, vararg args: String?) : Application() {
    companion object {
        private lateinit var startupArgs: Array<out String>
        val commandLineArgs = CommandLineArgs()
        var instance: EntryClass? = null
        private var shutdownActionsPerformed = false
        val autoStartManager = if (SystemUtils.IS_OS_WINDOWS) AutoStartManager(appId) else null
        val taskSchedulerManager = if (SystemUtils.IS_OS_WINDOWS) TaskSchedulerManager(scheduledTaskName) else null

        private object ShutdownLock

        fun actualMain(vararg args: String) {
            startupArgs = args
            val jCommander = JCommander(commandLineArgs)
            jCommander.parse(*args)
            if (commandLineArgs.help) {
                jCommander.usage()
                exitProcess(1)
            }

            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
            LoggingHandlers.initializeIfUninitialized()

            logger.info("Welcome to smartCharge!")
            val lockFlag = unique.acquireLock()
            if (!lockFlag)
                throw IllegalStateException("Unable to acquire the instance/IM java.exe lock using Unique4j!")

            val modeToApply = commandLineArgs.chargingMode
            if (modeToApply != null)
                preferences[Keys.CurrentChargingMode] = modeToApply

            if (!commandLineArgs.noDaemon) {
                Daemon.applyConfiguration()
                SystemTrayManager.createMenu()
            }

            val chargerStateToApply = commandLineArgs.switchChargerState
            if (chargerStateToApply != null)
                Charger.switchCharger(chargerStateToApply)

            if (!commandLineArgs.noGui)
                startGui()

            Runtime.getRuntime().addShutdownHook(Thread { performShutDownTasks() })

            if (commandLineArgs.noDaemon) {
                performShutDownTasks(ignoreShutdownSetting = true)
                exitProcess(0)
            }
        }

        fun startGui() {
            EntryClass(true, *startupArgs)
        }

        fun performShutDownTasks(ignoreShutdownSetting: Boolean = false) {
            if (shutdownActionsPerformed) return
            synchronized(ShutdownLock) {
                if (shutdownActionsPerformed) return
                Daemon.prepareApplicationShutdown()
                if (!ignoreShutdownSetting && taskSchedulerManager != null && taskSchedulerManager.taskExists()) {
                    logger.info("Switching the charger off at shut down...")
                    Charger.switchCharger(Off)
                }
                val lockFreeFlag = unique.freeLock()
                if (!lockFreeFlag)
                    throw IllegalStateException("Unable to release the instance lock using Unique4j!")
                logger.info("Goodbye!")
                shutdownActionsPerformed = true
            }
        }
    }

    init {
        if (callLaunch)
            launch(*args)
    }

    @Suppress("unused")
    constructor() : this(false, null)

    var currentStage: Stage? = null
        private set
    var controllerInstance: MainView? = null
        private set

    override fun start(primaryStage: Stage) {
        instance = this
        currentStage = primaryStage

        Platform.setImplicitExit(false)
        val fxmlLoader = FXMLLoader(javaClass.getResource("MainView.fxml"), null)
        val root = fxmlLoader.load<Parent>()
        controllerInstance = fxmlLoader.getController()

        val scene = Scene(root)
        primaryStage.title = "SmartCharger"
        val iconName = "icon.png"
        primaryStage.icons.add(Image(javaClass.getResourceAsStream(iconName)))

        if (SystemUtils.IS_OS_MAC) {
            setDockIconOnMac(iconName)
            setMacMenuBar(primaryStage.title)
        }

        primaryStage.minWidth = root.minWidth(0.0) + 70
        primaryStage.minHeight = root.minHeight(0.0) + 70

        primaryStage.scene = scene

        primaryStage.setOnCloseRequest {
            if (!preferences[Keys.TrayMessageShown]) {
                preferences[Keys.TrayMessageShown] = true
                SystemTrayManager.showTrayMessage(
                    "Smart charge",
                    "Smart charge is still working in the background. Check the system tray icon to see more.",
                    TrayIcon.MessageType.INFO
                )
            }

            hideMainView()
            it.consume()
        }

        showMainView()
    }

    @Suppress("SameParameterValue")
    private fun setDockIconOnMac(iconResourceName: String) {
        val awtToolkit = Toolkit.getDefaultToolkit()
        if (!Taskbar.isTaskbarSupported()) return

        val imageResource = javaClass.getResource(iconResourceName)
        val taskbar = Taskbar.getTaskbar()
        taskbar.iconImage = awtToolkit.getImage(imageResource)
    }

    private fun setMacMenuBar(appName: String) = with(MenuToolkit.toolkit()) {
        val defaultApplicationMenu: Menu = createDefaultApplicationMenu(appName)
        setApplicationMenu(defaultApplicationMenu)
    }

    fun hideMainView() {
        controllerInstance?.delayLogUpdatesAndSuppressErrorDialogs = true
        currentStage?.hide()
    }

    fun showMainView() {
        val stage = currentStage ?: return
        stage.show()
        stage.isIconified = false
        controllerInstance?.delayLogUpdatesAndSuppressErrorDialogs = false
    }
}
