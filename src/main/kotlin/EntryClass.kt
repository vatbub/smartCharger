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
import com.github.vatbub.common.core.Common
import com.github.vatbub.javaautostart.AutoStartManager
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import org.apache.commons.lang3.SystemUtils
import java.awt.TrayIcon
import kotlin.system.exitProcess

const val appId = "com.github.vatbub.smartCharge"

class EntryClass private constructor(callLaunch: Boolean, vararg args: String?) : Application() {
    companion object {
        private lateinit var startupArgs: Array<out String>
        val commandLineArgs = CommandLineArgs()
        var instance: EntryClass? = null
        private var shutdownActionsPerformed = false
        val autoStartManager = if (SystemUtils.IS_OS_WINDOWS) AutoStartManager(appId) else null

        private object ShutdownLock

        @JvmStatic
        fun main(vararg args: String) {
            startupArgs = args
            val jCommander = JCommander(commandLineArgs)
            jCommander.parse(*args)
            if (commandLineArgs.help) {
                jCommander.usage()
                exitProcess(1)
            }

            Common.getInstance().appName = appId
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)

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
                Daemon.switchCharger(chargerStateToApply)

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
                Daemon.stop()
                if (preferences[Keys.StopChargingOnShutdown] && !ignoreShutdownSetting) {
                    logger.info("Switching the charger off at shut down...")
                    Daemon.switchCharger(Daemon.ChargerState.Off)
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
        @Suppress("SENSELESS_COMPARISON")
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
        primaryStage.icons.add(Image(javaClass.getResourceAsStream("icon.png")))
        primaryStage.minWidth = root.minWidth(0.0) + 70
        primaryStage.minHeight = root.minHeight(0.0) + 70

        primaryStage.scene = scene

        primaryStage.setOnCloseRequest {
            if (!preferences[Keys.TrayMessageShown]) {
                preferences[Keys.TrayMessageShown] = true
                SystemTrayManager.showTrayMessage("Smart charge", "Smart charge is still working in the background. Check the system tray icon to see more.", TrayIcon.MessageType.INFO)
            }

            hideMainView()
            it.consume()
        }

        showMainView()
    }

    fun hideMainView() {
        currentStage?.hide()
    }

    fun showMainView() {
        val stage = currentStage ?: return
        stage.show()
        stage.isIconified = false
    }
}
