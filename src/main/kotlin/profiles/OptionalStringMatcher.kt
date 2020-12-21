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

sealed class OptionalStringMatcher : Matcher<String?> {
    companion object : MatcherCompanion<String?, OptionalStringMatcher> {
        override fun fromXml(matcherElement: Element): OptionalStringMatcher {
            val requirement = matcherElement.getAttribute("requirement")
            return when (val subtype = matcherElement.getAttribute("subtype")) {
                "Equals" -> OptionalEqualsStringMatcher(requirement)
                "Regex" -> OptionalRegexStringMatcher(requirement.toRegex())
                else -> throw IllegalArgumentException("OptionalStringMatcher subtype $subtype unknown")
            }
        }
    }
}

data class OptionalEqualsStringMatcher(val requirement: String) : OptionalStringMatcher() {
    override fun matches(obj: String?): Boolean = obj == requirement
}

data class OptionalRegexStringMatcher(val requirement: Regex) : OptionalStringMatcher() {
    override fun matches(obj: String?): Boolean = obj?.matches(requirement) ?: false
}
