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

import com.github.vatbub.smartcharge.logging.logger
import oshi.SystemInfo
import oshi.hardware.PowerSource
import kotlin.time.ExperimentalTime

@ExperimentalTime
object BatteryInfo {
    private val powerSources: Array<out PowerSource>
        get() {
            val sources = SystemInfo().hardware.powerSources
            if (sources == null || sources.isEmpty())
                throw IllegalStateException("This computer does not seem to have a battery. If you think this is an error, please send the log output to the project maintainers.")
            return sources
        }

    val currentChargerState: Charger.ChargerState
        get() {
            logger.debug("Reading power charging state...")
            return if (powerSources.any { it.isPowerOnLine }) {
                logger.debug("Computer is connected to power.")
                Charger.ChargerState.On
            } else {
                logger.debug("Computer is not connected to power.")
                Charger.ChargerState.Off
            }
        }

    val currentPercentage: Double
        get() {
            logger.debug("Reading power percentage state...")
            var totalMaxCapacity = 0
            var totalCurrentCharge = 0
            powerSources.forEach { powerSource ->
                logger.debug("Power source info: name=${powerSource.name} remainingCapacity=${powerSource.remainingCapacityPercent}")
                totalMaxCapacity += powerSource.maxCapacity
                totalCurrentCharge += powerSource.currentCapacity
            }

            val percentage = 100.0 * totalCurrentCharge / totalMaxCapacity
            logger.debug("Current total capacity: $percentage")
            return percentage
        }
}
