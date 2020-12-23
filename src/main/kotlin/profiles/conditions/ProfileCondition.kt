package com.github.vatbub.smartcharge.profiles.conditions

import com.github.vatbub.smartcharge.profiles.XmlSerializable
import org.jdom2.Element
import kotlin.time.ExperimentalTime

interface ProfileCondition : XmlSerializable {
    fun isActive(): Boolean

    @ExperimentalTime
    companion object {
        fun fromXml(conditionElement: Element): ProfileCondition =
            when (val type = conditionElement.getAttribute("type").value) {
                "Application" -> ApplicationCondition.fromXml(conditionElement)
                else -> throw IllegalArgumentException("ProfileCondition type $type unknown")
            }
    }
}