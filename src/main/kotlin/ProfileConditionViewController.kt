package com.github.vatbub.smartcharge

import com.github.vatbub.smartcharge.profiles.ApplicationStatus
import com.github.vatbub.smartcharge.profiles.conditions.ProfileCondition
import com.github.vatbub.smartcharge.profiles.matcherElement
import com.github.vatbub.smartcharge.profiles.matchers.*
import com.github.vatbub.smartcharge.profiles.requirementAttribute
import org.jdom2.Attribute
import org.jdom2.Element

abstract class ProfileConditionViewController<T : ProfileCondition> {
    var currentCondition: T? = null
        protected set

    protected abstract fun onConditionSet(newCondition: T)

    fun setCondition(condition: T) {
        currentCondition = condition
        onConditionSet(condition)
    }

    protected enum class StringMatcherMode(private val stringRepresentation: String) {
        Equals("equals"),
        Regex("matches regex"),
        Disabled("Ignore");

/*-
 * #%L
 * Smart Charge
 * %%
 * Copyright (C) 2019 - 2021 Frederik Kammel
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

        override fun toString(): String = stringRepresentation
    }

    protected enum class OptionalStringMatcherMode(private val stringRepresentation: String) {
        Equals("equals"),
        Regex("matches regex"),
        Disabled("Ignore");

        override fun toString(): String = stringRepresentation
    }

    protected enum class IntMatcherMode(private val stringRepresentation: String) {
        Equals("equals"),
        Lower("is lower than"),
        LowerOrEquals("is lower than or equals"),
        Greater("is greater than"),
        GreaterOrEquals("is greater than or equals"),
        Between("is between"),
        Disabled("Ignore");

        override fun toString(): String = stringRepresentation
    }

    protected enum class ApplicationStatusMatcherMode(private val stringRepresentation: String) {
        Equals("equals"),
        Disabled("Ignore");

        override fun toString(): String = stringRepresentation
    }

    protected val Matcher<String>.matcherMode: StringMatcherMode
        get() = when (this) {
            is StringMatcher.EqualsMatcher -> StringMatcherMode.Equals
            is StringMatcher.RegexMatcher -> StringMatcherMode.Regex
            else -> throw IllegalArgumentException()
        }

    protected val Matcher<String?>.matcherMode: OptionalStringMatcherMode
        get() = when (this) {
            is OptionalStringMatcher.EqualsMatcher -> OptionalStringMatcherMode.Equals
            is OptionalStringMatcher.RegexMatcher -> OptionalStringMatcherMode.Regex
            else -> throw IllegalArgumentException()
        }

    protected val Matcher<Int>.matcherMode: IntMatcherMode
        get() = when (this) {
            is IntMatcher.EqualsMatcher -> IntMatcherMode.Equals
            is IntMatcher.LowerMatcher -> IntMatcherMode.Lower
            is IntMatcher.LowerOrEqualsMatcher -> IntMatcherMode.LowerOrEquals
            is IntMatcher.GreaterMatcher -> IntMatcherMode.Greater
            is IntMatcher.GreaterOrEqualsMatcher -> IntMatcherMode.GreaterOrEquals
            is IntMatcher.BetweenMatcher -> IntMatcherMode.Between
            else -> throw IllegalArgumentException()
        }

    protected val Matcher<ApplicationStatus>.matcherMode: ApplicationStatusMatcherMode
        get() = when (this) {
            is DisabledMatcher<ApplicationStatus> -> ApplicationStatusMatcherMode.Disabled
            else -> ApplicationStatusMatcherMode.Equals
        }
}
