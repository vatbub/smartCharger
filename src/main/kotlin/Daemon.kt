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

import com.github.vatbub.smartcharge.Charger.ChargerState.Off
import com.github.vatbub.smartcharge.Charger.ChargerState.On
import com.github.vatbub.smartcharge.Charger.switchCharger
import com.github.vatbub.smartcharge.extensions.launchPeriodically
import com.github.vatbub.smartcharge.logging.logger
import com.github.vatbub.smartcharge.profiles.ProfileManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.InterruptedIOException
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.minutes

@ExperimentalTime
object Daemon {
    private var daemonJob: Job? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val isRunning: Boolean
        get() = daemonJob != null

    private fun start() {
        logger.info("Starting the daemon...")

        daemonJob = GlobalScope.launchPeriodically(1.minutes) {
            determineCurrentChargingMode()
                .switchChargerAccordingToChargingMode()
        }
    }

    private fun determineCurrentChargingMode(): ChargingMode {

        val activeProfile =
            if (!ProfileManager.enabled) {
                logger.info("ProfileManager is disabled")
                null
            } else if (ProfileManager.isOverridden) {
                logger.info("ProfileManager is enabled but overridden")
                null
            } else {
                logger.info("ProfileManager is enabled and not overridden")
                ProfileManager.getActiveProfile()
            }

        if (activeProfile == null) {
            logger.debug("No profile active, setting global charge mode...")
            return preferences[Keys.CurrentChargingMode]
        }

        logger.debug("Active profile: ${activeProfile.id}")
        return activeProfile.chargingMode
    }

    private suspend fun ChargingMode.switchChargerAccordingToChargingMode() {
        try {
            when (this) {
                ChargingMode.AlwaysOn -> switchCharger(On)
                ChargingMode.AlwaysOff -> switchCharger(Off)
                ChargingMode.Optimized -> {
                    val percentage = BatteryInfo.currentPercentage
                    if (percentage <= preferences[Keys.MinPercentage].toDouble())
                        switchCharger(On)
                    else if (percentage >= preferences[Keys.MaxPercentage].toDouble())
                        switchCharger(Off)
                }
            }
        } catch (e: InterruptedIOException) {
            logger.info("InterruptedIOException while setting charger state, retrying...", e)
            delay(500.milliseconds)
            switchChargerAccordingToChargingMode()
        }
    }

    fun prepareApplicationShutdown() {
        stop()
        logger.debug("Shutting the state verification executor service down...")
    }

    fun stop() {
        if (!isRunning) return
        logger.info("Shutting the daemon down...")
        daemonJob?.cancel()
        daemonJob = null
    }

    fun applyConfiguration() {
        if (isRunning)
            logger.info("Applying configuration changes...")

        stop()
        start()
    }
}

enum class ChargingMode {
    AlwaysOn, Optimized, AlwaysOff
}
