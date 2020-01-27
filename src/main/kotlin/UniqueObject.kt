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

import com.github.vatbub.smartcharge.ApplicationInstanceMessage.*
import com.github.vatbub.smartcharge.ChargingMode.*
import com.github.vatbub.smartcharge.Daemon.ChargerState.Off
import com.github.vatbub.smartcharge.Daemon.ChargerState.On
import javafx.application.Platform
import tk.pratanumandal.unique4j.Unique

private enum class ApplicationInstanceMessage {
    showGui, modeFull, modeOptimized, modeNoCharge, switchChargerOn, switchChargerOff
}

private object MessageHelper {
    private val separator = ";"

    fun buildMessage(): String {
        val messages = mutableListOf<ApplicationInstanceMessage>()

        if (!EntryClass.commandLineArgs.noGui)
            messages.add(showGui)

        val chargerModeMessage = when (EntryClass.commandLineArgs.chargingMode) {
            AlwaysOn -> modeFull
            ChargingMode.Optimized -> modeOptimized
            ChargingMode.AlwaysOff -> modeNoCharge
            null -> null
        }
        if (chargerModeMessage != null)
            messages.add(chargerModeMessage)

        val chargerStateMessage = when (EntryClass.commandLineArgs.switchChargerState) {
            On -> switchChargerOn
            Off -> switchChargerOff
            null -> null
        }
        if (chargerStateMessage != null)
            messages.add(chargerStateMessage)

        return messages.joinToString(separator)
    }

    fun decode(message: String): List<ApplicationInstanceMessage> {
        val splitResult = message.split(separator)
        return List(splitResult.size) { ApplicationInstanceMessage.valueOf(splitResult[it]) }
    }
}

val unique = object : Unique(appId) {
    override fun sendMessage(): String {
        logger.warn("Another instance is already running, passing arguments to other instance...")
        return MessageHelper.buildMessage()
    }

    override fun handleException(exception: Exception?) {
        if (exception != null)
            throw exception
    }

    override fun receiveMessage(message: String?) {
        if (message == null) {
            logger.warn("Received a message from another instance of this application, but the message was null. This is likely a bug, but it will be ignored for now. Also: https://www.xkcd.com/2200/")
            return
        }

        logger.info("Received a message from another instance of this application, executing appropriate actions...")
        MessageHelper.decode(message).forEach {
            when (it) {
                showGui -> GuiHelper.showMainView()
                modeFull -> {
                    preferences[Keys.CurrentChargingMode] = AlwaysOn
                    Daemon.applyConfiguration()
                }
                modeOptimized -> {
                    preferences[Keys.CurrentChargingMode] = Optimized
                    Daemon.applyConfiguration()
                }
                modeNoCharge -> {
                    preferences[Keys.CurrentChargingMode] = AlwaysOff
                    Daemon.applyConfiguration()
                }
                switchChargerOn -> Daemon.switchCharger(On)
                switchChargerOff -> Daemon.switchCharger(Off)
            }
        }
        SystemTrayManager.updateTrayMenu()
        Platform.runLater { EntryClass.instance?.controllerInstance?.updateGuiFromConfiguration() }
    }

    override fun beforeExit() {
        super.beforeExit()
        logger.warn("Exiting as another instance is running...")
    }
}
