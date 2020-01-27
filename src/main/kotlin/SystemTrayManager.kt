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

import com.github.vatbub.smartcharge.ChargingMode.*
import com.github.vatbub.smartcharge.GuiHelper.showMainView
import javafx.application.Platform
import java.awt.*

object SystemTrayManager {
    private var checkBoxMenuItemChargeFull: CheckboxMenuItem? = null
    private var checkBoxMenuItemChargeOptimized: CheckboxMenuItem? = null
    private var checkBoxMenuItemChargeOff: CheckboxMenuItem? = null

    private var menuUpdateInProgress = false

    private var trayIcon: TrayIcon? = null

    fun createMenu() {
        if (!SystemTray.isSupported())
            throw IllegalStateException("SystemTray not supported on this system")

        val popupMenu = PopupMenu()

        val iconUrl = javaClass.getResource("trayIcon.png")
        val iconImage = Toolkit.getDefaultToolkit().getImage(iconUrl)
        trayIcon = TrayIcon(iconImage)

        checkBoxMenuItemChargeFull = CheckboxMenuItem("Charge to 100%")
        checkBoxMenuItemChargeOptimized = CheckboxMenuItem("Optimized charging")
        checkBoxMenuItemChargeOff = CheckboxMenuItem("Don't charge")

        checkBoxMenuItemChargeFull?.addItemListener { event ->
            if (menuUpdateInProgress) return@addItemListener
            if (event.stateChange != 1) return@addItemListener
            preferences[Keys.CurrentChargingMode] = AlwaysOn
            Daemon.applyConfiguration()
            updateTrayMenu()
            updateMainView()
        }

        checkBoxMenuItemChargeOptimized?.addItemListener { event ->
            if (menuUpdateInProgress) return@addItemListener
            if (event.stateChange != 1) return@addItemListener
            preferences[Keys.CurrentChargingMode] = Optimized
            Daemon.applyConfiguration()
            updateTrayMenu()
            updateMainView()
        }

        checkBoxMenuItemChargeOff?.addItemListener { event ->
            if (menuUpdateInProgress) return@addItemListener
            if (event.stateChange != 1) return@addItemListener
            preferences[Keys.CurrentChargingMode] = AlwaysOff
            Daemon.applyConfiguration()
            updateTrayMenu()
            updateMainView()
        }

        val menuItemSettings = MenuItem("Show settings")
        val menuItemExit = MenuItem("Exit")

        menuItemSettings.addActionListener { showMainView() }
        menuItemExit.addActionListener {
            EntryClass.performShutDownTasks()
            Platform.exit()
            SystemTray.getSystemTray().remove(trayIcon)
        }

        popupMenu.add(checkBoxMenuItemChargeFull)
        popupMenu.add(checkBoxMenuItemChargeOptimized)
        popupMenu.add(checkBoxMenuItemChargeOff)
        popupMenu.addSeparator()
        popupMenu.add(menuItemSettings)
        popupMenu.add(menuItemExit)

        trayIcon?.addActionListener { showMainView() }

        trayIcon?.popupMenu = popupMenu

        SystemTray.getSystemTray().add(trayIcon)

        updateTrayMenu()
    }

    fun updateTrayMenu() {
        if (menuUpdateInProgress) return
        menuUpdateInProgress = true

        checkBoxMenuItemChargeFull?.state = preferences[Keys.CurrentChargingMode] == AlwaysOn
        checkBoxMenuItemChargeOptimized?.state = preferences[Keys.CurrentChargingMode] == Optimized
        checkBoxMenuItemChargeOff?.state = preferences[Keys.CurrentChargingMode] == AlwaysOff

        val stateString = when (preferences[Keys.CurrentChargingMode]) {
            AlwaysOn -> "Charge to 100%"
            AlwaysOff -> "Do not charge"
            Optimized -> "Optimized charge"
        }

        trayIcon?.toolTip = "Current mode: $stateString"

        menuUpdateInProgress = false
    }

    fun showTrayMessage(caption: String, text: String, messageType: TrayIcon.MessageType) {
        trayIcon?.displayMessage(caption, text, messageType)
    }

    private fun updateMainView() =
            Platform.runLater { EntryClass.instance?.controllerInstance?.updateGuiFromConfiguration() }
}
