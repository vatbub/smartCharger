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

import com.beust.jcommander.Parameter

class CommandLineArgs {
    @Parameter(names = ["--help", "-h", "/?"], help = true)
    var help: Boolean = false

    @Parameter(names = ["--noGui", "--nogui", "-ng", "/noGui", "/nogui", "/ng"], arity = 0, description = "If specified, no graphical user interface will be shown. Only the tray icon will be visible to the user. The user may open the GUI at any time by double-clicking the tray icon.")
    var noGui: Boolean = false

    @Parameter(names = ["--noDaemon", "--nodaemon", "-nd", "/noDaemon", "/nodaemon", "/nd"], arity = 0, description = "If specified, no daemon will be launched. If --switch is specified, the charger will be turned on or off accordingly. If --mode is specified, the specified mode will be saved for the next launch of the daemon.")
    var noDaemon: Boolean = false

    @Parameter(names = ["--mode", "-m", "/mode", "/m"], arity = 1, description = "The charging mode to use. Possible values (case-sensitive): AlwaysOn, Optimized, AlwaysOff ; If not specified, the charging mode which was used the last time is used. The user may change the charging mode at any time through the tray icon or through the GUI.")
    var chargingMode: ChargingMode? = null

    @Parameter(names = ["--switch", "-s", "/switch", "/s"], arity = 1, description = "Switches the charger on or off without regard to the current charging mode. Possible values (case-sensitive): On, Off")
    var switchChargerState: Daemon.ChargerState? = null
}
