package com.github.vatbub.smartcharge.apps

sealed class StringMatcher : Matcher<String?>

data class EqualsStringMatcher(val requirement: String) : StringMatcher() {
    override fun matches(obj: String?): Boolean = obj == requirement
}

data class RegexStringMatcher(val requirement: Regex) : StringMatcher() {
    override fun matches(obj: String?): Boolean = obj?.matches(requirement) ?: false
}
