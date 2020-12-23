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
import com.github.vatbub.smartcharge.extensions.chargingMode
import com.github.vatbub.smartcharge.extensions.condition
import com.github.vatbub.smartcharge.extensions.id
import com.github.vatbub.smartcharge.profiles.conditions.ProfileCondition
import org.jdom2.Attribute
import org.jdom2.Element
import kotlin.time.ExperimentalTime

@ExperimentalTime
data class Profile(val id: Long, val condition: ProfileCondition, val chargingMode: ChargingMode) : XmlSerializable {
    override fun toXml(): Element = profileElement {
        it.attributes.add(Attribute("id", id.toString()))
        it.attributes.add(Attribute("chargingMode", chargingMode.toString()))
        it.children.add(condition.toXml())
    }

    companion object : XmlSerializableCompanion<Profile> {
        override fun fromXml(element: Element) = Profile(
            id = element.id,
            condition = ProfileCondition.fromXml(element.condition),
            chargingMode = element.chargingMode
        )
    }
}
