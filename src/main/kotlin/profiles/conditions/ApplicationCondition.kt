package com.github.vatbub.smartcharge.profiles.conditions

import com.github.vatbub.smartcharge.extensions.matcher
import com.github.vatbub.smartcharge.profiles.*
import org.jdom2.Element
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationCondition(private val matcher: ApplicationMatcher) : ProfileCondition {
    override fun isActive(): Boolean =
        RunningApplication
            .getRunningApps()
            .firstOrNull { matcher.matches(it) } != null

    override fun toXml(): Element =
        conditionElement {
            it.children.add(matcher.toXml())
        }

    companion object : XmlSerializableCompanion<ApplicationCondition> {
        override fun fromXml(element: Element): ApplicationCondition =
            ApplicationCondition(
                matcher = Matcher.fromXml(element.matcher) as ApplicationMatcher
            )
    }
}