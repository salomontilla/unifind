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

// =============================================================================
// DTOs Oficiales de la API REST de UniFind (Node.js + MongoDB)
// =============================================================================

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val statusCode: Int? = null,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

@Serializable
data class ItemDto(
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val type: String = "found", // "found" o "lost"
    val status: String = "active", // "active", "in_process", "returned"
    val category: String = "",
    val description: String? = null,
    val custodyType: String? = null, // "PORTERIA" o "PERSONAL"
    val distinguishingMarks: String? = null,
    val brandAndColor: String? = null,
    val photoUrl: String? = null,
    val campusLocation: String? = null,
    val campusZone: String? = null,
    val roomReference: String? = null,
    val locationReference: String? = null,
    val timeAgo: String? = null,
    val securityQuestion: String? = null,
    val requiresVerification: Boolean = false,
    val rewardPoints: Int = 100,
    val moderationNotes: String? = null
) {
    val displayLocation: String
        get() {
            val base = campusLocation?.takeIf { it.isNotBlank() }
                ?: campusZone?.takeIf { it.isNotBlank() }
                ?: "Campus Sede Principal"
            val ref = roomReference?.takeIf { it.isNotBlank() }
                ?: locationReference?.takeIf { it.isNotBlank() }
            return if (ref != null) "$base · $ref" else base
        }

    val displayDescription: String
        get() = description?.takeIf { it.isNotBlank() }
            ?: distinguishingMarks?.takeIf { it.isNotBlank() }
            ?: brandAndColor?.takeIf { it.isNotBlank() }
            ?: "Registrado en la red institucional UniFind Uniautónoma."
}

@Serializable
data class CreateFoundExpressRequest(
    val title: String,
    val type: String = "found",
    val category: String,
    val custodyType: String, // "PORTERIA" o "PERSONAL"
    val campusZone: String,
    val locationReference: String? = null,
    val brandAndColor: String? = null,
    val photoUrl: String
)

@Serializable
data class CreateLostDetailedRequest(
    val title: String,
    val type: String = "lost",
    val category: String,
    val distinguishingMarks: String,
    val brandAndColor: String? = null,
    val campusZone: String? = null,
    val locationReference: String? = null,
    val photoUrl: String? = null
)

@Serializable
data class ClaimRequest(
    val ownershipProofText: String,
    val securityAnswer: String? = null
)

@Serializable
data class ClaimResponse(
    val success: Boolean = true,
    val claimApproved: Boolean = false,
    val custodyType: String = "",
    val deliveryToken: String? = null,
    val pickupLocation: String? = null,
    val instructions: String? = null,
    val chatReady: Boolean? = null,
    val chatId: String? = null
)

@Serializable
data class ModerateItemRequest(
    val action: String,
    val notes: String? = null,
    val moderatorId: String? = null
)

@Serializable
data class NotifyFoundRequest(
    val action: String, // "LEFT_AT_PORTERIA" o "HAVE_IT_WITH_ME"
    val finderNote: String? = null
)

@Serializable
data class RateUserRequest(
    val rating: Int,
    val comment: String? = null,
    val badges: List<String> = emptyList()
)

@Serializable
data class RateResponse(
    val success: Boolean = true,
    val honorPoints: Int = 300,
    val pointsAdded: Int = 100,
    val message: String? = null
)

@Serializable
data class LoginRequest(
    val institutionalEmail: String,
    val studentCode: String
)

@Serializable
data class UserDto(
    val id: String = "",
    val name: String? = null,
    val institutionalEmail: String = "",
    val studentCode: String = "",
    val role: String = "student",
    val honorPoints: Int = 200,
    val badges: List<String> = emptyList(),
    val rating: Double = 5.0
)

@Serializable
data class ChatDto(
    val id: String = "",
    val itemId: String = "",
    val itemCode: String? = null,
    val itemTitle: String? = null,
    val pickupLocation: String? = null,
    val status: String = "active"
)

@Serializable
data class ChatMessageDto(
    val id: String = "",
    val chatId: String = "",
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: String = "",
    val isMine: Boolean = false,
    val isVerified: Boolean = true
)

@Serializable
data class SendMessageRequest(
    val content: String
)

@Serializable
data class UpdateStatusRequest(
    val status: String // "returned", "in_process", etc.
)

// =============================================================================
// Modelos UI Auxiliares
// =============================================================================

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

// =============================================================================
// DTOs Oficiales Sprint 2 (HU-08, HU-09, HU-10)
// =============================================================================

@Serializable
data class HealthResponse(
    val status: String = "ok",
    val uptimeSeconds: Double? = null,
    val timestamp: String? = null
)

// HU-08: Perfil Integral, Rango de Honor y Estadísticas
@Serializable
data class UserProfileData(
    val user: UserProfileDetails,
    val stats: UserStats,
    val recentItems: List<ItemDto> = emptyList()
)

@Serializable
data class UserProfileDetails(
    val id: String,
    val fullName: String,
    val institutionalEmail: String,
    val studentCode: String,
    val phone: String = "",
    val avatarUrl: String = "",
    val reputation: ReputationDetails
)

@Serializable
data class ReputationDetails(
    val rating: Double = 5.0,
    val returnedCount: Int = 0,
    val honorPoints: Int = 200,
    val rank: String = "Guardián Novato", // "Guardián Novato", "Custodio Solidario", "Héroe del Campus"
    val badges: List<String> = emptyList()
)

@Serializable
data class UserStats(
    val totalReported: Int = 0,
    val reportedLost: Int = 0,
    val reportedFound: Int = 0,
    val returnedSuccess: Int = 0
)

@Serializable
data class UpdateProfileRequest(
    val fullName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null
)

// HU-09: Validación y Despacho de Tickets en Portería
@Serializable
data class TicketDetailsData(
    val deliveryToken: String,
    val isValid: Boolean = true,
    val isDelivered: Boolean = false,
    val status: String = "valid",
    val pickupLocation: String = "Portería Principal • Sede La Casona",
    val item: TicketItemInfo,
    val claimant: TicketClaimantInfo,
    val ownershipProofText: String? = null,
    val claimedAt: String? = null
)

@Serializable
data class TicketItemInfo(
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val category: String = "",
    val custodyType: String? = null,
    val photoUrl: String? = null,
    val campusLocation: String? = null,
    val specificArea: String? = null
)

@Serializable
data class TicketClaimantInfo(
    val id: String? = null,
    val fullName: String,
    val studentCode: String,
    val institutionalEmail: String,
    val phone: String = ""
)

@Serializable
data class ConfirmDeliveryRequest(
    val guardName: String = "Personal de Portería",
    val guardNotes: String? = null
)

@Serializable
data class ConfirmDeliveryResult(
    val deliveryToken: String,
    val itemCode: String,
    val title: String,
    val status: String = "returned",
    val deliveredAt: String = "",
    val handledBy: String = "Personal de Portería",
    val guardNotes: String? = null
)
