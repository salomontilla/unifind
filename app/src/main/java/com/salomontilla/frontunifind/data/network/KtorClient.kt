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
 * Conectado a la API REST oficial de UniFind (Node.js + MongoDB en Render Cloud)
 *
 * Resolución Inteligente de Host:
 * - Nube Producción: https://api-unifind.onrender.com/api/v1/ (Sin dependencia de Wi-Fi)
 * - Emulador Android: http://10.0.2.2:3000/api/v1/
 * - Dispositivo Físico en Wi-Fi: http://10.10.162.148:3000/api/v1/
 * - USB / adb reverse / localhost: http://127.0.0.1:3000/api/v1/
 * =============================================================================
 */
object KtorClient {

    const val CLOUD_HOST = "api-unifind.onrender.com"
    const val CURRENT_LAN_HOST = "10.10.162.148"
    const val LOCALHOST = "127.0.0.1"
    const val EMULATOR_HOST = "10.0.2.2"
    const val HOME_LAN_HOST = "192.168.80.13"
    const val LEGACY_LAN_HOST = "10.10.164.152"
    const val PORT = 3000

    val CANDIDATE_HOSTS = listOf(
        CLOUD_HOST,         // Render Cloud 24/7 (Prioridad #1 - Accesible desde cualquier red/datos móviles)
        CURRENT_LAN_HOST,   // 10.10.162.148 (Fallback Wi-Fi universidad)
        LOCALHOST,          // 127.0.0.1 (Fallback USB / adb reverse)
        EMULATOR_HOST,      // 10.0.2.2 (Fallback emulador)
        HOME_LAN_HOST       // 192.168.80.13 (Fallback Wi-Fi casa)
    )

    val isEmulator: Boolean
        get() = try {
            Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk", ignoreCase = true) ||
                    Build.MODEL.contains("Emulator", ignoreCase = true) ||
                    Build.MODEL.contains("Android SDK", ignoreCase = true) ||
                    Build.MODEL.contains("sdk_gphone", ignoreCase = true) ||
                    Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
                    Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
                    Build.PRODUCT.contains("sdk_gphone", ignoreCase = true) ||
                    Build.PRODUCT.contains("google_sdk", ignoreCase = true) ||
                    Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||
                    (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        } catch (_: Throwable) {
            false
        }

    // Host activo (comienza por defecto en la nube de Render)
    var host: String = CLOUD_HOST
        set(value) {
            field = value
            android.util.Log.i("UniFind", "KtorClient: Conmutando host activo a $value")
        }

    val displayHost: String
        get() = if (host == CLOUD_HOST) "Nube Render" else "$host:$PORT"

    val BASE_URL: String
        get() = if (host == CLOUD_HOST) {
            "https://$host/api/v1/"
        } else {
            "http://$host:$PORT/api/v1/"
        }

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
            connectTimeoutMillis = 15_000L
            requestTimeoutMillis = 20_000L
            socketTimeoutMillis = 20_000L
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
