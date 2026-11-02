package com.salomontilla.frontunifind.data.model

import kotlinx.serialization.Serializable

enum class ReportStatus {
    FOUND,      // Objeto Encontrado
    LOST,       // Objeto Perdido
    RETURNED    // Devuelto con éxito (+100 Pts Honor)
}

enum class ItemCategory(val label: String) {
    ALL("Todos"),
    DOCUMENTS("Documentos"),
    TECH("Electrónica"),
    CLOTHING("Ropa y Accesorios"),
    KEYS("Llaves"),
    BOOKS("Libros y Cuadernos")
}

@Serializable
data class ItemReport(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val campusLocation: String,
    val roomReference: String,
    val timeAgo: String,
    val verifiedFinder: Boolean = true,
    val requiresVerification: Boolean = true,
    val securityQuestion: String = "",
    val securityAnswerExpected: String = "",
    val attributes: Map<String, String> = emptyMap(),
    val rewardPoints: Int = 100
)

@Serializable
data class MessageThread(
    val id: String,
    val code: String,
    val title: String,
    val location: String,
    val lastSender: String,
    val lastMessage: String,
    val timestamp: String,
    val statusBadge: String,
    val unreadCount: Int = 0,
    val isResolved: Boolean = false
)

@Serializable
data class ChatMessage(
    val id: String,
    val senderName: String,
    val messageText: String,
    val timestamp: String,
    val isMine: Boolean,
    val isVerified: Boolean = false
)
