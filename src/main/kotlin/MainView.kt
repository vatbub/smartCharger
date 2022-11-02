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
import com.github.vatbub.smartcharge.ChargingMode.*
import com.github.vatbub.smartcharge.extensions.highlightFileInExplorer
import com.github.vatbub.smartcharge.logging.LoggingHandlers
import com.github.vatbub.smartcharge.logging.logger
import com.github.vatbub.smartcharge.profiles.ProfileManager
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.TextFlow
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.StringWriter
import java.net.URL
import java.util.*
import java.util.logging.Level
import java.util.logging.LogRecord
import kotlin.properties.Delegates
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource


class MainView {

    var delayLogUpdatesAndSuppressErrorDialogs by Delegates.observable(false) { _, _, newValue ->
        LoggingHandlers.TextFieldHandler.delayLogUpdates = newValue
    }

    @FXML
    private lateinit var resources: ResourceBundle

    @FXML
    private lateinit var location: URL

    @FXML
    private lateinit var textFieldIFTTTMakerApiKey: TextField

    @FXML
    private lateinit var textFieldMinPercentage: TextField

    @FXML
    private lateinit var textFieldStopEvent: TextField

    @FXML
    private lateinit var logTextFlow: TextFlow

    @FXML
    private lateinit var logScrollPane: ScrollPane

    @FXML
    private lateinit var toggleButtonChargeOptimized: ToggleButton

    @FXML
    private lateinit var toggleButtonChargeFull: ToggleButton

    @FXML
    private lateinit var textFieldStartEvent: TextField

    @FXML
    private lateinit var textFieldMaxPercentage: TextField

    @FXML
    private lateinit var checkBoxStopChargingOnShutdown: CheckBox

    @FXML
    private lateinit var toggleButtonStopCharging: ToggleButton

    @FXML
    private lateinit var checkBoxAutoStart: CheckBox

    @FXML
    private lateinit var checkBoxProfiles: CheckBox

    @FXML
    private lateinit var textBoxOverrideProfilesTimeInMinutes: TextField

    @FXML
    private lateinit var buttonChangeProfileSettings: Button

    @FXML
    private lateinit var hBoxOverrideProfiles: HBox

    @FXML
    private lateinit var buttonApplyOverrideForProfiles: Button

    private var guiUpdateInProgress = false

    @FXML
    fun buttonLogIn(event: ActionEvent?) {
        LogInView { apiKey ->
            logger.info("Log in to IFTTT successful")
            textFieldIFTTTMakerApiKey.text = apiKey
        }
    }

    @FXML
    fun buttonChangeProfileSettingsOnAction(event: ActionEvent?) {
        ProfileDialog.show()
    }

    @OptIn(ExperimentalTime::class)
    @FXML
    fun buttonApplyOverrideForProfilesOnAction(event: ActionEvent?) {
        val overrideDurationInMinutes = textBoxOverrideProfilesTimeInMinutes.text.toLong().minutes
        ProfileManager.overrideProfilesUntil(TimeSource.Monotonic.markNow() + overrideDurationInMinutes)
    }

    @FXML
    fun buttonHelpApiKeyOnAction(event: ActionEvent?) {
    }

    @FXML
    fun buttonHelpStartEventOnAction(event: ActionEvent?) {
    }

    @FXML
    fun buttonHelpStopEventOnAction(event: ActionEvent?) {
    }

    @FXML
    fun buttonShowLogFileOnAction(event: ActionEvent?) {
        val currentLogFile = LoggingHandlers.currentLogFile ?: throw IllegalStateException("File logging is disabled")
        currentLogFile.highlightFileInExplorer()
    }

    @Suppress("SENSELESS_COMPARISON")
    @FXML
    fun initialize() {
        assert(textFieldIFTTTMakerApiKey != null) { "fx:id=\"textFieldIFTTTMakerApiKey\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(textFieldMinPercentage != null) { "fx:id=\"textFieldMinPercentage\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(textFieldStopEvent != null) { "fx:id=\"textFieldStopEvent\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(logTextFlow != null) { "fx:id=\"logTextFlow\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(logScrollPane != null) { "fx:id=\"logScrollPane\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(toggleButtonChargeOptimized != null) { "fx:id=\"toggleButtonChargeOptimized\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(toggleButtonChargeFull != null) { "fx:id=\"toggleButtonChargeFull\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(textFieldStartEvent != null) { "fx:id=\"textFieldStartEvent\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(textFieldMaxPercentage != null) { "fx:id=\"textFieldMaxPercentage\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(checkBoxStopChargingOnShutdown != null) { "fx:id=\"checkBoxStopChargingOnShutdown\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(toggleButtonStopCharging != null) { "fx:id=\"toggleButtonStopCharging\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(checkBoxAutoStart != null) { "fx:id=\"checkBoxAutoStart\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(checkBoxProfiles != null) { "fx:id=\"checkBoxAppSpecificSettings\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(textBoxOverrideProfilesTimeInMinutes != null) { "fx:id=\"textBoxOverrideAppSpecificSettingsTimeInMinutes\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(buttonChangeProfileSettings != null) { "fx:id=\"buttonChangeAppSpecificSettings\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(hBoxOverrideProfiles != null) { "fx:id=\"hBoxOverrideAppSpecificSettings\" was not injected: check your FXML file 'MainView.fxml'." }
        assert(buttonApplyOverrideForProfiles != null) { "fx:id=\"buttonApplyOverrideForAppSpecificSettings\" was not injected: check your FXML file 'MainView.fxml'." }

        updateGuiFromConfiguration()
        LoggingHandlers.TextFieldHandler.loggingTextFlow = logTextFlow
        logTextFlow.children.addListener(
            ListChangeListener {
                logTextFlow.layout()
                logScrollPane.layout()
                logScrollPane.vvalue = 1.0
            })

        textFieldIFTTTMakerApiKey.textProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            preferences[Keys.IFTTTMakerApiKey] = newValue
            Daemon.applyConfiguration()
            updateGuiFromConfiguration()
        }

        textFieldStartEvent.textProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            preferences[Keys.IFTTTStartChargingEventName] = newValue
            Daemon.applyConfiguration()
            updateGuiFromConfiguration()
        }

        textFieldStopEvent.textProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            preferences[Keys.IFTTTStopChargingEventName] = newValue
            Daemon.applyConfiguration()
            updateGuiFromConfiguration()
        }

        textFieldMinPercentage.textProperty().addListener { _, oldValue, newValue ->
            if (guiUpdateInProgress) return@addListener
            try {
                val newDouble = if (newValue != "") newValue.toDouble() else 0.0
                if (newDouble < 0 || newDouble > 100)
                    throw NumberFormatException("MinPercentage must be a value between 0 and 100")

                preferences[Keys.MinPercentage] = newDouble
            } catch (e: NumberFormatException) {
                logger.warn("Invalid user input for MinPercentage: $newValue")
                textFieldMinPercentage.text = oldValue
            }
            Daemon.applyConfiguration()
        }

        textFieldMaxPercentage.textProperty().addListener { _, oldValue, newValue ->
            if (guiUpdateInProgress) return@addListener
            try {
                val newDouble = if (newValue != "") newValue.toDouble() else 0.0
                if (newDouble < 0 || newDouble > 100)
                    throw NumberFormatException("MaxPercentage must be a value between 0 and 100")

                preferences[Keys.MaxPercentage] = newDouble
            } catch (e: NumberFormatException) {
                logger.warn("Invalid user input for MaxPercentage: $newValue")
                textFieldMaxPercentage.text = oldValue
                throw e
            }
            Daemon.applyConfiguration()
        }

        checkBoxStopChargingOnShutdown.selectedProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            val taskSchedulerManager = EntryClass.taskSchedulerManager
            if (taskSchedulerManager == null) {
                updateGuiFromConfiguration()
                return@addListener
            }

            if (newValue) {
                taskSchedulerManager.createOnEventTask(
                    launchConfig = AutoStartLaunchConfig(additionalArgs = "--noGui --noDaemon --switch Off"),
                    eventChannel = "System",
                    eventName = "System[(EventID=1074)]"
                )
            } else {
                taskSchedulerManager.deleteTask()
            }

            updateGuiFromConfiguration()
        }

        checkBoxAutoStart.selectedProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            val autoStartManager = EntryClass.autoStartManager ?: return@addListener
            if (newValue)
                autoStartManager.addToAutoStart(AutoStartLaunchConfig(additionalArgs = "--noGui"))
            else
                autoStartManager.removeFromAutoStart()
            updateGuiFromConfiguration()
        }

        checkBoxProfiles.selectedProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            preferences[Keys.Profiles.Enabled] = newValue
            updateGuiFromConfiguration()
        }

        textBoxOverrideProfilesTimeInMinutes.textProperty().addListener { _, _, _ ->
            buttonApplyOverrideForProfiles.isDisable = false
        }

        toggleButtonChargeFull.selectedProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            if (newValue == false) return@addListener

            preferences[Keys.CurrentChargingMode] = AlwaysOn
            Daemon.applyConfiguration()
            updateGuiFromConfiguration()
            SystemTrayManager.updateTrayMenu()
        }

        toggleButtonChargeOptimized.selectedProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            if (newValue == false) return@addListener

            preferences[Keys.CurrentChargingMode] = Optimized
            Daemon.applyConfiguration()
            updateGuiFromConfiguration()
            SystemTrayManager.updateTrayMenu()
        }

        toggleButtonStopCharging.selectedProperty().addListener { _, _, newValue ->
            if (guiUpdateInProgress) return@addListener
            if (newValue == false) return@addListener

            preferences[Keys.CurrentChargingMode] = AlwaysOff
            Daemon.applyConfiguration()
            updateGuiFromConfiguration()
            SystemTrayManager.updateTrayMenu()
        }
    }

    fun updateGuiFromConfiguration() {
        if (guiUpdateInProgress) return
        guiUpdateInProgress = true

        textFieldIFTTTMakerApiKey.text = preferences[Keys.IFTTTMakerApiKey]

        if (preferences.containsKey(Keys.IFTTTStartChargingEventName))
            textFieldStartEvent.text = preferences[Keys.IFTTTStartChargingEventName]
        else {
            textFieldStartEvent.text = ""
            textFieldStartEvent.promptText = preferences[Keys.IFTTTStartChargingEventName]
        }

        if (preferences.containsKey(Keys.IFTTTStopChargingEventName))
            textFieldStopEvent.text = preferences[Keys.IFTTTStopChargingEventName]
        else {
            textFieldStopEvent.text = ""
            textFieldStopEvent.promptText = preferences[Keys.IFTTTStopChargingEventName]
        }

        if (preferences.containsKey(Keys.MinPercentage))
            textFieldMinPercentage.text = preferences[Keys.MinPercentage].toString()
        else {
            textFieldMinPercentage.text = ""
            textFieldMinPercentage.promptText = preferences[Keys.MinPercentage].toString()
        }

        if (preferences.containsKey(Keys.MaxPercentage))
            textFieldMaxPercentage.text = preferences[Keys.MaxPercentage].toString()
        else {
            textFieldMaxPercentage.text = ""
            textFieldMaxPercentage.promptText = preferences[Keys.MaxPercentage].toString()
        }

        val taskSchedulerManager = EntryClass.taskSchedulerManager
        if (taskSchedulerManager == null) {
            checkBoxStopChargingOnShutdown.isSelected = false
            checkBoxStopChargingOnShutdown.isDisable = true
        } else {
            checkBoxStopChargingOnShutdown.isDisable = false
            checkBoxStopChargingOnShutdown.isSelected = taskSchedulerManager.taskExists()
        }

        val autoStartManager = EntryClass.autoStartManager
        if (autoStartManager == null) {
            checkBoxAutoStart.isSelected = false
            checkBoxAutoStart.isDisable = true
        } else {
            checkBoxAutoStart.isDisable = false
            checkBoxAutoStart.isSelected = autoStartManager.isInAutoStart
        }

        val appSpecificSettingsEnabled = preferences[Keys.Profiles.Enabled]
        checkBoxProfiles.isSelected = appSpecificSettingsEnabled
        buttonChangeProfileSettings.isDisable = !appSpecificSettingsEnabled
        hBoxOverrideProfiles.isDisable = !appSpecificSettingsEnabled


        toggleButtonChargeFull.isSelected = preferences[Keys.CurrentChargingMode] == AlwaysOn
        toggleButtonChargeOptimized.isSelected = preferences[Keys.CurrentChargingMode] == Optimized
        toggleButtonStopCharging.isSelected = preferences[Keys.CurrentChargingMode] == AlwaysOff

        guiUpdateInProgress = false
    }

    fun showException(record: LogRecord) {
        if (delayLogUpdatesAndSuppressErrorDialogs) return

        val alertType = when (record.level) {
            Level.CONFIG -> Alert.AlertType.INFORMATION
            Level.INFO -> Alert.AlertType.INFORMATION
            Level.WARNING -> Alert.AlertType.WARNING
            Level.SEVERE -> Alert.AlertType.ERROR
            else -> Alert.AlertType.INFORMATION
        }

        val alert = Alert(alertType)
        alert.title = when (record.level) {
            Level.CONFIG -> "SmartCharge: Configuration message"
            Level.INFO -> "SmartCharge: Information"
            Level.WARNING -> "SmartCharge: Warning"
            Level.SEVERE -> "SmartCharge: Error"
            else -> "SmartCharge: Information"
        }
        alert.headerText = when (record.level) {
            Level.CONFIG -> "Configuration message."
            Level.INFO -> "Information"
            Level.WARNING -> "A warning occurred."
            Level.SEVERE -> "An error occurred."
            else -> "Information"
        }

        val throwable = record.thrown
        val rootCause = if (throwable == null) null else ExceptionUtils.getRootCause(throwable)!!

        val contentTextBuilder = StringBuilder(record.message)
        if (rootCause != null) {
            contentTextBuilder.append("${rootCause.javaClass.name}: ${rootCause.message}")
            val stringWriter = StringWriter()
            stringWriter.write(rootCause.stackTraceToString())

            val label = Label("The stacktrace was:")
            val textArea = TextArea(stringWriter.toString())
            with(textArea) {
                isWrapText = false
                isEditable = false
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
            }
            GridPane.setVgrow(textArea, Priority.ALWAYS)
            GridPane.setHgrow(textArea, Priority.ALWAYS)

            val expandableContent = GridPane()
            with(expandableContent) {
                maxWidth = Double.MAX_VALUE
                add(label, 0, 0)
                add(textArea, 0, 1)
            }

            alert.dialogPane.expandableContent = expandableContent
        }

        alert.contentText = contentTextBuilder.toString()

        alert.show()
    }
}
