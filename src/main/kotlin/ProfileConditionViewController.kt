package com.github.vatbub.smartcharge

import com.github.vatbub.smartcharge.profiles.conditions.ProfileCondition
import com.github.vatbub.smartcharge.profiles.matchers.Matcher
import com.github.vatbub.smartcharge.profiles.matchers.OptionalStringMatcher
import com.github.vatbub.smartcharge.profiles.matchers.StringMatcher

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
}
