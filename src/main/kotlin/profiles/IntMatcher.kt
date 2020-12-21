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

    companion object : MatcherCompanion<Int, IntMatcher> {
        override fun fromXml(matcherElement: Element): IntMatcher {
            return when (val subtype = matcherElement.getAttribute("subtype")) {
                "Equals" -> EqualsMatcher(matcherElement.getAttribute("requirement").toInt())
                "Lower" -> LowerMatcher(matcherElement.getAttribute("requirement").toInt())
                "LowerOrEquals" -> LowerOrEqualsMatcher(matcherElement.getAttribute("requirement").toInt())
                "Greater" -> GreaterMatcher(matcherElement.getAttribute("requirement").toInt())
                "GreaterOrEquals" -> GreaterOrEqualsMatcher(matcherElement.getAttribute("requirement").toInt())
                "Between" -> BetweenMatcher(
                    matcherElement.getAttribute("lowerRequirement").toInt()..
                            matcherElement.getAttribute("higherRequirement").toInt()
                )
                else -> throw IllegalArgumentException("IntMatcher subtype $subtype unknown")
            }
        }
    }
}
