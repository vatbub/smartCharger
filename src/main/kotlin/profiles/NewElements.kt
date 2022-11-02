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
package com.github.vatbub.smartcharge.profiles

import org.jdom2.Attribute
import org.jdom2.Element

fun Profile.profileElement(block: (Element) -> Unit) = Element("profile").apply(block)
fun TypedXmlObjectCompanion.conditionElement(block: (Element) -> Unit) = Element("condition")
    .applyTypeAttributes(this)
    .apply(block)

fun TypedXmlObjectCompanion.matcherElement(block: (Element) -> Unit) = Element("matcher")
    .applyTypeAttributes(this)
    .apply(block)

private fun Element.applyTypeAttributes(companion: TypedXmlObjectCompanion) = apply {
    attributes.add(Attribute("type", companion.type))
    if (companion is TypedXmlObjectWithSubtypeCompanion)
        attributes.add(Attribute("subtype", companion.subtype))
}

fun Element.requirementAttribute(value: String) = attributes.add(Attribute("requirement", value))
