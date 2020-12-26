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


sealed class StringMatcher : Matcher<String> {
    companion object : XmlSerializableCompanion<StringMatcher>, TypedXmlObjectCompanion {
        override val type: String = "StringMatcher"

        override fun fromXml(element: Element): StringMatcher {
            val requirement = element.requirement.value
            return when (val subtype = element.subtype) {
                EqualsMatcher.subtype -> EqualsMatcher(requirement)
                RegexMatcher.subtype -> RegexMatcher(requirement.toRegex())
                else -> throw IllegalArgumentException("StringMatcher subtype $subtype unknown")
            }
        }
    }

    class EqualsMatcher(private val requirement: String) : StringMatcher() {
        override fun matches(obj: String): Boolean = obj == requirement

        override fun toXml() = matcherElement {
            it.requirementAttribute(requirement)
        }

        override fun toString(): String = requirement

        companion object : StringMatcherCompanion() {
            override val subtype: String = "Equals"
        }
    }

    class RegexMatcher(private val requirement: Regex) : StringMatcher() {
        override fun matches(obj: String): Boolean = obj.matches(requirement)

        override fun toXml() = matcherElement {
            it.requirementAttribute(requirement.pattern)
        }

        override fun toString(): String = "matches ${requirement.pattern}"

        companion object : StringMatcherCompanion() {
            override val subtype: String = "Regex"
        }
    }

    abstract class StringMatcherCompanion : TypedXmlObjectWithSubtypeCompanion {
        override val type: String = StringMatcher.type
    }
}
