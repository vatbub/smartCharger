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

import com.github.vatbub.smartcharge.extensions.requirement
import com.github.vatbub.smartcharge.extensions.subtype
import org.jdom2.Attribute
import org.jdom2.Element


sealed class IntMatcher : Matcher<Int> {
    data class EqualsMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj == requirement
    }

    data class LowerMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj < requirement
    }

    data class LowerOrEqualsMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj <= requirement
    }

    data class GreaterMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj > requirement
    }

    data class GreaterOrEqualsMatcher(val requirement: Int) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj >= requirement
    }

    data class BetweenMatcher(val requirement: IntRange) : IntMatcher() {
        override fun matches(obj: Int): Boolean = obj in requirement
    }

    override fun toXml(): Element = when (this) {
        is EqualsMatcher -> matcherElement { it.attributes.add(this.requirement.toXml()) }
        is LowerMatcher -> matcherElement { it.attributes.add(this.requirement.toXml()) }
        is LowerOrEqualsMatcher -> matcherElement { it.attributes.add(this.requirement.toXml()) }
        is GreaterMatcher -> matcherElement { it.attributes.add(this.requirement.toXml()) }
        is GreaterOrEqualsMatcher -> matcherElement { it.attributes.add(this.requirement.toXml()) }
        is BetweenMatcher -> matcherElement { it.attributes.addAll(this.requirement.toXml()) }
    }

    private fun Int.toXml() = requirementAttribute(this.toString())
    private fun IntRange.toXml() = listOf(
        Attribute("lowerRequirement", this.first.toString()),
        Attribute("higherRequirement", this.last.toString())
    )

    companion object : XmlSerializableCompanion<IntMatcher> {
        override fun fromXml(element: Element): IntMatcher {
            return when (val subtype = element.subtype) {
                "Equals" -> EqualsMatcher(element.requirement.intValue)
                "Lower" -> LowerMatcher(element.requirement.intValue)
                "LowerOrEquals" -> LowerOrEqualsMatcher(element.requirement.intValue)
                "Greater" -> GreaterMatcher(element.requirement.intValue)
                "GreaterOrEquals" -> GreaterOrEqualsMatcher(element.requirement.intValue)
                "Between" -> BetweenMatcher(
                    element.getAttribute("lowerRequirement").intValue..
                            element.getAttribute("higherRequirement").intValue
                )
                else -> throw IllegalArgumentException("IntMatcher subtype $subtype unknown")
            }
        }
    }
}
