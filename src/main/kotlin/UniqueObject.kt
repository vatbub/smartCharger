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

import tk.pratanumandal.unique4j.Unique

val unique = object : Unique(appId) {
    override fun sendMessage(): String {
        logger.warn("Another instance is already running, sending a message to this instance...")
        return "otherInstanceShowGui"
    }

    override fun handleException(exception: Exception?) {
        if (exception != null)
            throw exception
    }

    override fun receiveMessage(p0: String?) {
        logger.info("Received a message from another instance of this application, showing the main gui...")
        GuiHelper.showMainView()
    }

    override fun beforeExit() {
        super.beforeExit()
        logger.warn("Exiting as another instance is running...")
    }
}
