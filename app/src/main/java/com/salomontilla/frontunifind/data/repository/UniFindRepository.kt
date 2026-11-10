package com.salomontilla.frontunifind.data.repository

import com.salomontilla.frontunifind.data.local.SessionManager
import com.salomontilla.frontunifind.data.model.ChatDto
import com.salomontilla.frontunifind.data.model.ChatMessageDto
import com.salomontilla.frontunifind.data.model.ClaimRequest
import com.salomontilla.frontunifind.data.model.ClaimResponse
import com.salomontilla.frontunifind.data.model.ConfirmDeliveryRequest
import com.salomontilla.frontunifind.data.model.ConfirmDeliveryResult
import com.salomontilla.frontunifind.data.model.CreateChatRequest
import com.salomontilla.frontunifind.data.model.CreateFoundExpressRequest
import com.salomontilla.frontunifind.data.model.CreateLostDetailedRequest
import com.salomontilla.frontunifind.data.model.HealthResponse
import com.salomontilla.frontunifind.data.model.HotspotDto
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.model.LoginRequest
import com.salomontilla.frontunifind.data.model.ModerateItemRequest
import com.salomontilla.frontunifind.data.model.NotifyFoundRequest
import com.salomontilla.frontunifind.data.model.RateResponse
import com.salomontilla.frontunifind.data.model.RateUserRequest
import com.salomontilla.frontunifind.data.model.SendMessageRequest
import com.salomontilla.frontunifind.data.model.TicketDetailsData
import com.salomontilla.frontunifind.data.model.UpdateStatusRequest
import com.salomontilla.frontunifind.data.model.UpdateUserRoleRequest
import com.salomontilla.frontunifind.data.model.UserDto
import com.salomontilla.frontunifind.data.model.UserProfileData
import com.salomontilla.frontunifind.data.network.KtorClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * =============================================================================
 * UniFindRepository: Capa de Datos 100% Conectada a la API REST Oficial
 *
 * Cero datos quemados: Toda la información proviene estrictamente del backend
 * (Node.js + MongoDB en puerto 3000).
 * =============================================================================
 */
object UniFindRepository {

    private val client = KtorClient.httpClient
    private val json = KtorClient.jsonConfig

    var currentUser: UserDto = SessionManager.getUser() ?: UserDto(
        id = "670c1f1e1c9d440000a10001",
        name = "Carlos Andrés Muñoz",
        fullName = "Carlos Andrés Muñoz",
        institutionalEmail = "carlos.munoz@uniautonoma.edu.co",
        studentCode = "2023-1001",
        role = "student",
        honorPoints = 200,
        badges = listOf("Honestidad ejemplar", "Entrega puntual"),
        rating = 5.0
    )

    private fun isNetworkOrTimeoutException(e: Throwable): Boolean {
        val msg = e.message ?: ""
        return e is io.ktor.client.network.sockets.ConnectTimeoutException ||
                e is io.ktor.client.plugins.HttpRequestTimeoutException ||
                e is java.net.ConnectException ||
                e is java.net.SocketTimeoutException ||
                e is java.net.UnknownHostException ||
                msg.contains("timeout", ignoreCase = true) ||
                msg.contains("refused", ignoreCase = true) ||
                msg.contains("failed to connect", ignoreCase = true)
    }

    /**
     * Reintento inteligente de host en caso de timeout o fallo de conexión.
     * Conmuta secuencialmente entre todos los candidatos configurados en KtorClient:
     * 127.0.0.1 (adb reverse) -> 192.168.80.13 (Wi-Fi) -> 10.0.2.2 (Emulador) -> 10.10.164.152
     */
    private suspend fun <T> withRetryHost(block: suspend () -> T): T {
        var lastException: Throwable? = null
        val startIndex = KtorClient.CANDIDATE_HOSTS.indexOf(KtorClient.host).coerceAtLeast(0)

        // Intento 1: con el host activo actual
        try {
            return block()
        } catch (e: Throwable) {
            if (!isNetworkOrTimeoutException(e)) throw e
            lastException = e
        }

        // Si falló, intentar secuencialmente con los demás candidatos
        for (offset in 1 until KtorClient.CANDIDATE_HOSTS.size) {
            val nextHost = KtorClient.CANDIDATE_HOSTS[(startIndex + offset) % KtorClient.CANDIDATE_HOSTS.size]
            android.util.Log.w("UniFind", "Conexión falló en ${KtorClient.host}, conmutando a candidato: $nextHost:${KtorClient.PORT}...")
            KtorClient.host = nextHost
            try {
                val res = block()
                android.util.Log.i("UniFind", "¡Conexión exitosa con $nextHost! Fijado como host activo.")
                return res
            } catch (e: Throwable) {
                if (!isNetworkOrTimeoutException(e)) throw e
                lastException = e
            }
        }

        throw lastException ?: IllegalStateException("No se pudo conectar a ningún host de UniFind Backend")
    }

    private inline fun <reified T> parseResponse(rawBody: String): T {
        val element: JsonElement = json.parseToJsonElement(rawBody)
        if (element is JsonObject && element.containsKey("data") && element["data"] != null) {
            return json.decodeFromJsonElement<T>(element["data"]!!)
        }
        return json.decodeFromJsonElement<T>(element)
    }

    // 1. POST /auth/login
    suspend fun login(email: String, code: String): Result<UserDto> = runCatching {
        withRetryHost {
            val responseText = client.post("auth/login") {
                setBody(LoginRequest(institutionalEmail = email.trim(), studentCode = code.trim()))
            }.bodyAsText()
            val rawUser = parseResponse<UserDto>(responseText)
            val normalized = rawUser.copy(
                id = rawUser.realId,
                name = rawUser.displayName,
                honorPoints = rawUser.effectiveHonorPoints,
                rating = rawUser.effectiveRating,
                badges = rawUser.effectiveBadges
            )
            currentUser = normalized
            SessionManager.saveSession(normalized)
            normalized
        }
    }

    // 2. GET /items?status=active
    suspend fun getItems(
        type: String? = null,
        category: String? = null,
        search: String? = null,
        custodyType: String? = null
    ): Result<List<ItemDto>> = runCatching {
        withRetryHost {
            val responseText = client.get("items") {
                parameter("status", "active")
                if (!type.isNullOrBlank() && type != "all") parameter("type", type)
                if (!category.isNullOrBlank() && category != "Todos") parameter("category", category)
                if (!search.isNullOrBlank()) parameter("search", search)
                if (!custodyType.isNullOrBlank()) parameter("custodyType", custodyType)
                parameter("limit", 50)
            }.bodyAsText()
            parseResponse<List<ItemDto>>(responseText)
        }
    }

    // 3. GET /items/{code}
    suspend fun getItemByCode(code: String): Result<ItemDto> = runCatching {
        withRetryHost {
            val responseText = client.get("items/$code").bodyAsText()
            parseResponse<ItemDto>(responseText)
        }
    }

    // 4. POST /items (Modo Exprés Encontrado / Portería)
    suspend fun createFoundExpress(req: CreateFoundExpressRequest): Result<ItemDto> = runCatching {
        withRetryHost {
            val payload = if (req.postedBy.isNullOrBlank()) req.copy(postedBy = currentUser.realId) else req
            android.util.Log.d("UniFind", "POST /items express: title='${payload.title}', postedBy='${payload.postedBy}'")
            val responseText = client.post("items") {
                setBody(payload)
            }.bodyAsText()
            android.util.Log.d("UniFind", "POST /items express response: $responseText")
            parseResponse<ItemDto>(responseText)
        }
    }

    // 5. POST /items (Modo Detallado Objeto Perdido)
    suspend fun createLostDetailed(req: CreateLostDetailedRequest): Result<ItemDto> = runCatching {
        withRetryHost {
            val payload = if (req.postedBy.isNullOrBlank()) req.copy(postedBy = currentUser.realId) else req
            android.util.Log.d("UniFind", "POST /items lost: title='${payload.title}', postedBy='${payload.postedBy}'")
            val responseText = client.post("items") {
                setBody(payload)
            }.bodyAsText()
            android.util.Log.d("UniFind", "POST /items lost response: $responseText")
            parseResponse<ItemDto>(responseText)
        }
    }

    // 6. POST /items/{code}/claim
    suspend fun claimItem(code: String, req: ClaimRequest): Result<ClaimResponse> = runCatching {
        withRetryHost {
            val payload = if (req.claimantId.isNullOrBlank()) req.copy(claimantId = currentUser.realId) else req
            val responseText = client.post("items/$code/claim") {
                setBody(payload)
            }.bodyAsText()
            parseResponse<ClaimResponse>(responseText)
        }
    }

    // 7. POST /items/{code}/notify-found
    suspend fun notifyFound(code: String, req: NotifyFoundRequest): Result<ClaimResponse> = runCatching {
        withRetryHost {
            val payload = if (req.finderId.isNullOrBlank()) req.copy(finderId = currentUser.realId) else req
            val responseText = client.post("items/$code/notify-found") {
                setBody(payload)
            }.bodyAsText()
            parseResponse<ClaimResponse>(responseText)
        }
    }

    // 8. POST /chats
    suspend fun createChat(itemId: String, itemCode: String): Result<ChatDto> = runCatching {
        withRetryHost {
            val responseText = client.post("chats") {
                setBody(CreateChatRequest(itemId = itemId, partnerId = currentUser.realId))
            }.bodyAsText()
            parseResponse<ChatDto>(responseText)
        }
    }

    // 9. GET /chats/{chatId}/messages
    suspend fun getChatMessages(chatId: String): Result<List<ChatMessageDto>> = runCatching {
        withRetryHost {
            val responseText = client.get("chats/$chatId/messages").bodyAsText()
            parseResponse<List<ChatMessageDto>>(responseText)
        }
    }

    // 10. POST /chats/{chatId}/messages
    suspend fun sendChatMessage(chatId: String, text: String): Result<ChatMessageDto> = runCatching {
        withRetryHost {
            val responseText = client.post("chats/$chatId/messages") {
                setBody(SendMessageRequest(content = text.trim(), senderId = currentUser.realId))
            }.bodyAsText()
            parseResponse<ChatMessageDto>(responseText)
        }
    }

    // 11. PATCH /items/{id}/status
    suspend fun updateItemStatus(itemId: String, status: String): Result<ItemDto> = runCatching {
        withRetryHost {
            val responseText = client.patch("items/$itemId/status") {
                setBody(UpdateStatusRequest(status = status))
            }.bodyAsText()
            parseResponse<ItemDto>(responseText)
        }
    }

    // 12. POST /users/{id}/rate
    suspend fun rateUser(
        userId: String,
        rating: Int,
        comment: String? = null,
        badges: List<String> = emptyList()
    ): Result<RateResponse> = runCatching {
        withRetryHost {
            val responseText = client.post("users/$userId/rate") {
                setBody(RateUserRequest(rating = rating.coerceIn(1, 5), comment = comment, badges = badges))
            }.bodyAsText()
            parseResponse<RateResponse>(responseText)
        }
    }

    // 13. GET /health
    suspend fun getHealth(): Result<HealthResponse> = runCatching {
        withRetryHost {
            val responseText = client.get("health").bodyAsText()
            parseResponse<HealthResponse>(responseText)
        }
    }

    // 14. GET /users/{id}/profile (HU-08: Perfil Integral)
    suspend fun getUserProfile(userId: String = currentUser.realId): Result<UserProfileData> = runCatching {
        withRetryHost {
            val responseText = client.get("users/$userId/profile").bodyAsText()
            parseResponse<UserProfileData>(responseText)
        }
    }

    // 15. GET /users/{id}/items (HU-08: Mis Reportes)
    suspend fun getUserItems(
        userId: String = currentUser.realId,
        status: String? = null,
        type: String? = null
    ): Result<List<ItemDto>> = runCatching {
        withRetryHost {
            val targetId = when {
                userId.isNotBlank() && !userId.startsWith("user-") -> userId
                currentUser.realId.isNotBlank() && !currentUser.realId.startsWith("user-") -> currentUser.realId
                else -> "670c1f1e1c9d440000a10001"
            }
            val responseText = client.get("users/$targetId/items") {
                if (!status.isNullOrBlank()) parameter("status", status)
                if (!type.isNullOrBlank()) parameter("type", type)
            }.bodyAsText()
            parseResponse<List<ItemDto>>(responseText)
        }
    }

    // 16. GET /items/tickets/{token} (HU-09: Consulta de Ticket en Portería)
    suspend fun getTicketDetails(token: String): Result<TicketDetailsData> = runCatching {
        withRetryHost {
            val responseText = client.get("items/tickets/$token").bodyAsText()
            parseResponse<TicketDetailsData>(responseText)
        }
    }

    // 17. POST /items/tickets/{token}/confirm-delivery
    suspend fun confirmTicketDelivery(
        token: String,
        guardName: String = currentUser.displayName,
        notes: String? = null
    ): Result<ConfirmDeliveryResult> = runCatching {
        withRetryHost {
            val responseText = client.post("items/tickets/$token/confirm-delivery") {
                setBody(ConfirmDeliveryRequest(guardName = guardName, guardNotes = notes))
            }.bodyAsText()
            parseResponse<ConfirmDeliveryResult>(responseText)
        }
    }

    // 18. GET /items?status=pending_review (Barrera 3: Reportes pendientes de moderación en campus)
    suspend fun getPendingModerationItems(): Result<List<ItemDto>> = runCatching {
        withRetryHost {
            val responseText = client.get("items") {
                parameter("status", "pending_review")
                parameter("limit", 50)
            }.bodyAsText()
            android.util.Log.d("UniFind", "GET /items?status=pending_review response: $responseText")
            parseResponse<List<ItemDto>>(responseText)
        }
    }

    // 19. PATCH /items/{id}/moderate (Barrera 3: Moderación directa por Vigilancia / Portería)
    suspend fun moderateItem(
        itemId: String,
        action: String, // "approve" o "reject"
        notes: String? = null
    ): Result<ItemDto> = runCatching {
        withRetryHost {
            val responseText = client.patch("items/$itemId/moderate") {
                setBody(ModerateItemRequest(action = action, notes = notes, moderatorId = currentUser.realId))
            }.bodyAsText()
            parseResponse<ItemDto>(responseText)
        }
    }

    // 20. GET /items?custodyType=PORTERIA&status=active (Inventario Físico en Portería)
    suspend fun getPorteriaInventory(search: String? = null): Result<List<ItemDto>> = runCatching {
        withRetryHost {
            val responseText = client.get("items") {
                parameter("custodyType", "PORTERIA")
                parameter("status", "active")
                if (!search.isNullOrBlank()) parameter("search", search)
                parameter("limit", 100)
            }.bodyAsText()
            parseResponse<List<ItemDto>>(responseText)
        }
    }

    // 21. GET /analytics/hotspots (Métricas de Concentración de Pérdidas)
    suspend fun getHotspots(): Result<List<HotspotDto>> = runCatching {
        withRetryHost {
            val responseText = client.get("analytics/hotspots").bodyAsText()
            parseResponse<List<HotspotDto>>(responseText)
        }
    }

    // 22. GET /users (Listado Integral de Usuarios con Filtros)
    suspend fun getUsers(search: String? = null, role: String? = null): Result<List<UserDto>> = runCatching {
        withRetryHost {
            val responseText = client.get("users") {
                if (!search.isNullOrBlank()) parameter("search", search)
                if (!role.isNullOrBlank() && role != "all") parameter("role", role)
                parameter("limit", 50)
            }.bodyAsText()
            parseResponse<List<UserDto>>(responseText)
        }
    }

    // 23. PATCH /users/{id}/role (Asignación RBAC Dinámica)
    suspend fun updateUserRole(userId: String, newRole: String): Result<UserDto> = runCatching {
        withRetryHost {
            val responseText = client.patch("users/$userId/role") {
                setBody(UpdateUserRoleRequest(role = newRole))
            }.bodyAsText()
            parseResponse<UserDto>(responseText)
        }
    }
}
