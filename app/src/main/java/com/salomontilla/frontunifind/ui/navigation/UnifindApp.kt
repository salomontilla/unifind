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
import com.salomontilla.frontunifind.data.local.SessionManager
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.screens.AdminHomeScreen
import com.salomontilla.frontunifind.ui.screens.ChatScreen
import com.salomontilla.frontunifind.ui.screens.DetailScreen
import com.salomontilla.frontunifind.ui.screens.FeedScreen
import com.salomontilla.frontunifind.ui.screens.GuardHomeScreen
import com.salomontilla.frontunifind.ui.screens.HonorScreen
import com.salomontilla.frontunifind.ui.screens.LoginScreen
import com.salomontilla.frontunifind.ui.screens.MessagesScreen
import com.salomontilla.frontunifind.ui.screens.ProfileScreen
import com.salomontilla.frontunifind.ui.screens.RatingModal
import com.salomontilla.frontunifind.ui.screens.ReportScreen

/**
 * =============================================================================
 * UnifindApp: Orquestador Principal de Navegación y Vistas en Jetpack Compose
 * =============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifindApp() {
    val initialScreen = remember {
        if (SessionManager.isLoggedIn()) {
            val savedUser = SessionManager.getUser()
            if (savedUser != null) {
                UniFindRepository.currentUser = savedUser
                when {
                    savedUser.isGuard -> Screen.GuardHome
                    savedUser.isAdmin -> Screen.AdminHome
                    else -> Screen.Feed
                }
            } else {
                Screen.Feed
            }
        } else {
            Screen.Login
        }
    }
    val backStack = remember { mutableStateListOf<Screen>(initialScreen) }
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
                        onNavigateToLogin = {
                            val user = SessionManager.getUser() ?: UniFindRepository.currentUser
                            when {
                                user.isGuard -> {
                                    backStack.clear()
                                    backStack.add(Screen.GuardHome)
                                }
                                user.isAdmin -> {
                                    backStack.clear()
                                    backStack.add(Screen.AdminHome)
                                }
                                else -> {
                                    navigateTo(Screen.Profile())
                                }
                            }
                        },
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
                        onBack = { navigateBack() },
                        onNavigateToProfileTab = { tab ->
                            navigateTo(Screen.Profile(initialTab = tab))
                        }
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
                        onNavigateToLogin = { navigateTo(Screen.Profile()) }
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
                        onNavigateToLogin = { navigateTo(Screen.Profile()) }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        initialTab = screen.initialTab,
                        onNavigateToFeed = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                        },
                        onNavigateToMessages = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                            backStack.add(Screen.Messages)
                        },
                        onNavigateToHonor = {
                            backStack.clear()
                            backStack.add(Screen.Feed)
                            backStack.add(Screen.Honor)
                        },
                        onNavigateToReport = { navigateTo(Screen.Report) },
                        onNavigateToDetail = { code -> navigateTo(Screen.Detail(code)) },
                        onNavigateToGuardHome = {
                            backStack.clear()
                            backStack.add(Screen.GuardHome)
                        },
                        onNavigateToAdminHome = {
                            backStack.clear()
                            backStack.add(Screen.AdminHome)
                        },
                        onLogout = {
                            SessionManager.clearSession()
                            backStack.clear()
                            backStack.add(Screen.Login)
                        }
                    )
                }

                is Screen.Chat -> {
                    ChatScreen(
                        chatId = screen.chatId,
                        onBack = { navigateBack() },
                        onConfirmDelivery = { showRatingModal = true }
                    )
                }

                is Screen.GuardHome -> {
                    GuardHomeScreen(
                        onNavigateToFeed = { navigateTo(Screen.Feed) },
                        onNavigateToProfile = { navigateTo(Screen.Profile()) },
                        onLogout = {
                            SessionManager.clearSession()
                            backStack.clear()
                            backStack.add(Screen.Login)
                        }
                    )
                }

                is Screen.AdminHome -> {
                    AdminHomeScreen(
                        onNavigateToFeed = { navigateTo(Screen.Feed) },
                        onLogout = {
                            SessionManager.clearSession()
                            backStack.clear()
                            backStack.add(Screen.Login)
                        }
                    )
                }

                is Screen.Login -> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.clear()
                            val user = SessionManager.getUser() ?: UniFindRepository.currentUser
                            when {
                                user.isGuard -> backStack.add(Screen.GuardHome)
                                user.isAdmin -> backStack.add(Screen.AdminHome)
                                else -> backStack.add(Screen.Feed)
                            }
                        }
                    )
                }
            }
        }

        if (showRatingModal) {
            RatingModal(
                onDismiss = { showRatingModal = false }
            )
        }
    }
}
