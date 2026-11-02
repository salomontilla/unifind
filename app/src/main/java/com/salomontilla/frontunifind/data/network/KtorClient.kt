package com.salomontilla.frontunifind.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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
 * Características Clave para Sustentación:
 * 1. CIO Engine: Motor asíncrono no bloqueante basado en Corrutinas de Kotlin.
 * 2. ContentNegotiation: Serialización y deserialización automática JSON con Kotlinx.
 * 3. Moderno & Multiplataforma: Sustituye a Retrofit/OkHttp tradicionales.
 * =============================================================================
 */
object KtorClient {

    // URL base de la API REST del backend
    private const val BASE_URL = "https://api.unifind.uniautonoma.edu.co/api/v1/"

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
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
