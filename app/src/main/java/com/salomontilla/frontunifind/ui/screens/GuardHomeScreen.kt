package com.salomontilla.frontunifind.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import com.salomontilla.frontunifind.data.model.ConfirmDeliveryResult
import com.salomontilla.frontunifind.data.model.CreateFoundExpressRequest
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.model.TicketDetailsData
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.components.GuardInventoryCardSkeleton
import com.salomontilla.frontunifind.ui.components.GuardModerationCardSkeleton
import com.salomontilla.frontunifind.ui.components.rememberShimmerBrush
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.DividerStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.InputStroke
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.RedDanger
import com.salomontilla.frontunifind.ui.theme.RedLight
import com.salomontilla.frontunifind.ui.theme.ScreenBackground
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite
import com.salomontilla.frontunifind.ui.theme.TextBody
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaNavy
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextPrimary
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary
import com.salomontilla.frontunifind.ui.theme.YellowLight
import kotlinx.coroutines.launch

/**
 * =============================================================================
 * PANTALLA OFICIAL DE VIGILANCIA: GuardHomeScreen
 * Interfaz especializada y exclusiva para el rol GUARD (Personal de Portería)
 * Elimina el carné estudiantil y elementos irrelevantes, enfocándose 100% en:
 * 1. Despacho y entrega física de objetos con Ticket Digital (HU-09)
 * 2. Moderación y autorización de publicaciones (Barrera 3 de Seguridad)
 * 3. Inventario físico bajo custodia en casillero de Portería
 * =============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardHomeScreen(
    onNavigateToFeed: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val guardUser = UniFindRepository.currentUser
    val shimmerBrush = rememberShimmerBrush()

    // 0 = Despacho (Tickets), 1 = Moderación (Barrera 3), 2 = Inventario Portería
    var selectedTab by remember { mutableIntStateOf(0) }

    // Estados de Métricas
    var custodyItems by remember { mutableStateOf<List<ItemDto>>(emptyList()) }
    var pendingItems by remember { mutableStateOf<List<ItemDto>>(emptyList()) }
    var completedCount by remember { mutableIntStateOf(1) }

    // Estados de Tab 0: Despacho de Ticket
    var tokenInput by remember { mutableStateOf("TICKET-4091-RET") }
    var isCheckingTicket by remember { mutableStateOf(false) }
    var ticketDetails by remember { mutableStateOf<TicketDetailsData?>(null) }
    var ticketError by remember { mutableStateOf<String?>(null) }
    var isConfirmingDelivery by remember { mutableStateOf(false) }
    var deliveryResult by remember { mutableStateOf<ConfirmDeliveryResult?>(null) }
    var guardNotes by remember { mutableStateOf("") }

    // Estados de Tab 1: Moderación (Barrera 3)
    var isLoadingPending by remember { mutableStateOf(false) }
    var moderationMessage by remember { mutableStateOf<String?>(null) }

    // Estados de Tab 2: Inventario
    var inventorySearch by remember { mutableStateOf("") }
    var isLoadingInventory by remember { mutableStateOf(false) }
    var showRegisterModal by remember { mutableStateOf(false) }

    // Función para refrescar datos operativos
    fun refreshData() {
        scope.launch {
            val pendingRes = UniFindRepository.getPendingModerationItems()
            pendingItems = pendingRes.getOrDefault(emptyList())

            val custodyRes = UniFindRepository.getPorteriaInventory()
            custodyItems = custodyRes.getOrDefault(emptyList())
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        bottomBar = {
            // =================================================================
            // BARRA DE NAVEGACIÓN ESPECIALIZADA PARA EL ROL GUARD
            // =================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = SurfaceWhite,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, CardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pestaña 1: Despacho (Tickets)
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = 0 }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = "Despachar",
                            tint = if (selectedTab == 0) UniautonomaBlue else UniautonomaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Despachar",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 0) UniautonomaBlue else UniautonomaTextSecondary
                        )
                    }

                    // Pestaña 2: Moderación (Barrera 3)
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTab = 1
                                refreshData()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Moderar",
                                tint = if (selectedTab == 1) UniautonomaOrange else UniautonomaTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            if (pendingItems.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(UniautonomaOrange)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Moderar (${pendingItems.size})",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 1) UniautonomaOrange else UniautonomaTextSecondary
                        )
                    }

                    // Pestaña 3: Inventario en Portería
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTab = 2
                                refreshData()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Custodia",
                            tint = if (selectedTab == 2) UniautonomaNavy else UniautonomaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Custodia (${custodyItems.size})",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 2) UniautonomaNavy else UniautonomaTextSecondary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // =================================================================
            // HEADER OFICIAL DE VIGILANCIA & SEGURIDAD DEL CAMPUS
            // =================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UniautonomaNavy)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(UniautonomaOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Consola de Portería & Custodia",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Campus La Casona • Popayán",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = SurfaceWhite.copy(alpha = 0.75f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Botón Ver Perfil de Guardia
                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Mi Carné de Guardia",
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Botón Salir / Cerrar Turno
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Cerrar Turno",
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tarjeta de Identificación del Guardia
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceWhite.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, SurfaceWhite.copy(alpha = 0.18f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = guardUser.name ?: "Andrés Felipe Salazar",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Código: ${guardUser.studentCode.ifEmpty { "GUARD-001" }} • Guardia Oficial",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = SurfaceWhite.copy(alpha = 0.7f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(GreenSuccess)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "TURNO ACTIVO",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // MÉTRICAS EN TIEMPO REAL (KPI DE PORTERÍA)
            // =================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Métrica 1: En Custodia
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, CardStroke),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${custodyItems.size}",
                            fontFamily = MuseoSansRounded,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaNavy
                        )
                        Text(
                            text = "En Custodia",
                            fontFamily = MuseoSansRounded,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextSecondary
                        )
                    }
                }

                // Métrica 2: Por Moderar (Barrera 3)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 },
                    shape = RoundedCornerShape(16.dp),
                    color = if (pendingItems.isNotEmpty()) YellowLight else SurfaceWhite,
                    border = BorderStroke(1.dp, if (pendingItems.isNotEmpty()) UniautonomaOrange.copy(alpha = 0.4f) else CardStroke),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${pendingItems.size}",
                            fontFamily = MuseoSansRounded,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaOrange
                        )
                        Text(
                            text = "Por Moderar",
                            fontFamily = MuseoSansRounded,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaOrange
                        )
                    }
                }

                // Métrica 3: Despachados
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, CardStroke),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$completedCount",
                            fontFamily = MuseoSansRounded,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenSuccess
                        )
                        Text(
                            text = "Entregados",
                            fontFamily = MuseoSansRounded,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextSecondary
                        )
                    }
                }
            }

            // =================================================================
            // SELECTOR DE OPERACIONES PRINCIPALES (TABS)
            // =================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = RoundedCornerShape(14.dp),
                color = BlueLight,
                border = BorderStroke(1.dp, CardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) UniautonomaNavy else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎟️ Despachar",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SurfaceWhite else UniautonomaTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) UniautonomaNavy else Color.Transparent)
                            .clickable {
                                selectedTab = 1
                                refreshData()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️ Moderar (${pendingItems.size})",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SurfaceWhite else UniautonomaTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 2) UniautonomaNavy else Color.Transparent)
                            .clickable {
                                selectedTab = 2
                                refreshData()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📦 Custodia",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 2) SurfaceWhite else UniautonomaTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =================================================================
            // CONTENIDO SEGÚN LA PESTAÑA ACTIVA
            // =================================================================
            when (selectedTab) {
                // -------------------------------------------------------------
                // TAB 0: DESPACHO CON TICKET DIGITAL
                // -------------------------------------------------------------
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceWhite,
                            border = BorderStroke(1.dp, CardStroke),
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Validación de Ticket de Retiro",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaNavy
                                )
                                Text(
                                    text = "El estudiante presenta este token generado al reclamar el objeto en la app.",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = tokenInput,
                                    onValueChange = {
                                        tokenInput = it.uppercase()
                                        ticketError = null
                                    },
                                    label = { Text("Código de Ticket *", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                                    placeholder = { Text("Ej. TICKET-4091-RET", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                                    singleLine = true,
                                    leadingIcon = {
                                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = UniautonomaOrange)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = UniautonomaBlue,
                                        unfocusedBorderColor = InputStroke
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Chips de prueba rápida
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
                                                .padding(horizontal = 9.dp, vertical = 3.dp)
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

                                if (ticketError != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = ticketError!!,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        color = RedDanger
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        if (tokenInput.isBlank()) {
                                            ticketError = "Ingresa un código de ticket"
                                            return@Button
                                        }
                                        isCheckingTicket = true
                                        ticketError = null
                                        deliveryResult = null
                                        scope.launch {
                                            val res = UniFindRepository.getTicketDetails(tokenInput.trim())
                                            isCheckingTicket = false
                                            val details = res.getOrNull()
                                            if (details != null && details.isValid) {
                                                ticketDetails = details
                                            } else {
                                                ticketError = "Ticket no encontrado o ya fue entregado anteriormente."
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = UniautonomaNavy)
                                ) {
                                    if (isCheckingTicket) {
                                        CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verificar Ticket en Sistema", fontFamily = MuseoSansRounded, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // DETALLES DEL TICKET VERIFICADO
                        AnimatedVisibility(visible = ticketDetails != null && deliveryResult == null) {
                            ticketDetails?.let { details ->
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    color = SurfaceWhite,
                                    border = BorderStroke(1.5.dp, GreenSuccess.copy(alpha = 0.5f)),
                                    shadowElevation = 3.dp
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Cotejo de Pertenencia & Titular",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 13.sp,
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
                                                    text = "TICKET VÁLIDO",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GreenSuccess
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Ficha del Objeto
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            color = BlueLight
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = "#${details.item.code} • ${details.item.title}",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = UniautonomaNavy
                                                )
                                                Text(
                                                    text = "Categoría: ${details.item.category} • Casillero Portería Principal",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    color = UniautonomaTextSecondary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Ficha del Reclamante
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            color = YellowLight
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = "Reclamante: ${details.claimant.fullName}",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = UniautonomaTextPrimary
                                                )
                                                Text(
                                                    text = "Código Estudiantil: ${details.claimant.studentCode}",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = UniautonomaOrange
                                                )
                                                Text(
                                                    text = "Correo: ${details.claimant.institutionalEmail}",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    color = UniautonomaTextSecondary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Prueba presentada: \"${details.ownershipProofText}\"",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                    color = UniautonomaTextPrimary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                            value = guardNotes,
                                            onValueChange = { guardNotes = it },
                                            label = { Text("Nota de Vigilancia (Opcional)", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                                            placeholder = { Text("Ej. Carné físico cotejado en ventanilla", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = UniautonomaBlue)
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Button(
                                            onClick = {
                                                isConfirmingDelivery = true
                                                scope.launch {
                                                    val res = UniFindRepository.confirmTicketDelivery(
                                                        token = details.deliveryToken,
                                                        guardName = guardUser.name ?: "Andrés Felipe Salazar (Vigilancia)",
                                                        notes = guardNotes.ifBlank { "Entrega física validada contra carné estudiantil en Portería." }
                                                    )
                                                    isConfirmingDelivery = false
                                                    val confirmed = res.getOrNull()
                                                    if (confirmed != null) {
                                                        deliveryResult = confirmed
                                                        completedCount += 1
                                                        refreshData()
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                                        ) {
                                            if (isConfirmingDelivery) {
                                                CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                            } else {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Confirmar Entrega Física (Cerrar Caso)", fontFamily = MuseoSansRounded, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // RECIBO DE ENTREGA COMPLETADA
                        AnimatedVisibility(visible = deliveryResult != null) {
                            deliveryResult?.let { delivered ->
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    color = GreenLight,
                                    border = BorderStroke(1.5.dp, GreenSuccess),
                                    shadowElevation = 3.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(GreenSuccess),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(28.dp))
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "¡Entrega Física Completada!",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = UniautonomaNavy,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "El objeto #${delivered.itemCode} ha sido retirado formalmente de Portería y finalizado en el sistema.",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 12.sp,
                                            color = TextBody,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                deliveryResult = null
                                                ticketDetails = null
                                                tokenInput = ""
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = UniautonomaNavy)
                                        ) {
                                            Text("Despachar Otro Ticket", fontFamily = MuseoSansRounded, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // TAB 1: MODERACIÓN PREVIA DE REPORTES (BARRERA 3)
                // -------------------------------------------------------------
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        // Banner Explicativo de Seguridad
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = YellowLight,
                            border = BorderStroke(1.dp, UniautonomaOrange.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = UniautonomaOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Filtro de seguridad activo (Barrera 3): Verifica los objetos dejados en ventanilla o reportados antes de que se publiquen en el Feed del campus.",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaNavy,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        if (moderationMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = GreenLight,
                                border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = moderationMessage ?: "",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoadingPending) {
                            repeat(2) {
                                GuardModerationCardSkeleton(brush = shimmerBrush)
                            }
                        } else if (pendingItems.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = SurfaceWhite,
                                border = BorderStroke(1.dp, CardStroke)
                            ) {
                                Column(
                                    modifier = Modifier.padding(30.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(GreenLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(26.dp))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Sin reportes pendientes de moderación",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UniautonomaNavy
                                    )
                                    Text(
                                        text = "Todos los objetos del campus están verificados y aprobados.",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        color = UniautonomaTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
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
                                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "#${item.code}",
                                                        fontFamily = MuseoSansRounded,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = SurfaceWhite
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(YellowLight)
                                                        .padding(horizontal = 7.dp, vertical = 2.dp)
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
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "BARRERA 3",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = UniautonomaOrange
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

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
                                            fontSize = 11.sp,
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
                                                        val res = UniFindRepository.moderateItem(item.id, "approve", "Aprobado en ventanilla por Vigilancia")
                                                        if (res.isSuccess) {
                                                            moderationMessage = "¡Reporte #${item.code} aprobado y publicado en Feed!"
                                                            refreshData()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Aprobar y Publicar", fontFamily = MuseoSansRounded, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        val res = UniFindRepository.moderateItem(item.id, "reject", "Rechazado por Vigilancia: foto ilegible o contenido no conforme")
                                                        if (res.isSuccess) {
                                                            moderationMessage = "Reporte #${item.code} rechazado."
                                                            refreshData()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
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

                // -------------------------------------------------------------
                // TAB 2: INVENTARIO FÍSICO EN PORTERÍA
                // -------------------------------------------------------------
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Casillero de Portería Principal (${custodyItems.size})",
                                fontFamily = MuseoSansRounded,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UniautonomaNavy
                            )

                            Button(
                                onClick = { showRegisterModal = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = UniautonomaBlue),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Registrar Ingreso", fontFamily = MuseoSansRounded, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Buscador de inventario
                        OutlinedTextField(
                            value = inventorySearch,
                            onValueChange = {
                                inventorySearch = it
                                scope.launch {
                                    val res = UniFindRepository.getPorteriaInventory(it.takeIf { it.isNotBlank() })
                                    custodyItems = res.getOrDefault(emptyList())
                                }
                            },
                            placeholder = { Text("Buscar en casillero por código o nombre...", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UniautonomaTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoadingInventory) {
                            repeat(3) {
                                GuardInventoryCardSkeleton(brush = shimmerBrush)
                            }
                        } else if (custodyItems.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceWhite,
                                border = BorderStroke(1.dp, CardStroke)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = UniautonomaTextSecondary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay objetos en casillero con ese filtro",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                }
                            }
                        } else {
                            custodyItems.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceWhite,
                                    border = BorderStroke(1.dp, CardStroke),
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
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
                                                Text(
                                                    text = item.category,
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = UniautonomaOrange
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = item.title,
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UniautonomaTextPrimary
                                            )
                                            Text(
                                                text = item.displayLocation,
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 10.sp,
                                                color = UniautonomaTextSecondary
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(GreenLight)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "EN CUSTODIA",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = GreenSuccess
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =================================================================
            // ACCESO ALTERNO: VER FEED ESTUDIANTIL
            // =================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clickable { onNavigateToFeed() },
                shape = RoundedCornerShape(14.dp),
                color = SurfaceWhite,
                border = BorderStroke(1.dp, CardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = UniautonomaBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ver Feed Público como Estudiante",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaTextPrimary
                            )
                            Text(
                                text = "Inspecciona cómo ven las publicaciones los estudiantes",
                                fontFamily = MuseoSansRounded,
                                fontSize = 10.sp,
                                color = UniautonomaTextSecondary
                            )
                        }
                    }
                    Text(
                        text = "Abrir Feed →",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // =========================================================================
    // MODAL REGISTRO RÁPIDO EN VENTANILLA DE PORTERÍA
    // =========================================================================
    if (showRegisterModal) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Documentos / Carnés") }
        var newMarks by remember { mutableStateOf("") }
        var isSaving by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showRegisterModal = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                border = BorderStroke(1.dp, CardStroke),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Ingresar Objeto a Portería",
                        fontFamily = MuseoSansRounded,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = UniautonomaNavy
                    )
                    Text(
                        text = "Registra una pertenencia entregada físicamente en ventanilla.",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        color = UniautonomaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título / Objeto *", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                        placeholder = { Text("Ej. Carné Estudiantil Sistemas", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newMarks,
                        onValueChange = { newMarks = it },
                        label = { Text("Señas Particulares (Opcional)", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                        placeholder = { Text("Ej. Portacarné azul con cinta", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showRegisterModal = false },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ScreenBackground, contentColor = UniautonomaTextPrimary)
                        ) {
                            Text("Cancelar", fontFamily = MuseoSansRounded, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (newTitle.isBlank()) return@Button
                                isSaving = true
                                scope.launch {
                                    UniFindRepository.createFoundExpress(
                                        CreateFoundExpressRequest(
                                            title = newTitle.trim(),
                                            type = "found",
                                            category = newCategory,
                                            custodyType = "PORTERIA",
                                            campusZone = "Portería Principal",
                                            locationReference = "Dejado en ventanilla de portería",
                                            brandAndColor = newMarks.trim().takeIf { it.isNotBlank() },
                                            photoUrl = "https://images.unsplash.com/photo-1584438784894-089d6a62b8fa"
                                        )
                                    )
                                    isSaving = false
                                    showRegisterModal = false
                                    refreshData()
                                }
                            },
                            modifier = Modifier.weight(1.2f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UniautonomaBlue)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Guardar en Casillero", fontFamily = MuseoSansRounded, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
