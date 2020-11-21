package com.github.vatbub.smartcharge.apps

data class ApplicationStatusMatcher(val requirement: ApplicationStatus) : Matcher<ApplicationStatus> {
    override fun matches(obj: ApplicationStatus): Boolean = obj == requirement
}