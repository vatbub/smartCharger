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

import com.github.vatbub.smartcharge.profiles.ApplicationMatcher
import com.github.vatbub.smartcharge.profiles.Profile
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlin.time.ExperimentalTime


@ExperimentalTime
class AppSpecificSettingsDialog {
    companion object {
        fun show() {

        }
    }

    @FXML
    private lateinit var tableViewApplicationList: TableView<Profile>

    @FXML
    private lateinit var columnApplication: TableColumn<Profile, ApplicationMatcher>

    @FXML
    private lateinit var columnMode: TableColumn<Profile, ChargingMode>

    @FXML
    private lateinit var columnEdit: TableColumn<Profile, Void>

    @FXML
    private lateinit var deleteColumn: TableColumn<Profile, Void>

    @FXML
    fun buttonAdd(event: ActionEvent?) {
    }

    @Suppress("SENSELESS_COMPARISON")
    @FXML
    fun initialize() {
        assert(columnApplication != null) { "fx:id=\"columnApplication\" was not injected: check your FXML file 'AppSpecificSettingsDialog.fxml'." }
        assert(tableViewApplicationList != null) { "fx:id=\"tableViewApplicationList\" was not injected: check your FXML file 'AppSpecificSettingsDialog.fxml'." }
        assert(columnMode != null) { "fx:id=\"columnMode\" was not injected: check your FXML file 'AppSpecificSettingsDialog.fxml'." }
        assert(columnEdit != null) { "fx:id=\"columnEdit\" was not injected: check your FXML file 'AppSpecificSettingsDialog.fxml'." }
        assert(deleteColumn != null) { "fx:id=\"deleteColumn\" was not injected: check your FXML file 'AppSpecificSettingsDialog.fxml'." }
    }
}
