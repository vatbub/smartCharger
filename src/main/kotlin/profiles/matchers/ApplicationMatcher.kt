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

import com.github.vatbub.smartcharge.extensions.*
import com.github.vatbub.smartcharge.profiles.*
import org.jdom2.Element
import kotlin.time.ExperimentalTime

@ExperimentalTime
data class ApplicationMatcher(
    val imageNameMatcher: Matcher<String>,
    val pidMatcher: Matcher<Int>,
    val sessionNameMatcher: Matcher<String>,
    val sessionIdMatcher: Matcher<Int>,
    val memoryUsageMatcher: Matcher<String>,
    val statusMatcher: Matcher<ApplicationStatus>,
    val userNameMatcher: Matcher<String?>,
    val windowTitleMatcher: Matcher<String?>
) : Matcher<RunningApplication> {
    override fun matches(obj: RunningApplication): Boolean =
        imageNameMatcher.matches(obj.imageName) &&
                pidMatcher.matches(obj.pid) &&
                sessionNameMatcher.matches(obj.sessionName) &&
                sessionIdMatcher.matches(obj.sessionId) &&
                memoryUsageMatcher.matches(obj.memoryUsage) &&
                statusMatcher.matches(obj.status) &&
                userNameMatcher.matches(obj.userName) &&
                windowTitleMatcher.matches(obj.windowTitle)

    override fun toXml(): Element =
        matcherElement {
            it.children.addAll(
                listOf(
                    imageNameMatcher.toXml().apply { name = "imageName" },
                    pidMatcher.toXml().apply { name = "pid" },
                    sessionNameMatcher.toXml().apply { name = "sessionName" },
                    sessionIdMatcher.toXml().apply { name = "sessionId" },
                    memoryUsageMatcher.toXml().apply { name = "memoryUsage" },
                    statusMatcher.toXml().apply { name = "status" },
                    userNameMatcher.toXml().apply { name = "userName" },
                    windowTitleMatcher.toXml().apply { name = "windowTitle" }
                )
            )
        }

    override fun toString(): String =
        mapOf(
            "imageName" to imageNameMatcher,
            "pid" to pidMatcher,
            "sessionName" to sessionNameMatcher,
            "sessionId" to sessionIdMatcher,
            "memoryUsage" to memoryUsageMatcher,
            "status" to statusMatcher,
            "userName" to userNameMatcher,
            "windowTitle" to windowTitleMatcher
        ).filterNot { it.value is DisabledMatcher<*> }
            .map { "${it.key}: ${it.value}" }
            .joinToString(", ")

    companion object : XmlSerializableCompanion<ApplicationMatcher>, TypedXmlObjectCompanion {
        override val type: String = "ApplicationMatcher"

        override fun fromXml(element: Element) =
            ApplicationMatcher(
                imageNameMatcher = StringMatcher.fromXml(element.imageName).parseDisabled(element.imageName),
                pidMatcher = IntMatcher.fromXml(element.pid).parseDisabled(element.pid),
                sessionNameMatcher = StringMatcher.fromXml(element.sessionName).parseDisabled(element.sessionName),
                sessionIdMatcher = IntMatcher.fromXml(element.sessionId).parseDisabled(element.sessionId),
                memoryUsageMatcher = StringMatcher.fromXml(element.memoryUsage).parseDisabled(element.memoryUsage),
                statusMatcher = ApplicationStatusMatcher.fromXml(element.status).parseDisabled(element.status),
                userNameMatcher = OptionalStringMatcher.fromXml(element.userName).parseDisabled(element.userName),
                windowTitleMatcher = OptionalStringMatcher.fromXml(element.windowTitle)
                    .parseDisabled(element.windowTitle)
            )
    }
}
