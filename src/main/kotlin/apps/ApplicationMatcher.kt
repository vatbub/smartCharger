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
package com.github.vatbub.smartcharge.apps

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
}
