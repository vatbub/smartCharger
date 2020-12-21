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
package com.github.vatbub.smartcharge.profiles

import com.github.vatbub.smartcharge.ChargingMode
import com.github.vatbub.smartcharge.Keys
import com.github.vatbub.smartcharge.extensions.toList
import com.github.vatbub.smartcharge.preferences
import com.github.vatbub.smartcharge.util.ObservableList
import com.jcabi.xml.XMLDocument
import org.w3c.dom.Element
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ProfileManager {
    var enabled: Boolean
        get() = preferences[Keys.Profiles.Enabled]
        set(value) {
            preferences[Keys.Profiles.Enabled] = value
        }

    val profiles: ObservableList<Profile> by lazy {
        ObservableList(readXml(), this::onAdd, this::onSet, this::onRemove, this::onClear)
    }

    private fun readXml(): List<Profile> {
        val xmlDocument = XMLDocument(preferences[Keys.Profiles.XmlFileLocation])
        return xmlDocument
            .node()
            .childNodes
            .toList()
            .mapNotNull { it as? Element }
            .filter { it.nodeName == "profile" }
            .map { profileElement ->
                val matcherNode = profileElement
                    .childNodes
                    .toList()
                    .mapNotNull { it as? Element }
                    .first { it.nodeName == "matcher" }

                Profile(
                    id = profileElement.getAttribute("id").toLong(),
                    matcher = Matcher.fromXml(matcherNode),
                    chargingMode = ChargingMode.valueOf(profileElement.getAttribute("chargingMode"))
                )
            }
    }

    private fun onAdd(index: Int?, element: Profile) {

    }

    private fun onSet(index: Int, previousValue: Profile, newValue: Profile) {

    }

    private fun onRemove(element: Profile) {

    }

    private fun onClear() {

    }
}
