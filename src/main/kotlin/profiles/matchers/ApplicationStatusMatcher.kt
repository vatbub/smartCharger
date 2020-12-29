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

import com.github.vatbub.smartcharge.extensions.requirement
import com.github.vatbub.smartcharge.profiles.*
import org.jdom2.Element


data class ApplicationStatusMatcher(val requirement: ApplicationStatus = ApplicationStatus.Running) :
    Matcher<ApplicationStatus> {
    override fun matches(obj: ApplicationStatus): Boolean = obj == requirement

    override fun toXml(): Element =
        matcherElement {
            it.requirementAttribute(requirement.toString())
        }

    override fun toString(): String = requirement.toString()

    companion object : XmlSerializableCompanion<ApplicationStatusMatcher>, TypedXmlObjectCompanion {
        override val type: String = "ApplicationStatusMatcher"

        override fun fromXml(element: Element) = ApplicationStatusMatcher(
            ApplicationStatus.valueOf(element.requirement.value)
        )
    }
}
