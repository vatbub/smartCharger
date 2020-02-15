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
import java.io.OutputStream
import java.io.PrintStream

val logger by lazy { LoggerFactory.getLogger("Global")!! }

val Any.logger
    get() = LoggerFactory.getLogger(this.javaClass)!!

val exceptionHandler = { thread: Thread, throwable: Throwable ->
    logger.error("Exception in thread ${thread.name}. Type \\\"<programName> help\\\" to see more info.", throwable)
    if (EntryClass.instance != null)
        Platform.runLater { EntryClass.instance?.controllerInstance?.showException(throwable) }
    SystemTrayManager.showTrayMessage("Smart charge", "An exception occurred: ${throwable.javaClass.name}: ${throwable.localizedMessage}", TrayIcon.MessageType.ERROR)
}

private object LockSystemOut
private object LockSystemErr

fun initializeSystemOutAndErrCopy() {
    initializeSystemOutCopy()
    initializeSystemErrCopy()
}

private fun initializeSystemOutCopy() {
    if (internalSystemOutCopyStream != null) return
    synchronized(LockSystemOut) {
        if (internalSystemOutCopyStream != null) return
        internalSystemOutCopyStream = OutputStreamCopy(System.out)
        System.setOut(PrintStream(internalSystemOutCopyStream!!))
    }
}

private fun initializeSystemErrCopy() {
    if (internalSystemErrCopyStream != null) return
    synchronized(LockSystemErr) {
        if (internalSystemErrCopyStream != null) return
        internalSystemErrCopyStream = OutputStreamCopy(System.err, systemOutCopyStream)
        System.setErr(PrintStream(internalSystemErrCopyStream!!))
    }
}

val systemOutCopyStream: OutputStreamCopy
    get() {
        val internalSystemOutCopyStreamCopy = internalSystemOutCopyStream
        return if (internalSystemOutCopyStreamCopy != null) {
            internalSystemOutCopyStreamCopy
        } else {
            initializeSystemOutAndErrCopy()
            return internalSystemOutCopyStream!!
        }
    }

private var internalSystemOutCopyStream: OutputStreamCopy? = null
private var internalSystemErrCopyStream: OutputStreamCopy? = null

class OutputStreamCopy(private val previousSystemOut: PrintStream, private val outputStreamCopyToWriteTo: OutputStreamCopy? = null) : OutputStream() {
    private val internalContent = StringBuilder()
    val content: String
        get() = outputStreamCopyToWriteTo?.content ?: internalContent.toString()

    @Suppress("MemberVisibilityCanBePrivate")
    val onContentChange = mutableListOf<(outputStreamCopy: OutputStreamCopy, oldValue: String, newValue: String) -> Unit>()
    val onContentAppend = mutableListOf<(outputStreamCopy: OutputStreamCopy, appendedText: String) -> Unit>()

    override fun write(b: Int) {} // apparently never called.

    override fun write(b: ByteArray, off: Int, len: Int) {
        val oldContent = content
        val appendedText = String(b, off, len)
        if (outputStreamCopyToWriteTo == null)
            internalContent.append(appendedText)
        else
            outputStreamCopyToWriteTo.internalContent.append(appendedText)
        previousSystemOut.write(b, off, len)

        notifyAppendListeners(appendedText)
        outputStreamCopyToWriteTo?.notifyAppendListeners(appendedText)
        notifyChangeListeners(oldContent)
        outputStreamCopyToWriteTo?.notifyChangeListeners(oldContent)
    }

    private fun notifyAppendListeners(appendedText: String) {
        onContentAppend.forEach { it(this, appendedText) }
    }

    private fun notifyChangeListeners(oldContent: String) {
        onContentChange.forEach { it(this, oldContent, content) }
    }
}
