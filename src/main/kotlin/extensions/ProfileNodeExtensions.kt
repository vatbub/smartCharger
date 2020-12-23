package com.github.vatbub.smartcharge.extensions

import com.github.vatbub.smartcharge.ChargingMode
import org.jdom2.Attribute
import org.jdom2.Element

val Element.matcher
    get() = this.childWithName("matcher")

val Element.condition
    get() = this.childWithName("condition")

val Element.profiles
    get() = this.childrenWithName("profile")

val Element.id
    get() = this.getAttribute("id").longValue

val Element.chargingMode
    get() = ChargingMode.valueOf(this.getAttribute("chargingMode").value)

val Element.type: String
    get() = this.getAttribute("type").value

val Element.subtype: String
    get() = this.getAttribute("subtype").value

val Element.requirement: Attribute
    get() = this.getAttribute("requirement")

val Element.imageName
    get() = this.childWithName("imageName")
val Element.pid
    get() = this.childWithName("pid")
val Element.sessionName
    get() = this.childWithName("sessionName")
val Element.sessionId
    get() = this.childWithName("sessionId")
val Element.memoryUsage
    get() = this.childWithName("memoryUsage")
val Element.status
    get() = this.childWithName("status")
val Element.userName
    get() = this.childWithName("userName")
val Element.windowTitle
    get() = this.childWithName("windowTitle")