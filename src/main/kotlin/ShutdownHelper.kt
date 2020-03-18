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
package com.github.vatbub.smartcharge

import com.github.vatbub.smartcharge.extensions.User32Extension
import com.github.vatbub.smartcharge.extensions.windowsHwnd
import com.github.vatbub.smartcharge.logging.logger
import com.sun.jna.WString
import com.sun.jna.platform.win32.Kernel32
import javafx.stage.Stage
import org.apache.commons.lang3.SystemUtils

object ShutdownHelper {
    fun Stage.preventShutdown(reason: String) {
        return

        if (!SystemUtils.IS_OS_WINDOWS) {
            logger.error("System shutdown can only be prevented on Windows. Using this method on other OSes will not prevent a system shutdown!")
            return
        }

        val convertedReason = WString(reason)
        val result = User32Extension.instance.ShutdownBlockReasonCreate(windowsHwnd, convertedReason).booleanValue()
        if (result){
            logger.warn("Shutdown prevented!")
            return
        }else{
            val errorCode = Kernel32.INSTANCE.GetLastError()
            throw Exception("Call to native method was not successful, the error code is $errorCode")
        }
    }
}
