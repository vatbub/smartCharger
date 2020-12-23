package com.github.vatbub.smartcharge.profiles

import org.jdom2.Element

interface XmlSerializable {
    fun toXml(): Element
}