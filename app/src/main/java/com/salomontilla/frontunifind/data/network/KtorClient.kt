package com.salomontilla.frontunifind.data.network

import android.os.Build
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
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * =============================================================================
 * KtorClient: Cliente HTTP Asíncrono Moderno para Kotlin & Coroutines
 *
 * Conectado a la API REST oficial de UniFind (Node.js + MongoDB)
 *
 * Resolución Inteligente de Host:
 * - Emulador Android: http://10.0.2.2:3000/api/v1/
 * - Dispositivo Físico en Wi-Fi: http://10.10.164.152:3000/api/v1/
 * - USB / adb reverse / localhost: http://127.0.0.1:3000/api/v1/
 * =============================================================================
 */
object KtorClient {

    const val EMULATOR_HOST = "10.0.2.2"
    const val LAN_HOST = "10.10.164.152"
    const val LOCALHOST = "127.0.0.1"
    const val PORT = 3000

    val isEmulator: Boolean
        get() = try {
            (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || "google_sdk" == Build.PRODUCT)
        } catch (_: Throwable) {
            false
        }

    // Host activo (se conmuta dinámicamente si hay fallo de conexión)
    var host: String = if (isEmulator) EMULATOR_HOST else LAN_HOST
        set(value) {
            field = value
            android.util.Log.i("UniFind", "KtorClient: Conmutando host activo a $value:$PORT")
        }

    val BASE_URL: String
        get() = "http://$host:$PORT/api/v1/"

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
            connectTimeoutMillis = 4_000L
            requestTimeoutMillis = 12_000L
            socketTimeoutMillis = 12_000L
        }

        install(Logging) {
            level = LogLevel.INFO
        }

        defaultRequest {
            url.takeFrom(BASE_URL)
            contentType(ContentType.Application.Json)
            header("Accept", "application/json")
        }
    }
}
