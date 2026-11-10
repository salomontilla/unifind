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

    const val LOCALHOST = "127.0.0.1"
    const val CURRENT_LAN_HOST = "192.168.80.13"
    const val EMULATOR_HOST = "10.0.2.2"
    const val LEGACY_LAN_HOST = "10.10.164.152"
    const val PORT = 3000

    val CANDIDATE_HOSTS = listOf(
        LOCALHOST,          // 127.0.0.1 (adb reverse - directo y rápido para emulador y USB)
        CURRENT_LAN_HOST,   // 192.168.80.13 (IP Wi-Fi activa para dispositivos móviles en la misma red)
        EMULATOR_HOST,      // 10.0.2.2 (Fallback QEMU SLIRP)
        LEGACY_LAN_HOST     // 10.10.164.152 (Fallback red universidad)
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

    // Host activo (comienza con LOCALHOST si es emulador o CURRENT_LAN_HOST si es móvil físico)
    var host: String = if (isEmulator) LOCALHOST else CURRENT_LAN_HOST
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
            connectTimeoutMillis = 2_500L
            requestTimeoutMillis = 10_000L
            socketTimeoutMillis = 10_000L
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
