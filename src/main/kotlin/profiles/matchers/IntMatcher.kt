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
import org.jdom2.Attribute
import org.jdom2.Element


sealed class IntMatcher : Matcher<Int> {
    data class EqualsMatcher(val requirement: Int = 0) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj == requirement

        override fun toXml(): Element = matcherElement { it.requirementAttribute(requirement.toString()) }

        override fun toString(): String = requirement.toString()

        companion object : IntMatcherCompanion() {
            override val subtype: String = "EqualsMatcher"
        }
    }

    data class LowerMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj < requirement

        override fun toXml(): Element = matcherElement { it.requirementAttribute(requirement.toString()) }

        override fun toString(): String = requirement.toString()

        companion object : IntMatcherCompanion() {
            override val subtype: String = "LowerMatcher"
        }
    }

    data class LowerOrEqualsMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj <= requirement

        override fun toXml(): Element = matcherElement { it.requirementAttribute(requirement.toString()) }

        override fun toString(): String = requirement.toString()

        companion object : IntMatcherCompanion() {
            override val subtype: String = "LowerOrEqualsMatcher"
        }
    }

    data class GreaterMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj > requirement

        override fun toXml(): Element = matcherElement { it.requirementAttribute(requirement.toString()) }

        override fun toString(): String = requirement.toString()

        companion object : IntMatcherCompanion() {
            override val subtype: String = "GreaterMatcher"
        }
    }

    data class GreaterOrEqualsMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj >= requirement

        override fun toXml(): Element = matcherElement { it.requirementAttribute(requirement.toString()) }

        override fun toString(): String = requirement.toString()

        companion object : IntMatcherCompanion() {
            override val subtype: String = "GreaterOrEqualsMatcher"
        }
    }

    data class BetweenMatcher(val requirement: IntRange) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj in requirement

        override fun toXml(): Element = matcherElement { it.attributes.addAll(requirement.toXml()) }

        override fun toString(): String = requirement.toString()

        private fun IntRange.toXml() = listOf(
            Attribute("lowerRequirement", this.first.toString()),
            Attribute("higherRequirement", this.last.toString())
        )

        companion object : IntMatcherCompanion() {
            override val subtype: String = "BetweenMatcher"
        }
    }

    companion object : XmlSerializableCompanion<IntMatcher>, TypedXmlObjectCompanion {
        override val type: String = "IntMatcher"

        override fun fromXml(element: Element): IntMatcher {
            return when (val subtype = element.subtype) {
                EqualsMatcher.subtype -> EqualsMatcher(element.requirement.intValue)
                LowerMatcher.subtype -> LowerMatcher(element.requirement.intValue)
                LowerOrEqualsMatcher.subtype -> LowerOrEqualsMatcher(element.requirement.intValue)
                GreaterMatcher.subtype -> GreaterMatcher(element.requirement.intValue)
                GreaterOrEqualsMatcher.subtype -> GreaterOrEqualsMatcher(element.requirement.intValue)
                BetweenMatcher.subtype -> BetweenMatcher(
                    element.getAttribute("lowerRequirement").intValue..
                            element.getAttribute("higherRequirement").intValue
                )
                else -> throw IllegalArgumentException("IntMatcher subtype $subtype unknown")
            }
        }
    }

    abstract class IntMatcherCompanion : TypedXmlObjectWithSubtypeCompanion {
        override val type: String = IntMatcher.type
    }
}
