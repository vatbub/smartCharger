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
package com.github.vatbub.smartcharge.profiles.matchers

import com.github.vatbub.smartcharge.extensions.parseDisabled
import com.github.vatbub.smartcharge.extensions.type
import com.github.vatbub.smartcharge.profiles.XmlSerializable
import com.github.vatbub.smartcharge.profiles.XmlSerializableCompanion
import org.jdom2.Element
import kotlin.time.ExperimentalTime

interface Matcher<T> : XmlSerializable {
    fun matches(obj: T): Boolean

    @ExperimentalTime
    companion object : XmlSerializableCompanion<Matcher<*>> {
        override fun fromXml(element: Element): Matcher<*> = when (val type = element.type) {
            ApplicationMatcher.type -> ApplicationMatcher.fromXml(element)
            ApplicationStatusMatcher.type -> ApplicationStatusMatcher.fromXml(element)
            IntMatcher.type -> IntMatcher.fromXml(element)
            OptionalStringMatcher.type -> OptionalStringMatcher.fromXml(element)
            StringMatcher.type -> StringMatcher.fromXml(element)
            else -> throw IllegalArgumentException("Matcher type $type unknown")
        }.parseDisabled(element)
    }
}
