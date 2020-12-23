package com.github.vatbub.smartcharge

import com.github.vatbub.smartcharge.Charger.ChargerState.Unknown
import com.github.vatbub.smartcharge.ifttt.IftttMakerChannel
import com.github.vatbub.smartcharge.logging.logger
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Charger {
    private val stateVerificationExecutorService: ScheduledExecutorService by lazy {
        logger.debug("Starting the state verification executor service...")
        Executors.newSingleThreadScheduledExecutor {
            Executors.defaultThreadFactory().newThread(it).apply {
                isDaemon = true
            }
        }
    }

    fun switchCharger(newChargerState: ChargerState) {
        logger.info("Switching the charger ${newChargerState.toString().toLowerCase()}...")

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
        lastChargerStateVerificationFuture?.cancel(false)
        if (!stateVerificationExecutorService.isShutdown)
            lastChargerStateVerificationFuture =
                stateVerificationExecutorService.schedule(this::verifyChargerState, 30, TimeUnit.SECONDS)
        logger.debug(result.responseText)
    }

    private var lastChargerStateVerificationFuture: Future<*>? = null


    private fun verifyChargerState() {
        if (expectedChargerState == Unknown) return
        val currentChargerState = BatteryInfo.currentChargerState
        if (expectedChargerState != currentChargerState)
            logger.warn("Please verify that your charger is working. Your charger is $currentChargerState but should be $expectedChargerState")
    }

    private var expectedChargerState: ChargerState = Unknown

    enum class ChargerState {
        On, Off, Unknown
    }
}