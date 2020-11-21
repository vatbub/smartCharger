package com.github.vatbub.smartcharge.apps

import kotlin.time.ExperimentalTime

@ExperimentalTime
data class ApplicationMatcher(
        val imageNameMatcher: Matcher<String>,
        val pidMatcher: Matcher<Int>,
        val sessionNameMatcher: Matcher<String>,
        val sessionIdMatcher: Matcher<Int>,
        val memoryUsageMatcher: Matcher<String>,
        val statusMatcher: Matcher<ApplicationStatus>,
        val userNameMatcher: Matcher<String?>,
        val windowTitleMatcher: Matcher<String?>
) : Matcher<RunningApplication> {
    override fun matches(obj: RunningApplication): Boolean =
            imageNameMatcher.matches(obj.imageName) &&
                    pidMatcher.matches(obj.pid) &&
                    sessionNameMatcher.matches(obj.sessionName) &&
                    sessionIdMatcher.matches(obj.sessionId) &&
                    memoryUsageMatcher.matches(obj.memoryUsage) &&
                    statusMatcher.matches(obj.status) &&
                    userNameMatcher.matches(obj.userName) &&
                    windowTitleMatcher.matches(obj.windowTitle)
}