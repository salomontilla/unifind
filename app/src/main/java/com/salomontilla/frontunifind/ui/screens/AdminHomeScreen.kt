package com.salomontilla.frontunifind.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.data.model.HealthResponse
import com.salomontilla.frontunifind.data.model.HotspotDto
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.model.UserDto
import com.salomontilla.frontunifind.data.repository.UniFindRepository
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
import com.salomontilla.frontunifind.ui.theme.UniautonomaBgLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlueLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaNavy
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextPrimary
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary
import com.salomontilla.frontunifind.ui.theme.YellowLight
import kotlinx.coroutines.launch

/**
 * =============================================================================
 * PANTALLA OFICIAL DE ADMINISTRACIÓN: AdminHomeScreen
 * Consola exclusiva para el rol ADMIN / SUPERADMIN de la Corporación Universitaria
 * Autónoma del Cauca.
 *
 * Módulos Funcionales (RBAC Completo):
 * 1. 📊 Métricas Globales del Campus & Hotspots de Pérdida (Analytics Aggregation)
 * 2. 🛡️ Moderación Central de Seguridad (Barrera 3: Aprobación/Rechazo de Publicaciones)
 * 3. 👥 Gestión de Usuarios y Roles Institucionales (RBAC: student, guard, admin)
 * 4. 🏛️ Credencial Oficial de Administración UAC y Health Check de Infraestructura
 * =============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onNavigateToFeed: () -> Unit = {},
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val adminUser = UniFindRepository.currentUser
    val shimmerBrush = rememberShimmerBrush()

    // 0 = Métricas & Hotspots, 1 = Barrera 3 (Moderación), 2 = Usuarios & RBAC, 3 = Credencial Admin
    var selectedTab by remember { mutableIntStateOf(0) }

    // Estados de Métricas & Hotspots
    var hotspots by remember { mutableStateOf<List<HotspotDto>>(emptyList()) }
    var isLoadingHotspots by remember { mutableStateOf(false) }
    var allItemsCount by remember { mutableIntStateOf(6) }
    var returnedCount by remember { mutableIntStateOf(3) }
    var activeCount by remember { mutableIntStateOf(3) }

    // Estados de Moderación (Barrera 3)
    var pendingItems by remember { mutableStateOf<List<ItemDto>>(emptyList()) }
    var isLoadingPending by remember { mutableStateOf(false) }
    var moderationMessage by remember { mutableStateOf<String?>(null) }
    var rejectingItemId by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    // Estados de Usuarios & RBAC
    var userList by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var isLoadingUsers by remember { mutableStateOf(false) }
    var userSearchQuery by remember { mutableStateOf("") }
    var rbacSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Estados de Salud de Servidor
    var serverHealth by remember { mutableStateOf<HealthResponse?>(null) }

    fun refreshAllData() {
        scope.launch {
            isLoadingHotspots = true
            UniFindRepository.getHotspots().onSuccess { hotspots = it }
            isLoadingHotspots = false
        }
        scope.launch {
            isLoadingPending = true
            UniFindRepository.getPendingModerationItems().onSuccess { pendingItems = it }
            isLoadingPending = false
        }
        scope.launch {
            isLoadingUsers = true
            UniFindRepository.getUsers().onSuccess { userList = it }
            isLoadingUsers = false
        }
        scope.launch {
            UniFindRepository.getHealth().onSuccess { serverHealth = it }
        }
        scope.launch {
            UniFindRepository.getItems().onSuccess { items ->
                activeCount = items.size
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshAllData()
    }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = SurfaceWhite,
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, CardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pestaña 0: Métricas
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = 0 }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = "Métricas",
                            tint = if (selectedTab == 0) UniautonomaBlue else UniautonomaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Métricas",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 0) UniautonomaBlue else UniautonomaTextSecondary
                        )
                    }

                    // Pestaña 1: Barrera 3 (Moderación)
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTab = 1
                                refreshAllData()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
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
                            text = "Barrera 3 (${pendingItems.size})",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 1) UniautonomaOrange else UniautonomaTextSecondary
                        )
                    }

                    // Pestaña 2: Usuarios & RBAC
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTab = 2
                                refreshAllData()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Usuarios",
                            tint = if (selectedTab == 2) UniautonomaNavy else UniautonomaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Usuarios",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 2) UniautonomaNavy else UniautonomaTextSecondary
                        )
                    }

                    // Pestaña 3: Credencial Admin
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = 3 }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = if (selectedTab == 3) UniautonomaBlue else UniautonomaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Credencial",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selectedTab == 3) UniautonomaBlue else UniautonomaTextSecondary
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
            // HEADER OFICIAL DE ADMINISTRACIÓN INSTITUCIONAL UAC (#062653)
            // =================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(UniautonomaNavy, Color(0xFF08336E))
                        )
                    )
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
                                    .background(UniautonomaBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Panel Administrador Universitario",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Supervisión Global • Sede La Casona & Principal",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = SurfaceWhite.copy(alpha = 0.75f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { refreshAllData() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refrescar",
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Cerrar Sesión",
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tarjetas de Métricas Rápidas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 1: Activos
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$activeCount",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Activos",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    color = SurfaceWhite.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Card 2: Por Moderar (Barrera 3)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (pendingItems.isNotEmpty()) UniautonomaOrange.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.10f))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${pendingItems.size}",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (pendingItems.isNotEmpty()) UniautonomaOrange else SurfaceWhite
                                )
                                Text(
                                    text = "En Revisión",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    color = SurfaceWhite.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Card 3: Devueltos
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$returnedCount",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GreenSuccess
                                )
                                Text(
                                    text = "Devueltos",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    color = SurfaceWhite.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Card 4: Usuarios
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userList.size.coerceAtLeast(4)}",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                                Text(
                                    text = "Usuarios",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    color = SurfaceWhite.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // CONTENIDO DE PESTAÑAS ADMINISTRATIVAS
            // =================================================================
            when (selectedTab) {
                0 -> {
                    // =========================================================
                    // TAB 0: MÉTRICAS DEL CAMPUS & ANÁLISIS DE HOTSPOTS
                    // =========================================================
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Hotspots de Pérdidas en el Campus",
                            fontFamily = MuseoSansRounded,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaTextPrimary
                        )
                        Text(
                            text = "Zonas críticas identificadas mediante agregación de incidencias",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            color = UniautonomaTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isLoadingHotspots) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = UniautonomaBlue
                            )
                        } else if (hotspots.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceWhite,
                                border = BorderStroke(1.dp, CardStroke)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = UniautonomaTextSecondary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay zonas críticas registradas aún",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UniautonomaTextPrimary
                                    )
                                }
                            }
                        } else {
                            hotspots.forEach { spot ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = SurfaceWhite,
                                    shadowElevation = 2.dp,
                                    border = BorderStroke(1.dp, CardStroke)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(YellowLight),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Place,
                                                    contentDescription = null,
                                                    tint = UniautonomaOrange,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = spot.building.ifBlank { "Edificio Central" },
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = UniautonomaTextPrimary
                                                )
                                                Text(
                                                    text = "${spot.campus} • Categorías: ${spot.categories.joinToString()}",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    color = UniautonomaTextSecondary
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(RedLight)
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "${spot.totalLostItems} Pérdidas",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RedDanger
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Botón de acceso a vista del estudiante para pruebas
                        Button(
                            onClick = onNavigateToFeed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlueLight,
                                contentColor = UniautonomaNavy
                            )
                        ) {
                            Text(
                                text = "👀 Explorar Vista de Estudiante (Feed Comunitario)",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                1 -> {
                    // =========================================================
                    // TAB 1: BARRERA 3 DE SEGURIDAD (MODERACIÓN DE PUBLICACIONES)
                    // =========================================================
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Barrera 3: Moderación Central",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaTextPrimary
                                )
                                Text(
                                    text = "Autoriza o rechaza reportes antes de publicarse",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (pendingItems.isNotEmpty()) YellowLight else GreenLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${pendingItems.size} Pendientes",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pendingItems.isNotEmpty()) UniautonomaOrange else GreenSuccess
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
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GreenSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = moderationMessage ?: "",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isLoadingPending) {
                            GuardModerationCardSkeleton(brush = shimmerBrush)
                            Spacer(modifier = Modifier.height(10.dp))
                            GuardModerationCardSkeleton(brush = shimmerBrush)
                        } else if (pendingItems.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceWhite,
                                border = BorderStroke(1.dp, CardStroke)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(GreenLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = GreenSuccess,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "¡Al día! No hay reportes pendientes",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UniautonomaTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Todos los reportes cumplen la Barrera 3 de Seguridad",
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
                                        .padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = SurfaceWhite,
                                    shadowElevation = 2.dp,
                                    border = BorderStroke(1.dp, CardStroke)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(YellowLight)
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = item.code.ifBlank { "PENDIENTE" },
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = UniautonomaOrange
                                                )
                                            }

                                            Text(
                                                text = if (item.type == "found") "Encontrado" else "Extraviado",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UniautonomaBlue
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = item.title,
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = UniautonomaTextPrimary
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = item.description ?: "Sin descripción detallada.",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 12.sp,
                                            color = UniautonomaTextSecondary
                                        )

                                        if (!item.distinguishingMarks.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(BlueLight)
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = "🔍 Señas: ${item.distinguishingMarks}",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = UniautonomaNavy
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        UniFindRepository.moderateItem(
                                                            itemId = item.id,
                                                            action = "approve"
                                                        ).onSuccess {
                                                            moderationMessage = "Aprobado: ${item.code}"
                                                            pendingItems = pendingItems.filter { it.id != item.id }
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = GreenSuccess,
                                                    contentColor = SurfaceWhite
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Aprobar",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    rejectingItemId = item.id
                                                    rejectReason = ""
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = RedLight,
                                                    contentColor = RedDanger
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Rechazar",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // =========================================================
                    // TAB 2: USUARIOS Y CONTROL DE ROLES RBAC
                    // =========================================================
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Control de Acceso y Roles (RBAC)",
                            fontFamily = MuseoSansRounded,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaTextPrimary
                        )
                        Text(
                            text = "Administra roles institucionales: student, guard, admin",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            color = UniautonomaTextSecondary
                        )

                        if (rbacSuccessMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = GreenLight,
                                border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GreenSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = rbacSuccessMessage ?: "",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isLoadingUsers) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = UniautonomaBlue
                            )
                        } else {
                            userList.forEach { user ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = SurfaceWhite,
                                    shadowElevation = 2.dp,
                                    border = BorderStroke(1.dp, CardStroke)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(if (user.isGuard) YellowLight else if (user.isAdmin) BlueLight else GreenLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (user.isGuard || user.isAdmin) Icons.Default.Security else Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = if (user.isGuard) UniautonomaOrange else if (user.isAdmin) UniautonomaNavy else GreenSuccess,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = user.displayName,
                                                        fontFamily = MuseoSansRounded,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = UniautonomaTextPrimary
                                                    )
                                                    Text(
                                                        text = "${user.studentCode} • ${user.institutionalEmail}",
                                                        fontFamily = MuseoSansRounded,
                                                        fontSize = 11.sp,
                                                        color = UniautonomaTextSecondary
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50.dp))
                                                    .background(if (user.isGuard) YellowLight else if (user.isAdmin) BlueLight else GreenLight)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = user.role.uppercase(),
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (user.isGuard) UniautonomaOrange else if (user.isAdmin) UniautonomaNavy else GreenSuccess
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(color = DividerStroke, thickness = 1.dp)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.EmojiEvents,
                                                    contentDescription = null,
                                                    tint = UniautonomaOrange,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${user.effectiveHonorPoints} Pts Honor",
                                                    fontFamily = MuseoSansRounded,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = UniautonomaTextPrimary
                                                )
                                            }

                                            // Selector de Roles RBAC
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (!user.isStudent) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(GreenLight)
                                                            .clickable {
                                                                scope.launch {
                                                                    UniFindRepository.updateUserRole(user.realId, "student")
                                                                    rbacSuccessMessage = "Rol cambiado a student para ${user.displayName}"
                                                                    userList = userList.map { if (it.realId == user.realId) it.copy(role = "student") else it }
                                                                }
                                                            }
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = "A Estudiante",
                                                            fontFamily = MuseoSansRounded,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = GreenSuccess
                                                        )
                                                    }
                                                }

                                                if (!user.isGuard) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(YellowLight)
                                                            .clickable {
                                                                scope.launch {
                                                                    UniFindRepository.updateUserRole(user.realId, "guard")
                                                                    rbacSuccessMessage = "Rol cambiado a guard para ${user.displayName}"
                                                                    userList = userList.map { if (it.realId == user.realId) it.copy(role = "guard") else it }
                                                                }
                                                            }
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = "A Guardia",
                                                            fontFamily = MuseoSansRounded,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = UniautonomaOrange
                                                        )
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

                3 -> {
                    // =========================================================
                    // TAB 3: CREDENCIAL OFICIAL ADMIN Y HEALTH CHECK INFRAESTRUCTURA
                    // =========================================================
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Credencial Digital
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = UniautonomaNavy,
                            shadowElevation = 6.dp
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 20.dp, y = 10.dp)
                                        .size(150.dp)
                                        .alpha(0.08f)
                                )

                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Credencial Administrativa UAC",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SurfaceWhite
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(UniautonomaOrange)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "SUPERVISIÓN",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SurfaceWhite
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(UniautonomaBlue)
                                            .border(2.5.dp, UniautonomaOrange, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = adminUser.displayName,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SurfaceWhite
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Código: ${adminUser.studentCode} • ${adminUser.institutionalEmail}",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        color = SurfaceWhite.copy(alpha = 0.85f)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .padding(horizontal = 14.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "Dirección de Bienestar y Tecnologías • Sede Principal",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UniautonomaOrange
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Health Check
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceWhite,
                            border = BorderStroke(1.dp, CardStroke)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Estado de Servidor & Backend",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = UniautonomaBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "API REST UniFind (Node.js + MongoDB)",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UniautonomaTextPrimary
                                            )
                                            Text(
                                                text = "http://10.0.2.2:3000/api/v1/health",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 10.sp,
                                                color = UniautonomaTextSecondary
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(if (serverHealth != null) GreenLight else YellowLight)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (serverHealth != null) "Online • ${com.salomontilla.frontunifind.data.network.KtorClient.displayHost}" else "Conectando...",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (serverHealth != null) GreenSuccess else UniautonomaOrange
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Cerrar Sesión
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UniautonomaNavy,
                                contentColor = SurfaceWhite
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = UniautonomaOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cerrar Sesión Administrativa",
                                fontFamily = MuseoSansRounded,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal para rechazar con motivo (Barrera 3)
    if (rejectingItemId != null) {
        Dialog(onDismissRequest = { rejectingItemId = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Motivo de Rechazo (Barrera 3)",
                        fontFamily = MuseoSansRounded,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = UniautonomaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Indica la razón por la que no cumple los lineamientos universitarios",
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        color = UniautonomaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = {
                            Text(
                                text = "Ej. Imagen no corresponde a objeto del campus",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedDanger,
                            unfocusedBorderColor = InputStroke
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { rejectingItemId = null },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UniautonomaBgLight,
                                contentColor = UniautonomaTextPrimary
                            )
                        ) {
                            Text(text = "Cancelar", fontFamily = MuseoSansRounded, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val targetId = rejectingItemId ?: return@Button
                                scope.launch {
                                    UniFindRepository.moderateItem(
                                        itemId = targetId,
                                        action = "reject",
                                        notes = rejectReason.ifBlank { "Rechazado por moderación institucional" }
                                    ).onSuccess {
                                        moderationMessage = "Publicación rechazada"
                                        pendingItems = pendingItems.filter { it.id != targetId }
                                        rejectingItemId = null
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedDanger,
                                contentColor = SurfaceWhite
                            )
                        ) {
                            Text(text = "Confirmar", fontFamily = MuseoSansRounded, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
