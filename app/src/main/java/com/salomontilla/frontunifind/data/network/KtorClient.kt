package com.salomontilla.frontunifind.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * =============================================================================
 * KtorClient: Cliente HTTP Asíncrono Moderno para Kotlin & Coroutines
 *
 * Conectado a la API REST oficial de UniFind (Node.js + MongoDB)
 * URL para Emulador Android: http://10.0.2.2:3000/api/v1/
 * =============================================================================
 */
object KtorClient {

    var BASE_URL = "http://10.0.2.2:3000/api/v1/"

    val jsonConfig = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(jsonConfig)
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 6_000L
            requestTimeoutMillis = 15_000L
            socketTimeoutMillis = 15_000L
        }

        install(Logging) {
            level = LogLevel.INFO
        }

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            header("Accept", "application/json")
        }
    }
}
