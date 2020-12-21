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

import com.github.vatbub.smartcharge.extensions.toList
import org.w3c.dom.Element
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

    companion object : MatcherCompanion<RunningApplication, ApplicationMatcher> {
        override fun fromXml(matcherElement: Element) = with(
            matcherElement
                .childNodes
                .toList()
                .mapNotNull { it as? Element }) {
            ApplicationMatcher(
                imageNameMatcher = StringMatcher.fromXml(this.first { it.nodeName == "imageName" }),
                pidMatcher = IntMatcher.fromXml(this.first { it.nodeName == "pid" }),
                sessionNameMatcher = StringMatcher.fromXml(this.first { it.nodeName == "sessionName" }),
                sessionIdMatcher = IntMatcher.fromXml(this.first { it.nodeName == "sessionId" }),
                memoryUsageMatcher = StringMatcher.fromXml(this.first { it.nodeName == "memoryUsage" }),
                statusMatcher = ApplicationStatusMatcher.fromXml(this.first { it.nodeName == "status" }),
                userNameMatcher = OptionalStringMatcher.fromXml(this.first { it.nodeName == "userName" }),
                windowTitleMatcher = OptionalStringMatcher.fromXml(this.first { it.nodeName == "windowTitle" }),
            )
        }
    }
}
