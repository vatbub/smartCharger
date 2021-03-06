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

import com.github.vatbub.kotlin.preferences.Key
import com.github.vatbub.kotlin.preferences.Preferences
import com.github.vatbub.kotlin.preferences.PropertiesFileKeyValueProvider
import com.github.vatbub.smartcharge.ChargingMode.Optimized
import java.io.File

val preferences = Preferences(PropertiesFileKeyValueProvider(appDataFolder.resolve("settings.properties")))

object Keys {
    object IFTTTMakerApiKey : Key<String>("iftttMakerKey", "", { it }, { it })
    object IFTTTStartChargingEventName :
        Key<String>("iftttStartChargingEventName", "laptopStartCharging", { it }, { it })

    object IFTTTStopChargingEventName : Key<String>("iftttStopChargingEventName", "laptopStopCharging", { it }, { it })
    object MinPercentage : Key<Double>("minPercentage", 25.0, { it.toDouble() }, { it.toString() })
    object MaxPercentage : Key<Double>("maxPercentage", 80.0, { it.toDouble() }, { it.toString() })
    object CurrentChargingMode :
        Key<ChargingMode>("currentChargingMode", Optimized, { ChargingMode.valueOf(it) }, { it.toString() })

    object TrayMessageShown : Key<Boolean>("trayMessageShown", false, { it.toBoolean() }, { it.toString() })

    object Profiles {
        object Enabled : Key<Boolean>("profilesEnabled", false, { it.toBoolean() }, { it.toString() })

        val XmlFileLocation by lazy {
            Key(
                uniqueName = "profilesXmlFileLocation",
                defaultValue = appDataFolder.resolve("profiles.xml"),
                parser = { File(it) },
                serializer = { it.absolutePath }
            )
        }
    }
}
