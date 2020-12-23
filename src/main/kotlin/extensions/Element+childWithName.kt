package com.github.vatbub.smartcharge.extensions

import org.jdom2.Element


fun Element.childWithName(name: String): Element =
    this.childWithNameOrNull(name)!!

fun Element.childWithNameOrNull(name: String): Element? =
    this.childrenWithName(name).firstOrNull()

fun Element.childrenWithName(name: String): List<Element> =
    this.children
        .toList()
        .filter { it.name == name }