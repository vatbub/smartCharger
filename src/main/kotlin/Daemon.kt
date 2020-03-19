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
import com.github.vatbub.smartcharge.Daemon.ChargerState.*
import com.github.vatbub.smartcharge.logging.exceptionHandler
import com.github.vatbub.smartcharge.logging.logger
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object Daemon {
    private var periodicBatteryCheckExecutorService: ScheduledExecutorService? = null
    private val stateVerificationExecutorService: ScheduledExecutorService by lazy {
        logger.debug("Starting the state verification executor service...")
        Executors.newSingleThreadScheduledExecutor()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val isRunning: Boolean
        get() {
            synchronized(Lock) {
                return periodicBatteryCheckExecutorService != null
            }
        }

    object Lock

    var expectedChargerState: ChargerState = Unknown
        private set
    private var lastChargerStateVerificationFuture: Future<*>? = null


    enum class ChargerState {
        On, Off, Unknown
    }

    private fun verifyChargerState() {
        if (expectedChargerState == Unknown) return
        val currentChargerState = BatteryInfo.currentChargerState
        if (expectedChargerState != currentChargerState)
            logger.warn("Please verify that your charger is working. Your charger is $currentChargerState but should be $expectedChargerState")
    }

    fun switchCharger(newChargerState: ChargerState) {
        logger.info("Switching the charger ${newChargerState.toString().toLowerCase()}...")

        expectedChargerState = newChargerState
        val eventName = when (newChargerState) {
            On -> preferences[Keys.IFTTTStartChargingEventName]
            Off -> preferences[Keys.IFTTTStopChargingEventName]
            else -> throw IllegalArgumentException("Illegal argument value supplied for nwChargerState: $this, allowed values: On, Off")
        }

        val apiKey = preferences[Keys.IFTTTMakerApiKey]
        if (apiKey.isBlank()) {
            logger.error("Cannot send the event to IFTTT due to an exception", IllegalArgumentException("IFTTT Maker api key not set. Please get your api key from https://ifttt.com/maker_webhooks/settings and set the setting iftttMakerKey to it. Type \\\"<programName> help\\\" to see more info."))
            stop()
        }

        val result = Internet.sendEventToIFTTTMakerChannel(apiKey, eventName)
        lastChargerStateVerificationFuture?.cancel(false)
        lastChargerStateVerificationFuture = stateVerificationExecutorService.schedule(this::verifyChargerState, 30, TimeUnit.SECONDS)
        logger.debug(result)
    }

    private fun start() {
        synchronized(Lock) {
            logger.info("Starting the daemon, initializing optimized charging...")
            val newExecutorService = Executors.newSingleThreadScheduledExecutor()
            newExecutorService.scheduleAtFixedRate({
                try {
                    val percentage = BatteryInfo.currentPercentage
                    if (percentage <= preferences[Keys.MinPercentage].toDouble())
                        switchCharger(On)
                    else if (percentage >= preferences[Keys.MaxPercentage].toDouble())
                        switchCharger(Off)
                } catch (e: Throwable) {
                    exceptionHandler(Thread.currentThread(), e)
                }
            }, 0, 1, TimeUnit.MINUTES)

            periodicBatteryCheckExecutorService = newExecutorService
        }
    }

    fun prepareApplicationShutdown() {
        synchronized(Lock) {
            stop()
            logger.debug("Shutting the state verification executor service down...")
            stateVerificationExecutorService.shutdownNow()
        }
    }

    fun stop() {
        synchronized(Lock) {
            if (!isRunning) return
            logger.info("Shutting the daemon down...")
            periodicBatteryCheckExecutorService?.shutdownNow()
        }
    }

    fun applyConfiguration() {
        if (isRunning)
            logger.info("Applying configuration changes...")
        else
            logger.info("Starting the daemon...")

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
