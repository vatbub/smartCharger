package com.github.vatbub.smartcharge.apps

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
}
