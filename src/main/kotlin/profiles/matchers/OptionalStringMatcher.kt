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
import com.github.vatbub.smartcharge.extensions.subtype
import com.github.vatbub.smartcharge.profiles.*
import org.jdom2.Element


sealed class OptionalStringMatcher : Matcher<String?> {
    companion object : XmlSerializableCompanion<OptionalStringMatcher>, TypedXmlObjectCompanion {
        override val type: String = "OptionalStringMatcher"

        private const val nullString = "null"

        override fun fromXml(element: Element): OptionalStringMatcher {
            var requirement: String? = element.requirement.value
            if (requirement == nullString)
                requirement = null
            return when (val subtype = element.subtype) {
                EqualsMatcher.subtype -> EqualsMatcher(requirement)
                RegexMatcher.subtype -> RegexMatcher(requirement?.toRegex())
                else -> throw IllegalArgumentException("OptionalStringMatcher subtype $subtype unknown")
            }
        }
    }

    class EqualsMatcher(private val requirement: String?) : OptionalStringMatcher() {
        override fun matches(obj: String?): Boolean = obj == requirement

        override fun toXml() = matcherElement {
            it.requirementAttribute(requirement ?: nullString)
        }

        override fun toString(): String = requirement ?: nullString

        companion object : OptionalStringMatcherCompanion() {
            override val subtype: String = "Equals"
        }
    }

    class RegexMatcher(private val requirement: Regex?) : OptionalStringMatcher() {
        override fun matches(obj: String?): Boolean {
            // If both are null return true
            if (requirement == null && obj == null) return true
            // but if only one of them is null return false
            if (requirement == null || obj == null) return false

            // None of them is null
            return obj.matches(requirement)
        }

        override fun toXml() = matcherElement {
            it.requirementAttribute(requirement?.pattern ?: nullString)
        }

        override fun toString(): String = requirement?.pattern?.let { "matches $it" } ?: nullString

        companion object : OptionalStringMatcherCompanion() {
            override val subtype: String = "Regex"
        }
    }

    abstract class OptionalStringMatcherCompanion : TypedXmlObjectWithSubtypeCompanion {
        override val type: String = OptionalStringMatcher.type
    }
}
