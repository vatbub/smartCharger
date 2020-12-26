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
package com.github.vatbub.smartcharge.extensions

import com.github.vatbub.smartcharge.ChargingMode
import org.jdom2.Attribute
import org.jdom2.Element

val Element.matcher
    get() = this.childWithName("matcher")

val Element.disabled: Boolean
    get() {
        val attribute = this.getAttribute("disabled") ?: return false
        return listOf("true", "on", "1", "yes")
            .contains(attribute.value.trim())
    }

val Element.condition
    get() = this.childWithName("condition")

val Element.profiles
    get() = this.childrenWithName("profile")

val Element.id
    get() = this.getAttribute("id").longValue

val Element.priority
    get() = this.getAttribute("priority").intValue

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
