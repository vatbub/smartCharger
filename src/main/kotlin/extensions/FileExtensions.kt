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
