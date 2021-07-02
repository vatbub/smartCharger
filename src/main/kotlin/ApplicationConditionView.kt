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

import com.github.vatbub.smartcharge.profiles.ApplicationStatus
import com.github.vatbub.smartcharge.profiles.RunningApplication
import com.github.vatbub.smartcharge.profiles.conditions.ApplicationCondition
import com.github.vatbub.smartcharge.profiles.getRunningApps
import com.github.vatbub.smartcharge.profiles.matchers.ApplicationMatcher
import com.github.vatbub.smartcharge.util.javafx.bindAndMap
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class ApplicationConditionView : ProfileConditionViewController<ApplicationCondition>() {
    @FXML
    private lateinit var comboBoxModeImageName: ComboBox<StringMatcherMode>

    @FXML
    private lateinit var comboBoxModePid: ComboBox<IntMatcherMode>

    @FXML
    private lateinit var comboBoxModeSessionName: ComboBox<StringMatcherMode>

    @FXML
    private lateinit var comboBoxModeSessionId: ComboBox<IntMatcherMode>

    @FXML
    private lateinit var comboBoxModeMemoryUsage: ComboBox<IntMatcherMode>

    @FXML
    private lateinit var comboBoxModeStatus: ComboBox<ApplicationStatusMatcherMode>

    @FXML
    private lateinit var comboBoxModeUserName: ComboBox<OptionalStringMatcherMode>

    @FXML
    private lateinit var comboBoxModeWindowTitle: ComboBox<OptionalStringMatcherMode>

    @FXML
    private lateinit var comboBoxValueImageName: ComboBox<String>

    @FXML
    private lateinit var textFieldValuePid: TextField

    @FXML
    private lateinit var textFieldValueSessionName: TextField

    @FXML
    private lateinit var textFieldValueSessionId: TextField

    @FXML
    private lateinit var textFieldValueMemoryUsage: TextField

    @FXML
    private lateinit var comboBoxValueStatus: ComboBox<ApplicationStatus>

    @FXML
    private lateinit var textFieldValueUserName: TextField

    @FXML
    private lateinit var textFieldValueWindowTitle: TextField

    @OptIn(ExperimentalTime::class)
    @Suppress("SENSELESS_COMPARISON")
    @FXML
    fun initialize() {
        assert(comboBoxModeSessionName != null) { "fx:id=\"comboBoxModeSessionName\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(textFieldValueWindowTitle != null) { "fx:id=\"textFieldValueWindowTitle\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModeUserName != null) { "fx:id=\"comboBoxModeUserName\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxValueStatus != null) { "fx:id=\"comboBoxValueStatus\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(textFieldValuePid != null) { "fx:id=\"textFieldValuePid\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModeStatus != null) { "fx:id=\"comboBoxModeStatus\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModeImageName != null) { "fx:id=\"comboBoxModeImageName\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModeSessionId != null) { "fx:id=\"comboBoxModeSessionId\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModePid != null) { "fx:id=\"comboBoxModePid\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxValueImageName != null) { "fx:id=\"comboBoxValueImageName\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModeWindowTitle != null) { "fx:id=\"comboBoxModeWindowTitle\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(textFieldValueSessionId != null) { "fx:id=\"textFieldValueSessionId\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(textFieldValueSessionName != null) { "fx:id=\"textFieldValueSessionName\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(textFieldValueMemoryUsage != null) { "fx:id=\"textFieldValueMemoryUsage\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(textFieldValueUserName != null) { "fx:id=\"textFieldValueUserName\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }
        assert(comboBoxModeMemoryUsage != null) { "fx:id=\"comboBoxModeMemoryUsage\" was not injected: check your FXML file 'ApplicationConditionView.fxml'." }

        comboBoxModeImageName.items = FXCollections.observableArrayList(*StringMatcherMode.values())
        comboBoxModePid.items = FXCollections.observableArrayList(*IntMatcherMode.values())
        comboBoxModeSessionName.items = FXCollections.observableArrayList(*StringMatcherMode.values())
        comboBoxModeSessionId.items = FXCollections.observableArrayList(*IntMatcherMode.values())
        comboBoxModeMemoryUsage.items = FXCollections.observableArrayList(*IntMatcherMode.values())
        comboBoxModeStatus.items = FXCollections.observableArrayList(*ApplicationStatusMatcherMode.values())
        comboBoxModeUserName.items = FXCollections.observableArrayList(*OptionalStringMatcherMode.values())
        comboBoxModeWindowTitle.items = FXCollections.observableArrayList(*OptionalStringMatcherMode.values())


        comboBoxValueImageName.disableProperty()
            .bindAndMap(comboBoxModeImageName.selectionModel.selectedItemProperty()) { it == StringMatcherMode.Disabled }
        textFieldValuePid.disableProperty()
            .bindAndMap(comboBoxModePid.selectionModel.selectedItemProperty()) { it == IntMatcherMode.Disabled }
        textFieldValueSessionName.disableProperty()
            .bindAndMap(comboBoxModeSessionName.selectionModel.selectedItemProperty()) { it == StringMatcherMode.Disabled }
        textFieldValueSessionId.disableProperty()
            .bindAndMap(comboBoxModeSessionId.selectionModel.selectedItemProperty()) { it == IntMatcherMode.Disabled }
        textFieldValueMemoryUsage.disableProperty()
            .bindAndMap(comboBoxModeMemoryUsage.selectionModel.selectedItemProperty()) { it == IntMatcherMode.Disabled }
        comboBoxValueStatus.disableProperty()
            .bindAndMap(comboBoxModeStatus.selectionModel.selectedItemProperty()) { it == ApplicationStatusMatcherMode.Disabled }
        textFieldValueUserName.disableProperty()
            .bindAndMap(comboBoxModeUserName.selectionModel.selectedItemProperty()) { it == OptionalStringMatcherMode.Disabled }
        textFieldValueWindowTitle.disableProperty()
            .bindAndMap(comboBoxModeWindowTitle.selectionModel.selectedItemProperty()) { it == OptionalStringMatcherMode.Disabled }

        comboBoxValueImageName.items = FXCollections.observableArrayList(RunningApplication.getRunningApps().map(RunningApplication::imageName))
        comboBoxValueStatus.items = FXCollections.observableArrayList(*ApplicationStatus.values())

        textFieldValuePid
        textFieldValueSessionName
        textFieldValueSessionId
        textFieldValueMemoryUsage
        textFieldValueUserName
        textFieldValueWindowTitle
    }

    override fun onConditionSet(newCondition: ApplicationCondition) {
        val matcher = newCondition.matcher as ApplicationMatcher

        comboBoxModeImageName.selectionModel.select(matcher.imageNameMatcher)
        comboBoxModePid.selectionModel.select(matcher.pidMatcher)
        comboBoxModeSessionName.selectionModel.select(matcher.sessionNameMatcher)
        comboBoxModeSessionId.selectionModel.select(matcher.sessionIdMatcher)
        comboBoxModeMemoryUsage.selectionModel.select(matcher.memoryUsageMatcher)
        comboBoxModeStatus.selectionModel.select(matcher.statusMatcher)
        comboBoxModeUserName.selectionModel.select(matcher.userNameMatcher)
        comboBoxModeWindowTitle.selectionModel.select(matcher.windowTitleMatcher)
    }
}

private enum class ApplicationStatusMatcherMode(private val stringRepresentation: String) {
    Equals("equals"),
    Disabled("Ignore");

    override fun toString(): String = stringRepresentation
}
