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
import com.github.vatbub.smartcharge.extensions.profiles
import com.github.vatbub.smartcharge.preferences
import com.github.vatbub.smartcharge.profiles.conditions.ProfileCondition
import com.github.vatbub.smartcharge.util.ObservableList
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

@OptIn(ExperimentalTime::class)
@Suppress("UNUSED_PARAMETER")
object ProfileManager {
    var enabled: Boolean
        get() = preferences[Keys.Profiles.Enabled]
        set(value) {
            preferences[Keys.Profiles.Enabled] = value
        }

    val profiles: ObservableList<Profile> by lazy {
        ObservableList(readXml(), this::onAdd, this::onSet, this::onRemove, this::onClear)
    }

    var overriddenUntil: TimeMark? = null
        private set
    val isOverridden: Boolean
        get() = overriddenUntil?.hasNotPassedNow() == true

    fun overrideProfilesUntil(time: TimeMark) {
        overriddenUntil = time
    }

    fun getActiveProfile(): Profile? = profiles
        .sortedByDescending(Profile::priority)
        .firstOrNull { it.condition.isActive() }

    private val atomicNextProfileId by lazy {
        val currentMax = profiles.maxOfOrNull { it.id } ?: 0
        AtomicLong(currentMax + 1)
    }

    fun createNewProfile(condition: ProfileCondition, priority: Int, chargingMode: ChargingMode): Profile =
        Profile(atomicNextProfileId.getAndIncrement(), condition, priority, chargingMode)
            .also { profiles.add(it) }

    private fun readXml(): List<Profile> {
        val xmlFile = preferences[Keys.Profiles.XmlFileLocation]
        if (!xmlFile.exists()) return listOf()

        return SAXBuilder()
            .build(xmlFile)
            .rootElement
            .profiles
            .map { Profile.fromXml(it) }
    }

    private fun saveXml() {
        val document = Document(
            Element("profileManager").apply {
                children.addAll(
                    ProfileManager.profiles
                        .map { it.toXml() }
                )
            }
        )

        val xmlOutputter = XMLOutputter(Format.getPrettyFormat())

        FileWriter(preferences[Keys.Profiles.XmlFileLocation]).use { fileWriter ->
            xmlOutputter.output(document, fileWriter)
        }
    }

    private fun onAdd(index: Int?, element: Profile) {
        saveXml()
    }

    private fun onSet(index: Int, previousValue: Profile, newValue: Profile) {
        saveXml()
    }

    private fun onRemove(element: Profile) {
        saveXml()
    }

    private fun onClear() {
        saveXml()
    }
}
