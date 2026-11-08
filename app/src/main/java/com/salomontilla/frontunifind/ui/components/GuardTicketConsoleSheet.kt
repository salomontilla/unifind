package com.salomontilla.frontunifind.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.data.model.ConfirmDeliveryResult
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.model.TicketDetailsData
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.InputStroke
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.RedDanger
import com.salomontilla.frontunifind.ui.theme.RedLight
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite
import com.salomontilla.frontunifind.ui.theme.TextBody
import com.salomontilla.frontunifind.ui.theme.UniautonomaBgLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaNavy
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextPrimary
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary
import com.salomontilla.frontunifind.ui.theme.YellowLight
import kotlinx.coroutines.launch

/**
 * =============================================================================
 * Consola Oficial de Portería (HU-09: Validación y Despacho de Tickets)
 * ModalBottomSheet institucional para verificar tokens TICKET-XXXX-RET y confirmar
 * la entrega física en Portería Principal de la Uniautónoma.
 * =============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardTicketConsoleSheet(
    onDismiss: () -> Unit,
    onDeliveryCompleted: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var tokenInput by remember { mutableStateOf("TICKET-4091-RET") }
    var isLoading by remember { mutableStateOf(false) }
    var isConfirming by remember { mutableStateOf(false) }
    var ticketDetails by remember { mutableStateOf<TicketDetailsData?>(null) }
    var deliveryResult by remember { mutableStateOf<ConfirmDeliveryResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var guardNotes by remember { mutableStateOf("") }

    var activeConsoleTab by remember { mutableIntStateOf(0) } // 0 = Despacho Tickets, 1 = Moderar Objetos (Barrera 3)
    var pendingItems by remember { mutableStateOf<List<ItemDto>>(emptyList()) }
    var isLoadingPending by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(activeConsoleTab) {
        if (activeConsoleTab == 1) {
            isLoadingPending = true
            actionMessage = null
            val res = UniFindRepository.getPendingModerationItems()
            isLoadingPending = false
            pendingItems = res.getOrDefault(emptyList())
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = SurfaceWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(UniautonomaTextSecondary.copy(alpha = 0.35f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Institucional
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = UniautonomaBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Consola Oficial de Portería",
                fontFamily = MuseoSansRounded,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = UniautonomaNavy
            )

            Text(
                text = "Validación y Despacho Físico de Pertenencias • Uniautónoma",
                fontFamily = MuseoSansRounded,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = UniautonomaTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SELECTOR DE PESTAÑAS: VALIDAR TICKET vs MODERAR REPORTES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlueLight)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeConsoleTab == 0) UniautonomaNavy else Color.Transparent)
                        .clickable { activeConsoleTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Validar Ticket",
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeConsoleTab == 0) SurfaceWhite else UniautonomaTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeConsoleTab == 1) UniautonomaNavy else Color.Transparent)
                        .clickable { activeConsoleTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = "Moderar Objetos",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeConsoleTab == 1) SurfaceWhite else UniautonomaTextSecondary
                        )
                        if (pendingItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(UniautonomaOrange)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${pendingItems.size}",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activeConsoleTab == 0) {
            if (deliveryResult == null) {
                // Entrada del Token
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = {
                        tokenInput = it.uppercase()
                        errorMessage = null
                    },
                    label = { Text("Código de Ticket Digital *", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                    placeholder = { Text("Ej. TICKET-4091-RET", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = UniautonomaOrange
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UniautonomaBlue,
                        unfocusedBorderColor = InputStroke
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Chips de acceso rápido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ejemplos:",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaTextSecondary
                    )
                    listOf("TICKET-4091-RET", "TICKET-2045-RET").forEach { sample ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(BlueLight)
                                .clickable { tokenInput = sample }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = sample,
                                fontFamily = MuseoSansRounded,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (tokenInput.trim().isBlank()) {
                            errorMessage = "Ingresa un código de ticket válido"
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val res = UniFindRepository.getTicketDetails(tokenInput.trim())
                            isLoading = false
                            val details = res.getOrNull()
                            if (details != null && details.isValid) {
                                ticketDetails = details
                            } else {
                                errorMessage = "Ticket no encontrado o expirado en el sistema"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UniautonomaNavy,
                        contentColor = SurfaceWhite
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Consultando en UniFind API...", fontFamily = MuseoSansRounded, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Consultar Ticket en Portería", fontFamily = MuseoSansRounded, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        color = UniautonomaOrange,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Ficha del Ticket Encontrado
                val currentTicket = ticketDetails
                if (currentTicket != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UniautonomaBgLight,
                        border = BorderStroke(1.dp, CardStroke)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentTicket.deliveryToken,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaNavy
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(GreenLight)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "VÁLIDO PARA RETIRO",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Datos del Objeto
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = UniautonomaBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${currentTicket.item.title} (#${currentTicket.item.code})",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = UniautonomaOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentTicket.pickupLocation,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Datos del Reclamante
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceWhite)
                                    .border(1.dp, CardStroke, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "RECLAMANTE VERIFICADO",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = UniautonomaNavy, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = currentTicket.claimant.fullName,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UniautonomaNavy
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = UniautonomaTextSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Código: ${currentTicket.claimant.studentCode} • ${currentTicket.claimant.institutionalEmail}",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                }
                                if (currentTicket.claimant.phone.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = UniautonomaTextSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Tel: ${currentTicket.claimant.phone}",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 11.sp,
                                            color = UniautonomaTextSecondary
                                        )
                                    }
                                }
                            }

                            if (!currentTicket.ownershipProofText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Prueba de propiedad: \"${currentTicket.ownershipProofText}\"",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = guardNotes,
                        onValueChange = { guardNotes = it },
                        label = { Text("Notas de Portería (Opcional)", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                        placeholder = { Text("Ej. Carné estudiantil físico verificado", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            isConfirming = true
                            scope.launch {
                                val res = UniFindRepository.confirmTicketDelivery(
                                    token = currentTicket.deliveryToken,
                                    guardName = "Portería Principal • Turno Diurno",
                                    notes = guardNotes.trim().takeIf { it.isNotBlank() }
                                )
                                isConfirming = false
                                deliveryResult = res.getOrNull()
                                onDeliveryCompleted?.invoke()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenSuccess,
                            contentColor = SurfaceWhite
                        )
                    ) {
                        if (isConfirming) {
                            CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmando Despacho...", fontFamily = MuseoSansRounded, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmar Entrega Física", fontFamily = MuseoSansRounded, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Confirmación Visual de Despacho Exitoso
                val delivered = deliveryResult!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GreenLight)
                        .border(1.5.dp, GreenSuccess, RoundedCornerShape(18.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GreenSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "¡Entrega Física Confirmada!",
                        fontFamily = MuseoSansRounded,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = UniautonomaNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "El objeto #${delivered.itemCode} (${delivered.title}) ha sido entregado formalmente y transicionado a estado 'returned' en la red UniFind.",
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        color = TextBody,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ticket: ${delivered.deliveryToken} • Atendido por: ${delivered.handledBy}",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenSuccess
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UniautonomaNavy,
                        contentColor = SurfaceWhite
                    )
                ) {
                    Text("Cerrar Consola", fontFamily = MuseoSansRounded, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            } else {
                // =============================================================
                // TAB 1: MODERACIÓN DE OBJETOS EN PORTERÍA (BARRERA 3)
                // =============================================================
                if (actionMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = GreenLight,
                        border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = actionMessage ?: "",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenSuccess
                            )
                        }
                    }
                }

                if (isLoadingPending) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UniautonomaBlue, modifier = Modifier.size(32.dp))
                    }
                } else if (pendingItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sin reportes pendientes",
                            fontFamily = MuseoSansRounded,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Todos los objetos del campus han sido verificados y aprobados para el Feed.",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            color = UniautonomaTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "Reportes en espera de verificación de Portería (Barrera 3):",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaTextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    pendingItems.forEach { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceWhite,
                            border = BorderStroke(1.dp, CardStroke),
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(UniautonomaNavy)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "#${item.code}",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SurfaceWhite
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(YellowLight)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (item.custodyType == "PORTERIA") "EN PORTERÍA" else "PERSONAL",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UniautonomaOrange
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(YellowLight)
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "BARRERA 3",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = UniautonomaOrange
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.title,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaTextPrimary
                                )

                                Text(
                                    text = "${item.category} • ${item.displayLocation}",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    color = UniautonomaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                val res = UniFindRepository.moderateItem(item.id, "approve", "Aprobado por Vigilancia en Portería")
                                                if (res.isSuccess) {
                                                    actionMessage = "¡Reporte #${item.code} aprobado y publicado en Feed!"
                                                    pendingItems = pendingItems.filter { it.id != item.id }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Aprobar", fontFamily = MuseoSansRounded, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                val res = UniFindRepository.moderateItem(item.id, "reject", "Rechazado por Vigilancia en Portería")
                                                if (res.isSuccess) {
                                                    actionMessage = "Reporte #${item.code} rechazado."
                                                    pendingItems = pendingItems.filter { it.id != item.id }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rechazar", fontFamily = MuseoSansRounded, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
