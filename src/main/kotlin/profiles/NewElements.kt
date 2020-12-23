package com.github.vatbub.smartcharge.profiles

import org.jdom2.Attribute
import org.jdom2.Element

fun profileElement(block: (Element) -> Unit) = Element("profile").apply(block)
fun conditionElement(block: (Element) -> Unit) = Element("condition").apply(block)
fun matcherElement(block: (Element) -> Unit) = Element("matcher").apply(block)
fun requirementAttribute(value: String) = Attribute("requirement", value)