package com.github.vatbub.smartcharge.extensions

import org.apache.commons.lang3.SystemUtils
import java.awt.Desktop
import java.io.File

fun File.highlightFileInExplorer() {
    if (!SystemUtils.IS_OS_WINDOWS || this.isDirectory) {
        val folder = if (this.isDirectory) this else this.parentFile
        Desktop.getDesktop().open(folder)
    } else {
        Runtime.getRuntime().exec("explorer.exe /select, $absolutePath")
    }
}