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

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.PointerType
import com.sun.jna.WString
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.W32APIOptions


interface User32Extension : User32 {
    companion object {
        val instance: User32Extension by lazy { Native.load("user32", User32Extension::class.java, W32APIOptions.DEFAULT_OPTIONS) }
    }

    fun ShutdownBlockReasonCreate(hwnd: WinDef.HWND, pwszReason: WString): WinDef.BOOL

    class LPCWSTR : PointerType {
        constructor(address: Pointer?) : super(address)
        constructor() : super()
    }
}
