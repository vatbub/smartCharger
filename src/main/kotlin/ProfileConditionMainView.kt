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

import com.github.vatbub.smartcharge.ConditionType.Application
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage


class ProfileConditionMainView {
    companion object {
        fun show(): ProfileConditionMainView {
            val fxmlLoader =
                FXMLLoader(ProfileConditionMainView::class.java.getResource("ProfileConditionMainView.fxml"), null)
            val root = fxmlLoader.load<Parent>()
            val conditionMainView: ProfileConditionMainView = fxmlLoader.getController()

            val stage = Stage()
            val scene = Scene(root)
            stage.title = "SmartCharger ConditionEditor"
            stage.icons.add(Image(ProfileConditionMainView::class.java.getResourceAsStream("icon.png")))

            stage.minWidth = root.minWidth(0.0) + 70
            stage.minHeight = root.minHeight(0.0) + 70
            stage.scene = scene

            stage.show()
            return conditionMainView
        }
    }

    @FXML
    private lateinit var comboBoxConditionType: ComboBox<ConditionType>

    @FXML
    private lateinit var anchorPaneContent: AnchorPane

    private var currentController: ProfileConditionViewController? = null

    @Suppress("SENSELESS_COMPARISON")
    @FXML
    fun initialize() {
        assert(comboBoxConditionType != null) { "fx:id=\"comboBoxConditionType\" was not injected: check your FXML file 'ProfileConditionMainView.fxml'." }
        assert(anchorPaneContent != null) { "fx:id=\"anchorPaneContent\" was not injected: check your FXML file 'ProfileConditionMainView.fxml'." }

        comboBoxConditionType.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            val fxmlName = when (newValue) {
                Application -> "ApplicationConditionView.fxml"
                else -> {
                    anchorPaneContent.children.clear()
                    currentController = null
                    return@addListener
                }
            }

            val fxmlLoader = FXMLLoader(ProfileConditionMainView::class.java.getResource(fxmlName), null)
            val root = fxmlLoader.load<Parent>()
            currentController = fxmlLoader.getController()

            anchorPaneContent.children.clear()
            anchorPaneContent.children.add(root)
        }

        comboBoxConditionType.items = FXCollections.observableArrayList(*ConditionType.values())
    }
}

private enum class ConditionType(val stringRepresentation: String) {
    Application("Application");

    override fun toString(): String = stringRepresentation
}

abstract class ProfileConditionViewController
