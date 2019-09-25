/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.tests

import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.tests.utils.*
import io.ktor.http.*
import kotlinx.io.streams.*
import java.io.*
import kotlin.test.*

class FormsTest {

    @Test
    fun testEmptyFormData() = clientTest(MockEngine) {
        config {
            engine {
                addHandler {
                    val content = it.body.toByteReadPacket()
                    respondOk(content.readText())
                }
            }
        }

        test { client ->
            val input = object : InputStream() {
                override fun read(): Int = -1
            }.asInput()

            val builder = HttpRequestBuilder().apply {
                body = MultiPartFormDataContent(
                    formData {
                        appendInput("file",
                            Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                                append(HttpHeaders.ContentDisposition, "filename=myfile.txt")
                            }
                        ) { input }
                    }
                )
            }

            val data = client.execute(builder).use {
                it.response.receive<String>()
            }
        }
    }
}
