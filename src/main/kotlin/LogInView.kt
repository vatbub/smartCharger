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

import com.github.vatbub.smartcharge.ifttt.IftttMakerChannel
import com.github.vatbub.smartcharge.util.javafx.bindAndMap
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.stage.Stage
import java.awt.Desktop
import java.net.URI
import java.util.*


class LogInView(
    showGuiOnClassInitialization: Boolean = true,
    private val onApiTokenReceived: (apiKey: String) -> Unit
) {
    @Suppress("MemberVisibilityCanBePrivate")
    val stage = Stage()

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("LogInView.fxml"), null)
        fxmlLoader.setController(this)
        val root = fxmlLoader.load<Parent>()
        stage.icons.add(Image(javaClass.getResourceAsStream("icon.png")))
        stage.minWidth = root.minWidth(0.0) + 70
        stage.minHeight = root.minHeight(0.0) + 70
        stage.scene = Scene(root)

        if (showGuiOnClassInitialization)
            stage.show()
    }

    @FXML
    private lateinit var iftttApiKeyTextField: TextField

    @FXML
    private lateinit var testConnectionButton: Button

    @FXML
    fun cancelOnAction() {
        stage.hide()
    }

    @FXML
    fun testConnectionOnAction() {
        val apiKey = iftttApiKeyTextField.text
        IftttMakerChannel(apiKey).sendEvent("apiKeyTest").throwExceptionIfNecessary()
        onApiTokenReceived(apiKey)
        stage.hide()
        Alert(INFORMATION, "Login was successful.", OK).show()
    }

    @FXML
    fun openIftttInBrowser() {
        Desktop.getDesktop().browse(URI("https://ifttt.com/maker_webhooks"))
    }

    @FXML
    fun initialize() {
        testConnectionButton.disableProperty()
            .bindAndMap(iftttApiKeyTextField.textProperty()) { it.isEmpty() }
        iftttApiKeyTextField.text = preferences[Keys.IFTTTMakerApiKey]
    }
}
