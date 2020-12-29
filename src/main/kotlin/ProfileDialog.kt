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

import com.github.vatbub.smartcharge.extensions.toFxList
import com.github.vatbub.smartcharge.profiles.Profile
import com.github.vatbub.smartcharge.profiles.ProfileManager
import com.github.vatbub.smartcharge.profiles.conditions.ApplicationCondition
import com.github.vatbub.smartcharge.profiles.conditions.ProfileCondition
import com.github.vatbub.smartcharge.profiles.matchers.*
import com.github.vatbub.smartcharge.util.javafx.ButtonCell
import com.github.vatbub.smartcharge.util.javafx.IntStringConverter
import com.github.vatbub.smartcharge.util.javafx.SimpleCellValueFactory
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.util.Callback
import kotlin.time.ExperimentalTime


@ExperimentalTime
class ProfileDialog {
    companion object {
        fun show(): ProfileDialog {
            val fxmlLoader = FXMLLoader(ProfileDialog::class.java.getResource("ProfileDialog.fxml"), null)
            val root = fxmlLoader.load<Parent>()
            val profileDialog: ProfileDialog = fxmlLoader.getController()

            val stage = Stage()
            val scene = Scene(root)
            stage.title = "SmartCharger Profiles"
            stage.icons.add(Image(ProfileDialog::class.java.getResourceAsStream("icon.png")))

            stage.minWidth = root.minWidth(0.0) + 70
            stage.minHeight = root.minHeight(0.0) + 70
            stage.scene = scene

            stage.show()
            return profileDialog
        }
    }

    @FXML
    private lateinit var tableViewApplicationList: TableView<Profile>

    @FXML
    private lateinit var columnCondition: TableColumn<Profile, ProfileCondition>

    @FXML
    private lateinit var columnMode: TableColumn<Profile, ChargingMode>

    @FXML
    private lateinit var columnPriority: TableColumn<Profile, Int>

    @FXML
    private lateinit var columnEdit: TableColumn<Profile, Profile>

    @FXML
    private lateinit var deleteColumn: TableColumn<Profile, Profile>

    @FXML
    fun buttonAdd(event: ActionEvent?) {
        ProfileConditionMainView.show()
        ProfileManager.createNewProfile(
            condition = ApplicationCondition(
                matcher = ApplicationMatcher(
                    imageNameMatcher = StringMatcher.EqualsMatcher("chrome.exe")
                )
            ),
            priority = 0,
            chargingMode = ChargingMode.AlwaysOn
        )

        refreshView()
    }

    @Suppress("SENSELESS_COMPARISON")
    @FXML
    fun initialize() {
        assert(tableViewApplicationList != null) { "fx:id=\"tableViewApplicationList\" was not injected: check your FXML file 'ProfileDialog.fxml'." }
        assert(columnCondition != null) { "fx:id=\"columnCondition\" was not injected: check your FXML file 'ProfileDialog.fxml'." }
        assert(columnMode != null) { "fx:id=\"columnMode\" was not injected: check your FXML file 'ProfileDialog.fxml'." }
        assert(columnPriority != null) { "fx:id=\"columnPriority\" was not injected: check your FXML file 'ProfileDialog.fxml'." }
        assert(columnEdit != null) { "fx:id=\"columnEdit\" was not injected: check your FXML file 'ProfileDialog.fxml'." }
        assert(deleteColumn != null) { "fx:id=\"deleteColumn\" was not injected: check your FXML file 'ProfileDialog.fxml'." }

        columnCondition.cellValueFactory = PropertyValueFactory(Profile::condition.name)
        columnMode.cellValueFactory = PropertyValueFactory(Profile::chargingMode.name)
        columnPriority.cellValueFactory = PropertyValueFactory(Profile::priority.name)
        columnEdit.cellValueFactory = SimpleCellValueFactory { this }
        deleteColumn.cellValueFactory = SimpleCellValueFactory { this }

        columnMode.cellFactory = ComboBoxTableCell.forTableColumn(*ChargingMode.values())
        columnMode.onEditCommit = EventHandler<TableColumn.CellEditEvent<Profile, ChargingMode>> { event ->
            val index = ProfileManager.profiles.indexOf(event.rowValue)
            ProfileManager.profiles[index] = event.rowValue.copy(chargingMode = event.newValue)
        }
        columnPriority.cellFactory = TextFieldTableCell.forTableColumn(IntStringConverter())
        columnPriority.onEditCommit = EventHandler<TableColumn.CellEditEvent<Profile, Int>> { event ->
            val index = ProfileManager.profiles.indexOf(event.rowValue)
            ProfileManager.profiles[index] = event.rowValue.copy(priority = event.newValue)
        }
        columnEdit.cellFactory = editButtonCellFactory
        deleteColumn.cellFactory = deleteButtonCellFactory

        refreshView()
    }

    private fun refreshView() {
        tableViewApplicationList.items = ProfileManager.profiles.toFxList()
    }

    private val editButtonCellFactory by lazy {
        Callback<TableColumn<Profile, Profile>, TableCell<Profile, Profile>> { column ->
            ButtonCell("Edit") { item, event -> println("Edit pressed, profile id: ${item.id}") }
        }
    }

    private val deleteButtonCellFactory by lazy {
        Callback<TableColumn<Profile, Profile>, TableCell<Profile, Profile>> {
            ButtonCell("Delete") { item, _ ->
                val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
                    title = "Confirm profile deletion"
                    contentText = "Are you sure that this profile shall be permanently deleted?"
                    buttonTypes.setAll(
                        ButtonType("Yes", ButtonData.YES),
                        ButtonType("No", ButtonData.NO)
                    )
                }
                alert.showAndWait().ifPresent { type ->
                    if (type.buttonData != ButtonData.YES) return@ifPresent
                    ProfileManager.profiles.remove(item)
                    refreshView()
                }
            }
        }
    }
}

