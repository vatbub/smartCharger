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
package com.github.vatbub.smartcharge.apps

import com.github.vatbub.smartcharge.profiles.RunningApplication
import com.github.vatbub.smartcharge.profiles.getRunningApps
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class RunningAppsListTest {
    @Test
    fun printRunningAppsTest() = assertDoesNotThrow {
        RunningApplication.getRunningApps().forEach { println(it.imageName) }
    }

    @Test
    fun printRunningAppsWithWindowTitleTest() = assertDoesNotThrow {
        RunningApplication.getRunningApps()
            .filter { it.windowTitle != null }
            .forEach { println("${it.imageName} - ${it.windowTitle} - ${it.memoryUsage}") }
    }
}
