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

import javafx.application.Platform
import org.slf4j.LoggerFactory
import java.awt.TrayIcon

val logger by lazy { LoggerFactory.getLogger("Global")!! }

val Any.logger
    get() = LoggerFactory.getLogger(this.javaClass)!!

val exceptionHandler = { thread: Thread, throwable: Throwable ->
    logger.error("Exception in thread ${thread.name}. Type \\\"<programName> help\\\" to see more info.", throwable)
    if (EntryClass.instance != null)
        Platform.runLater { EntryClass.instance?.controllerInstance?.showException(throwable) }
    SystemTrayManager.showTrayMessage("Smart charge", "An exception occurred: ${throwable.javaClass.name}: ${throwable.localizedMessage}", TrayIcon.MessageType.ERROR)
}
