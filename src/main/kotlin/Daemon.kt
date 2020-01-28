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

import com.github.vatbub.common.internet.Internet
import com.github.vatbub.smartcharge.ChargingMode.*
import com.github.vatbub.smartcharge.Daemon.ChargerState.Off
import com.github.vatbub.smartcharge.Daemon.ChargerState.On
import oshi.SystemInfo
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object Daemon {
    private var scheduledExecutorService: ScheduledExecutorService? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val isRunning: Boolean
        get() {
            synchronized(Lock) {
                return scheduledExecutorService != null
            }
        }

    object Lock

    private fun getCurrentBatteryPercentage(): Double {
        val powerSources = SystemInfo().hardware.powerSources
        var sourceIndex: Int? = null

        logger.info("Reading power state...")
        powerSources.forEachIndexed { index, powerSource ->
            logger.info("Power source info: name=${powerSource.name} remainingCapacity=${powerSource.remainingCapacity}")
            if (powerSource.name.contains("battery", ignoreCase = true))
                sourceIndex = index
        }

        if (sourceIndex == null)
            throw IllegalStateException("This computer does not seem to have a battery. If you think this is an error, please send the log output to the project maintainers.")

        return powerSources[sourceIndex!!].remainingCapacity * 100
    }

    enum class ChargerState {
        On, Off
    }

    fun switchCharger(newChargerState: ChargerState) {
        logger.info("Switching the charger ${newChargerState.toString().toLowerCase()}...")

        val eventName = when (newChargerState) {
            On -> preferences[Keys.IFTTTStartChargingEventName]
            Off -> preferences[Keys.IFTTTStopChargingEventName]
        }

        val apiKey = preferences[Keys.IFTTTMakerApiKey]
        if (apiKey.isBlank()) {
            logger.warn("Cannot send the event to IFTTT due to an exception", IllegalArgumentException("IFTTT Maker api key not set. Please get your api key from https://ifttt.com/maker_webhooks/settings and set the setting iftttMakerKey to it. Type \\\"<programName> help\\\" to see more info."))
            stop()
        }

        val result = Internet.sendEventToIFTTTMakerChannel(apiKey, eventName)
        logger.info(result)
        logger.info("Event successfully sent to IFTTT.")
    }

    private fun start() {
        synchronized(Lock) {
            logger.info("[DAEMON] Initializing optimized charging...")
            val newExecutorService = Executors.newSingleThreadScheduledExecutor()
            newExecutorService.scheduleAtFixedRate({
                try {
                    val percentage = getCurrentBatteryPercentage()
                    if (percentage <= preferences[Keys.MinPercentage].toDouble())
                        switchCharger(On)
                    else if (percentage >= preferences[Keys.MaxPercentage].toDouble())
                        switchCharger(Off)
                } catch (e: Throwable) {
                    exceptionHandler(Thread.currentThread(), e)
                }
            }, 0, 10, TimeUnit.SECONDS)

            scheduledExecutorService = newExecutorService
        }
    }

    fun stop() {
        synchronized(Lock) {
            if (!isRunning) return
            logger.info("[DAEMON] Shutting the daemon down...")
            scheduledExecutorService?.shutdownNow()
        }
    }

    fun applyConfiguration() {
        if (isRunning)
            logger.info("[DAEMON] Applying configuration changes...")
        else
            logger.info("[DAEMON] Starting the daemon...")

        stop()

        when (preferences[Keys.CurrentChargingMode]) {
            AlwaysOn -> Thread { switchCharger(On) }.start()
            Optimized -> start()
            AlwaysOff -> Thread { switchCharger(Off) }.start()
        }
    }
}

enum class ChargingMode {
    AlwaysOn, Optimized, AlwaysOff
}
