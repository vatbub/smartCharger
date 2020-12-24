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

import com.github.vatbub.smartcharge.extensions.source
import com.github.vatbub.smartcharge.logging.logger
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.concurrent.Worker.State.RUNNING
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.Image
import javafx.scene.web.WebView
import javafx.stage.Stage
import java.net.URL
import java.util.*
import java.util.regex.Pattern
import kotlin.time.ExperimentalTime


@ExperimentalTime
class LogInView(private val startUrl: String, private val loadStartUrlAgainIfThisUrlIsLoaded: String? = null, showGuiOnClassInitialization: Boolean = true, private val onApiTokenReceived: (apiKey: String) -> Boolean) {
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
    private lateinit var resources: ResourceBundle

    @FXML
    private lateinit var location: URL

    @FXML
    private lateinit var urlLabel: Label

    @FXML
    private lateinit var webView: WebView

    @FXML
    private lateinit var progressBar: ProgressBar

    @Suppress("SENSELESS_COMPARISON")
    @FXML
    fun initialize() {
        assert(urlLabel != null) { "fx:id=\"urlLabel\" was not injected: check your FXML file 'LogInView.fxml'." }
        assert(webView != null) { "fx:id=\"webView\" was not injected: check your FXML file 'LogInView.fxml'." }
        assert(progressBar != null) { "fx:id=\"progressBar\" was not injected: check your FXML file 'LogInView.fxml'." }

        urlLabel.textProperty().bind(webView.engine.locationProperty())
        stage.titleProperty().bind(webView.engine.titleProperty())
        progressBar.progressProperty().bind(webView.engine.loadWorker.progressProperty())
        webView.engine.loadWorker.stateProperty().addListener { _, _, newValue ->
            progressBar.isVisible = when (newValue) {
                RUNNING -> true
                else -> false
            }
        }

        webView.engine.locationProperty().addListener { _, _, newValue ->
            logger.debug("WebView is browsing to $newValue")
            if (loadStartUrlAgainIfThisUrlIsLoaded != null && newValue == loadStartUrlAgainIfThisUrlIsLoaded)
                Platform.runLater { webView.engine.load(startUrl) }
        }

        webView.engine.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState === Worker.State.SUCCEEDED && webView.engine.location == startUrl) {
                getApiKeyFromHtmlSource(webView.engine.document.source)
            }
        }

        webView.engine.load(startUrl)
    }

    private fun getApiKeyFromHtmlSource(source: String) {
        val pattern = Pattern.compile("<SPAN>https://maker\\.ifttt\\.com/use/.*</SPAN>", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(source)
        if (!matcher.find()) {
            logger.error("We were expecting to find the IFTTT Api Key on this website, but something went wrong. Please report this to the developers!")
            stage.hide()
            return
        }

        var matchingResult = matcher.group(0)
        matchingResult = matchingResult.removeRange(0, 6)
        matchingResult = matchingResult.removeRange(matchingResult.length - 7, matchingResult.length)
        val apiKey = matchingResult.removePrefix("https://maker.ifttt.com/use/")
        val closeForm = onApiTokenReceived(apiKey)
        if (closeForm)
            stage.hide()
    }
}
