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
package com.github.vatbub.smartcharge.profiles.conditions

import com.github.vatbub.smartcharge.extensions.matcher
import com.github.vatbub.smartcharge.profiles.*
import com.github.vatbub.smartcharge.profiles.matchers.ApplicationMatcher
import com.github.vatbub.smartcharge.profiles.matchers.Matcher
import org.jdom2.Element
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationCondition(private val matcher: Matcher<RunningApplication> = ApplicationMatcher()) : ProfileCondition {
    override fun isActive(): Boolean =
        RunningApplication
            .getRunningApps()
            .firstOrNull { matcher.matches(it) } != null

    override fun toXml(): Element =
        conditionElement {
            it.children.add(matcher.toXml())
        }

    override fun toString(): String = matcher.toString()

    companion object : XmlSerializableCompanion<ApplicationCondition>, TypedXmlObjectCompanion {
        override val type: String = "ApplicationCondition"

        @Suppress("UNCHECKED_CAST")
        override fun fromXml(element: Element): ApplicationCondition =
            ApplicationCondition(
                matcher = Matcher.fromXml(element.matcher) as Matcher<RunningApplication>
            )
    }
}
