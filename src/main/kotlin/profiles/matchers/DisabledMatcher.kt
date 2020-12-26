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
package com.github.vatbub.smartcharge.profiles.matchers

import org.jdom2.Attribute
import org.jdom2.Element

class DisabledMatcher<T>(private val matcher: Matcher<T>) : Matcher<T> {
    override fun toXml(): Element =
        matcher.toXml()
            .apply { attributes.add(Attribute("disabled", true.toString())) }

    override fun matches(obj: T): Boolean = true

    fun enabled() = matcher
}

fun <T> Matcher<T>.disabled() = DisabledMatcher(this)
