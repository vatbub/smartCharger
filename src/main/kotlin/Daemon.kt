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
import com.github.vatbub.smartcharge.logging.exceptionHandler
import com.github.vatbub.smartcharge.logging.logger
import com.github.vatbub.smartcharge.profiles.ProfileManager
import java.io.InterruptedIOException
import java.lang.Thread.sleep
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Daemon {
    private var daemonExecutorService: ScheduledExecutorService? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val isRunning: Boolean
        get() {
            synchronized(Lock) {
                return daemonExecutorService != null
            }
        }

    object Lock

    private fun start() {
        synchronized(Lock) {
            logger.info("Starting the daemon, initializing optimized charging...")
            daemonExecutorService = Executors.newSingleThreadScheduledExecutor().apply {
                scheduleAtFixedRate({
                    try {
                        determineCurrentChargingMode()
                            .switchChargerAccordingToChargingMode()
                    } catch (e: Throwable) {
                        exceptionHandler(Thread.currentThread(), e)
                    }
                }, 0, 1, TimeUnit.MINUTES)
            }
        }
    }

    private fun determineCurrentChargingMode(): ChargingMode {
        val activeProfile =
            if (ProfileManager.enabled)
                ProfileManager.getActiveProfile()
            else null

        if (activeProfile == null) {
            logger.debug("No profile active, setting global charge mode...")
            return preferences[Keys.CurrentChargingMode]
        }

        logger.debug("Active profile: ${activeProfile.id}")
        return activeProfile.chargingMode
    }

    private fun ChargingMode.switchChargerAccordingToChargingMode() {
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
            Thread {
                sleep(500)
                switchChargerAccordingToChargingMode()
            }
        }
    }

    fun prepareApplicationShutdown() {
        synchronized(Lock) {
            stop()
            logger.debug("Shutting the state verification executor service down...")
        }
    }

    fun stop() {
        synchronized(Lock) {
            if (!isRunning) return
            logger.info("Shutting the daemon down...")
            daemonExecutorService?.shutdownNow()
        }
    }

    fun applyConfiguration() {
        if (isRunning)
            logger.info("Applying configuration changes...")
        else
            logger.info("Starting the daemon...")

        stop()
        start()
    }
}

enum class ChargingMode {
    AlwaysOn, Optimized, AlwaysOff
}
