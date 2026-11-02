package com.salomontilla.frontunifind.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.salomontilla.frontunifind.ui.screens.ChatScreen
import com.salomontilla.frontunifind.ui.screens.DetailScreen
import com.salomontilla.frontunifind.ui.screens.FeedScreen
import com.salomontilla.frontunifind.ui.screens.HonorScreen
import com.salomontilla.frontunifind.ui.screens.LoginScreen
import com.salomontilla.frontunifind.ui.screens.MessagesScreen
import com.salomontilla.frontunifind.ui.screens.RatingModal
import com.salomontilla.frontunifind.ui.screens.ReportScreen

/**
 * =============================================================================
 * UnifindApp: Orquestador Principal de Navegación y Vistas en Jetpack Compose
 *
 * Características para Sustentación:
 * 1. Single Activity Architecture: Toda la aplicación corre sobre un único
 *    contenedor reactivo gobernado por estados de navegación.
 * 2. Backstack Reactivo: Soporte automático del botón Atrás del sistema operativo.
 * 3. ModalBottomSheet: Despliegue contextual del modal de valoración (Pantalla 7).
 * =============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifindApp() {
    // Pila de navegación (Backstack) reactiva iniciando en Login Institucional
    val backStack = remember { mutableStateListOf<Screen>(Screen.Login) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Login

    var showRatingModal by remember { mutableStateOf(false) }

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    // Manejador del botón 'Atrás' físico o por gestos de Android
    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
            when (screen) {
                is Screen.Feed -> {
                    FeedScreen(
                        onNavigateToDetail = { id -> navigateTo(Screen.Detail(id)) },
                        onNavigateToReport = { navigateTo(Screen.Report) },
                        onNavigateToMessages = { navigateTo(Screen.Messages) },
                        onNavigateToHonor = { navigateTo(Screen.Honor) },
                        onNavigateToLogin = { navigateTo(Screen.Login) },
                        onShowHonorModal = { showRatingModal = true }
                    )
                }

                is Screen.Detail -> {
                    DetailScreen(
                        itemId = screen.itemId,
                        onBack = { navigateBack() },
                        onNavigateToChat = { chatId -> navigateTo(Screen.Chat(chatId)) }
                    )
                }

                is Screen.Report -> {
                    ReportScreen(
                        onBack = { navigateBack() }
                    )
                }

                is Screen.Messages -> {
                    MessagesScreen(
                        onNavigateToFeed = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                        },
                        onNavigateToReport = { navigateTo(Screen.Report) },
                        onNavigateToHonor = { navigateTo(Screen.Honor) },
                        onNavigateToChat = { chatId -> navigateTo(Screen.Chat(chatId)) },
                        onShowHonorModal = { showRatingModal = true },
                        onNavigateToLogin = { navigateTo(Screen.Login) }
                    )
                }

                is Screen.Honor -> {
                    HonorScreen(
                        onNavigateToFeed = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                        },
                        onNavigateToMessages = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                            backStack.add(Screen.Messages)
                        },
                        onNavigateToReport = { navigateTo(Screen.Report) },
                        onNavigateToLogin = { navigateTo(Screen.Login) }
                    )
                }

                is Screen.Chat -> {
                    ChatScreen(
                        chatId = screen.chatId,
                        onBack = { navigateBack() },
                        onConfirmDelivery = { showRatingModal = true }
                    )
                }

                is Screen.Login -> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                        }
                    )
                }
            }
        }

        // Modal de Valoración y Confirmación (Pantalla 7)
        if (showRatingModal) {
            RatingModal(
                onDismiss = { showRatingModal = false }
            )
        }
    }
}
