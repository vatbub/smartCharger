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

import com.github.vatbub.smartcharge.Charger.ChargerState.Unknown
import com.github.vatbub.smartcharge.ifttt.IftttMakerChannel
import com.github.vatbub.smartcharge.logging.logger
import kotlinx.coroutines.*
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.MILLISECONDS

object Charger {
    private var lastChargerStateVerificationFuture: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun switchCharger(newChargerState: ChargerState) {
        logger.info("Switching the charger ${newChargerState.toString().lowercase(Locale.getDefault())}...")

        expectedChargerState = newChargerState
        val eventName = when (newChargerState) {
            ChargerState.On -> preferences[Keys.IFTTTStartChargingEventName]
            ChargerState.Off -> preferences[Keys.IFTTTStopChargingEventName]
            else -> throw IllegalArgumentException("Illegal argument value supplied for nwChargerState: $this, allowed values: On, Off")
        }

        val apiKey = preferences[Keys.IFTTTMakerApiKey]
        if (apiKey.isBlank()) {
            logger.error(
                "Cannot send the event to IFTTT due to an exception",
                IllegalArgumentException("IFTTT Maker api key not set. Please get your api key from https://ifttt.com/maker_webhooks/settings and set the setting iftttMakerKey to it. Type \\\"<programName> help\\\" to see more info.")
            )
            Daemon.stop()
            return
        }

        val result = IftttMakerChannel(apiKey).sendEvent(eventName)

        lastChargerStateVerificationFuture?.cancel()
        lastChargerStateVerificationFuture = GlobalScope.launch {
            delay(30.seconds.toLong(MILLISECONDS))
            verifyChargerState()
        }
        logger.debug(result.responseText)
    }


    private fun verifyChargerState() {
        if (expectedChargerState == Unknown) return
        val currentChargerState = BatteryInfo.currentChargerState
        if (expectedChargerState != currentChargerState)
            logger.warn(
                "Please verify that your charger is working. " +
                        "Your charger is ${currentChargerState.toString().lowercase(Locale.getDefault())} " +
                        "but should be ${expectedChargerState.toString().lowercase(Locale.getDefault())}"
            )
    }

    private var expectedChargerState: ChargerState = Unknown

    enum class ChargerState {
        On, Off, Unknown
    }
}
