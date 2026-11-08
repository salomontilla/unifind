package com.salomontilla.frontunifind

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.salomontilla.frontunifind.data.local.SessionManager
import com.salomontilla.frontunifind.ui.navigation.UnifindApp
import com.salomontilla.frontunifind.ui.theme.UnifindTheme

/**
 * =============================================================================
 * MainActivity: Punto de Entrada Principal (Single-Activity en Jetpack Compose)
 *
 * Conceptos Clave para Sustentación con la Profesora:
 * 1. Declarative UI (Jetpack Compose): Sustituye completamente el paradigma
 *    imperativo heredado de XML Views y ViewBinding.
 * 2. UnifindTheme: Configuración centralizada de Material Design 3 con tokens
 *    institucionales de Uniautónoma.
 * 3. Ktor & Coroutines Ready: Arquitectura reactiva moderna preparada para
 *    consumir APIs REST asíncronas sin bloqueos del hilo principal (UI Thread).
 * =============================================================================
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            UnifindTheme {
                UnifindApp()
            }
        }
    }
}
