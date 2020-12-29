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
package com.github.vatbub.smartcharge.util.javafx

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.TableCell

class ButtonCell<S, T>(
    private val buttonTextFactory: (T) -> String,
    private val onAction: (T, ActionEvent) -> Unit
) : TableCell<S, T>() {
    constructor(buttonText: String, onAction: (T, ActionEvent) -> Unit) :
            this({ buttonText }, onAction)

    override fun updateItem(item: T?, empty: Boolean) {
        if (empty || item == null) {
            graphic = null
            return
        }

        graphic = Button(buttonTextFactory(item)).apply {
            setOnAction { event ->
                onAction(item, event)
            }
            maxWidth = Double.POSITIVE_INFINITY
        }
    }
}
