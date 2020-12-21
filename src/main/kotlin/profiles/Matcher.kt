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

import org.w3c.dom.Element
import kotlin.time.ExperimentalTime

interface Matcher<T> {
    fun matches(obj: T): Boolean

    @ExperimentalTime
    companion object {
        fun fromXml(matcherElement: Element): Matcher<*> =
            when (val type = matcherElement.getAttribute("type")) {
                "Application" -> ApplicationMatcher.fromXml(matcherElement)
                "ApplicationStatus" -> ApplicationStatusMatcher.fromXml(matcherElement)
                "Int" -> IntMatcher.fromXml(matcherElement)
                "OptionalString" -> OptionalStringMatcher.fromXml(matcherElement)
                "String" -> StringMatcher.fromXml(matcherElement)
                else -> throw IllegalArgumentException("Matcher type $type unknown")
            }
    }
}
