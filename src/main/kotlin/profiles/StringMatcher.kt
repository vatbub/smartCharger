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

sealed class StringMatcher : Matcher<String> {
    companion object : MatcherCompanion<String, StringMatcher> {
        override fun fromXml(matcherElement: Element): StringMatcher {
            val requirement = matcherElement.getAttribute("requirement")
            return when (val subtype = matcherElement.getAttribute("subtype")) {
                "Equals" -> EqualsStringMatcher(requirement)
                "Regex" -> RegexStringMatcher(requirement.toRegex())
                else -> throw IllegalArgumentException("StringMatcher subtype $subtype unknown")
            }
        }
    }
}

data class EqualsStringMatcher(val requirement: String) : StringMatcher() {
    override fun matches(obj: String): Boolean = obj == requirement
}

data class RegexStringMatcher(val requirement: Regex) : StringMatcher() {
    override fun matches(obj: String): Boolean = obj.matches(requirement)
}
