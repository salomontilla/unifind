package com.salomontilla.frontunifind.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Feed : Screen("feed")
    object Report : Screen("report")
    object Messages : Screen("messages")
    object Honor : Screen("honor")
    object Profile : Screen("profile")
    data class Detail(val itemId: String = "UAC-8841") : Screen("detail/{itemId}") {
        companion object {
            const val ROUTE = "detail/{itemId}"
            fun createRoute(itemId: String) = "detail/$itemId"
        }
    }
    data class Chat(val chatId: String = "chat-1") : Screen("chat/{chatId}") {
        companion object {
            const val ROUTE = "chat/{chatId}"
            fun createRoute(chatId: String) = "chat/$chatId"
        }
    }
}
