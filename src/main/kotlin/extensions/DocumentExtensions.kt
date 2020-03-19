package com.github.vatbub.smartcharge.extensions

import com.jcabi.xml.XMLDocument
import org.w3c.dom.Document

val Document.source: String
    get() = XMLDocument(this).toString()