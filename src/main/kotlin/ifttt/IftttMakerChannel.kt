/*-
 * #%L
 * ifttt-maker-cli
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

package com.github.vatbub.smartcharge.ifttt

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

data class IftttMakerChannel(private val apiKey: String, private val endpoint: URL = URL("https://maker.ifttt.com/")) {
    companion object {
        private val client by lazy { OkHttpClient() }
        private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    }

    fun sendEvent(
        eventName: String,
        details1: String = "",
        details2: String = "",
        details3: String = ""
    ): IftttMakerChannelResponse {
        val request = Request.Builder()
            .url(URL(endpoint, "trigger/$eventName/with/key/$apiKey"))
            .post(
                "{ \"value1\" : \"$details1\", \"value2\" : \"$details2\", \"value3\" : \"$details3\" }"
                    .toRequestBody(jsonMediaType)
            )
            .build()
        client.newCall(request).execute().use { response ->
            return IftttMakerChannelResponse(response.code, response.body?.string())
        }
    }
}
