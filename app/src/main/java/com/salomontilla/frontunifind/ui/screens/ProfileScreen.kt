package com.salomontilla.frontunifind.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.data.model.HealthResponse
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.model.UserProfileData
import com.salomontilla.frontunifind.data.network.KtorClient
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.components.GuardTicketConsoleSheet
import com.salomontilla.frontunifind.ui.components.InstitutionInfoSheet
import com.salomontilla.frontunifind.ui.components.InstitutionalTexts
import com.salomontilla.frontunifind.ui.components.MyReportCardSkeleton
import com.salomontilla.frontunifind.ui.components.UnifindBottomBar
import com.salomontilla.frontunifind.ui.components.rememberShimmerBrush
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.DividerStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.RedDanger
import com.salomontilla.frontunifind.ui.theme.RedLight
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
 * PANTALLA 8: ProfileScreen (Carné Digital, Rango Cívico y Mis Reportes)
 * HU-08 (Perfil & Reportes), HU-09 (Consola Portería), HU-10 (Health Check)
 * =============================================================================
 */
@Composable
fun ProfileScreen(
    initialTab: Int = 0,
    onNavigateToFeed: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToHonor: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToGuardHome: () -> Unit = {},
    onNavigateToAdminHome: () -> Unit = {},
    onLogout: () -> Unit
) {
    val user = UniFindRepository.currentUser
    val scope = rememberCoroutineScope()
    val shimmerBrush = rememberShimmerBrush()

    var profileData by remember { mutableStateOf<UserProfileData?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var serverHealth by remember { mutableStateOf<HealthResponse?>(null) }
    var selectedTab by remember { mutableIntStateOf(initialTab) } // 0 = Mi Reputación, 1 = Mis Reportes
    var myItems by remember { mutableStateOf<List<ItemDto>>(emptyList()) }
    var isLoadingItems by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var activeInfoSheet by remember { mutableStateOf<String?>(null) }
    var showPorteriaConsole by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            UniFindRepository.getUserProfile(user.realId).onSuccess { data ->
                profileData = data
            }
            isLoadingProfile = false
        }
        scope.launch {
            UniFindRepository.getHealth().onSuccess { health ->
                serverHealth = health
            }
        }
        scope.launch {
            isLoadingItems = true
            UniFindRepository.getUserItems(user.realId).onSuccess { items ->
                myItems = items
            }
            isLoadingItems = false
        }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            isLoadingItems = true
            UniFindRepository.getUserItems(user.realId).onSuccess { items ->
                myItems = items
            }
            isLoadingItems = false
        }
    }

    val effectiveRole = profileData?.user?.role?.takeIf { it.isNotBlank() } ?: user.role
    val displayCode = profileData?.user?.studentCode ?: user.studentCode.ifBlank { "2023-1001" }
    val isGuard = effectiveRole.equals("guard", ignoreCase = true) || displayCode.startsWith("GUARD", ignoreCase = true)
    val isAdmin = effectiveRole.equals("admin", ignoreCase = true) || effectiveRole.equals("superadmin", ignoreCase = true) || displayCode.startsWith("ADMIN", ignoreCase = true)
    val isStudent = !isGuard && !isAdmin

    val displayName = profileData?.user?.fullName ?: (user.name?.takeIf { it.isNotBlank() } ?: (if (isGuard) "Andrés Felipe Salazar (Vigilancia)" else if (isAdmin) "Administrador del Sistema UniFind" else "Carlos Muñoz"))
    val displayEmail = profileData?.user?.institutionalEmail ?: user.institutionalEmail.ifBlank { if (isGuard) "andres.salazar@uniautonoma.edu.co" else if (isAdmin) "admin.unifind@uniautonoma.edu.co" else "carlos.munoz@uniautonoma.edu.co" }
    val displayPoints = profileData?.user?.reputation?.honorPoints ?: user.effectiveHonorPoints
    val displayRating = profileData?.user?.reputation?.rating ?: user.effectiveRating
    val displayReturned = profileData?.stats?.returnedSuccess ?: (if (isAdmin) 10 else if (isGuard) 1 else 3)

    val cardTitle = when {
        isGuard -> "Carné de Seguridad y Custodia"
        isAdmin -> "Credencial Administrativa UAC"
        else -> "Carné Digital Estudiantil"
    }

    val departmentText = when {
        isGuard -> "Personal de Vigilancia y Portería • Sede Popayán La Casona"
        isAdmin -> "Dirección de Bienestar y Tecnologías • Sede Principal"
        else -> "Ingeniería de Sistemas • Sede Popayán La Casona"
    }

    val roleBadgeText = when {
        isGuard -> "Vigilancia / Portería"
        isAdmin -> "Administrador UAC"
        else -> "Estudiante Activo"
    }

    val rankName = when {
        isGuard -> "Oficial de Custodia"
        isAdmin -> "Auditor & Moderador Global"
        displayPoints >= 300 -> "Héroe del Campus"
        displayPoints >= 150 -> "Custodio Solidario"
        else -> "Guardián Novato"
    }

    val rankColor = when {
        isAdmin -> UniautonomaBlue
        isGuard -> UniautonomaOrange
        rankName == "Héroe del Campus" -> UniautonomaOrange
        rankName == "Custodio Solidario" -> GreenSuccess
        else -> UniautonomaBlueLight
    }

    val defaultBadges = when {
        isGuard -> listOf("Honestidad UAC", "Vigilante Oficial", "Custodio del Campus")
        isAdmin -> listOf("Supervisión Campus", "Administrador", "Auditoría Global")
        else -> listOf("Honestidad ejemplar", "Entrega puntual", "Guardián del Campus")
    }
    val badgesList = profileData?.user?.reputation?.badges?.ifEmpty { defaultBadges } ?: defaultBadges

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UniautonomaBgLight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 115.dp)
        ) {
            // =================================================================
            // 1. CABECERA CARNÉ DIGITAL ESTUDIANTIL (#062653)
            // =================================================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(UniautonomaNavy, Color(0xFF08336E))
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .offset(x = 24.dp, y = 12.dp)
                            .size(160.dp)
                            .alpha(0.07f)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(UniautonomaOrange)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(22.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(UniautonomaOrange)
                                    )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cardTitle,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (isGuard) YellowLight else if (isAdmin) BlueLight else GreenLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isGuard || isAdmin) Icons.Default.Security else Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = if (isGuard) UniautonomaOrange else if (isAdmin) UniautonomaBlue else GreenSuccess,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = roleBadgeText,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGuard) UniautonomaOrange else if (isAdmin) UniautonomaBlue else GreenSuccess
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Avatar con borde naranja institucional (#F58220)
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(UniautonomaBlue)
                                .border(2.5.dp, UniautonomaOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = displayName,
                            fontFamily = MuseoSansRounded,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SurfaceWhite
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Código: $displayCode • $displayEmail",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = departmentText,
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // HU-08: Rango Cívico Institucional / Nivel Operativo
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(rankColor.copy(alpha = 0.22f))
                                .border(1.2.dp, rankColor, RoundedCornerShape(50.dp))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isGuard || isAdmin) Icons.Default.Security else Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = rankColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isGuard || isAdmin) rankName else "Rango Cívico: $rankName",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SurfaceWhite
                            )
                        }
                    }
                }
            }

            // =================================================================
            // 2. SELECTOR DE PESTAÑAS (HU-08: Mi Reputación vs. Mis Reportes)
            // =================================================================
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BlueLight)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Pestaña 0: Mi Reputación
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) UniautonomaNavy else Color.Transparent)
                            .clickable { selectedTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mi Reputación",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SurfaceWhite else UniautonomaTextSecondary
                        )
                    }

                    // Pestaña 1: Mis Reportes
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) UniautonomaNavy else Color.Transparent)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Mis Reportes",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) SurfaceWhite else UniautonomaTextSecondary
                            )
                            if (myItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (selectedTab == 1) UniautonomaOrange else UniautonomaNavy)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${myItems.size}",
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
            }

            // CONTENIDO SEGÚN LA PESTAÑA ACTIVA
            if (selectedTab == 0) {
                // =============================================================
                // TAB 0: REPUTACIÓN, INSIGNIAS Y ESTADO DEL SISTEMA
                // =============================================================
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Columna 1: Puntos de Honor / Nivel
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceWhite,
                            tonalElevation = 0.dp,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, CardStroke)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(YellowLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isGuard || isAdmin) Icons.Default.Security else Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = UniautonomaOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isGuard) "Oficial" else if (isAdmin) "500 Pts" else "$displayPoints Pts",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaNavy
                                )
                                Text(
                                    text = if (isGuard) "Seguridad" else if (isAdmin) "Supervisión" else "Honor",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }
                        }

                        // Columna 2: Objetos Devueltos / En Custodia / Auditados
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceWhite,
                            tonalElevation = 0.dp,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, CardStroke)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(GreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GreenSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isGuard) "${profileData?.stats?.totalReported ?: 3}" else if (isAdmin) "${profileData?.stats?.totalReported ?: 12}" else "$displayReturned",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaNavy
                                )
                                Text(
                                    text = if (isGuard) "En Custodia" else if (isAdmin) "Auditados" else "Devueltos",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }
                        }

                        // Columna 3: Calificación / Operatividad
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceWhite,
                            tonalElevation = 0.dp,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, CardStroke)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(YellowLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = UniautonomaOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isGuard) "5.0 ★" else if (isAdmin) "100%" else "$displayRating ★",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaNavy
                                )
                                Text(
                                    text = if (isGuard) "Operatividad" else if (isAdmin) "Disponibilidad" else "Reputación",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }
                        }
                    }
                }

                // HU-08: Carrusel Horizontal Interactivo de Insignias
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceWhite,
                        tonalElevation = 0.dp,
                        shadowElevation = 4.dp,
                        border = BorderStroke(1.dp, CardStroke)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Insignias y Méritos Cívicos",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UniautonomaTextPrimary
                                )
                                Text(
                                    text = "${badgesList.size} Desbloqueadas",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaOrange
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Carrusel Horizontal interactivo
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                badgesList.forEach { badgeTitle ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(BlueLight)
                                            .border(1.2.dp, UniautonomaBlue.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(SurfaceWhite),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = null,
                                                    tint = UniautonomaBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = badgeTitle,
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UniautonomaNavy
                                            )
                                            Text(
                                                text = "Comunidad UAC",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 9.sp,
                                                color = UniautonomaTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Ajustes, Estado del Servidor (HU-10) y Consola de Portería (HU-09)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceWhite,
                        tonalElevation = 0.dp,
                        shadowElevation = 4.dp,
                        border = BorderStroke(1.dp, CardStroke)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ajustes y Operaciones del Campus",
                                fontFamily = MuseoSansRounded,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UniautonomaTextPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // HU-10: Estado de API REST en vivo (Health Check)
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
                                            text = "Servidor API REST UniFind",
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
                                        text = if (serverHealth != null) "Servidor Activo • ${KtorClient.host}:${KtorClient.PORT}" else "Conectando...",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (serverHealth != null) GreenSuccess else UniautonomaOrange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DividerStroke, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Acceso especializado según RBAC
                            if (isGuard) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(YellowLight)
                                        .clickable { onNavigateToGuardHome() }
                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = UniautonomaOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Consola de Vigilancia y Portería",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = UniautonomaNavy
                                            )
                                            Text(
                                                text = "Despacho de tickets, moderación e inventario",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 11.sp,
                                                color = UniautonomaTextSecondary
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = UniautonomaOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else if (isAdmin) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BlueLight)
                                        .clickable { onNavigateToAdminHome() }
                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = UniautonomaBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Consola Administrativa del Campus",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = UniautonomaNavy
                                            )
                                            Text(
                                                text = "Hotspots de pérdidas, Barrera 3 y roles RBAC",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 11.sp,
                                                color = UniautonomaTextSecondary
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = UniautonomaBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BlueLight)
                                        .clickable { showPorteriaConsole = true }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ConfirmationNumber,
                                            contentDescription = null,
                                            tint = UniautonomaOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Consola de Portería (HU-09)",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = UniautonomaNavy
                                            )
                                            Text(
                                                text = "Validar y despachar tickets TICKET-XXXX-RET",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 11.sp,
                                                color = UniautonomaTextSecondary
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = UniautonomaOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DividerStroke, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Switch de notificaciones
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = UniautonomaBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Notificaciones de objetos perdidos",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UniautonomaTextPrimary
                                    )
                                }
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SurfaceWhite,
                                        checkedTrackColor = UniautonomaBlue
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = DividerStroke, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Políticas de Privacidad
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activeInfoSheet = "PRIVACY" }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = UniautonomaBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Políticas de Privacidad",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = UniautonomaTextPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = UniautonomaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Reglamento de Bienestar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activeInfoSheet = "BIENESTAR" }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = UniautonomaBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Reglamento de Bienestar",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = UniautonomaTextPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = UniautonomaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Botón de Cerrar Sesión
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UniautonomaNavy,
                            contentColor = SurfaceWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = UniautonomaOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesión Institucional",
                            fontFamily = MuseoSansRounded,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // =============================================================
                // TAB 1: MIS REPORTES (HU-08: Publicaciones del Usuario)
                // =============================================================
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Publicaciones Registradas (${myItems.size})",
                            fontFamily = MuseoSansRounded,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaTextPrimary
                        )

                        Text(
                            text = "+ Crear Reporte",
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaOrange,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateToReport() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (isLoadingItems) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            repeat(3) {
                                MyReportCardSkeleton(brush = shimmerBrush)
                            }
                        }
                    }
                } else if (myItems.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = UniautonomaTextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Aún no tienes publicaciones",
                                fontFamily = MuseoSansRounded,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaNavy
                            )
                            Text(
                                text = "Reporta un hallazgo o publica una pertenencia extraviada para recibir avisos.",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                color = UniautonomaTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onNavigateToReport,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = UniautonomaBlue)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publicar Ahora", fontFamily = MuseoSansRounded, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    items(myItems) { itemDto ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .clickable { onNavigateToDetail(itemDto.code) },
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
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "#${itemDto.code}",
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
                                                .background(if (itemDto.type == "found") BlueLight else YellowLight)
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = if (itemDto.type == "found") "HALLAZGO" else "EXTRAVÍO",
                                                fontFamily = MuseoSansRounded,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (itemDto.type == "found") UniautonomaBlue else UniautonomaOrange
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(
                                                when (itemDto.status) {
                                                    "pending_review" -> YellowLight
                                                    "rejected" -> RedLight
                                                    "returned" -> GreenLight
                                                    "in_process" -> YellowLight
                                                    else -> BlueLight
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = when (itemDto.status) {
                                                "pending_review" -> "EN REVISIÓN (BARRERA 3)"
                                                "rejected" -> "NO APROBADO"
                                                "returned" -> "DEVUELTO"
                                                "in_process" -> "EN RECLAMO"
                                                else -> "DISPONIBLE"
                                            },
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (itemDto.status) {
                                                "pending_review" -> UniautonomaOrange
                                                "rejected" -> RedDanger
                                                "returned" -> GreenSuccess
                                                "in_process" -> UniautonomaOrange
                                                else -> UniautonomaBlue
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = itemDto.title,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaTextPrimary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = UniautonomaTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${itemDto.displayLocation} • ${if (itemDto.custodyType == "PORTERIA") "En Portería" else "Custodia Personal"}",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                }

                                if (itemDto.status == "pending_review") {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = "⏳ En espera de validación de contenido por Bienestar Universitario",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = UniautonomaOrange
                                    )
                                } else if (itemDto.status == "rejected") {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = "❌ Moderación: ${itemDto.moderationNotes?.takeIf { it.isNotBlank() } ?: "Publicación no cumple lineamientos del campus"}",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RedDanger
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isGuard) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                color = UniautonomaNavy,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGuardHome() }
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = UniautonomaOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Volver a Consola de Vigilancia",
                        fontFamily = MuseoSansRounded,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SurfaceWhite
                    )
                }
            }
        } else if (isAdmin) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                color = UniautonomaNavy,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdminHome() }
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = UniautonomaBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Volver a Panel de Administrador",
                        fontFamily = MuseoSansRounded,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SurfaceWhite
                    )
                }
            }
        } else {
            UnifindBottomBar(
                currentRoute = "profile",
                onNavigate = { route ->
                    when (route) {
                        "feed" -> onNavigateToFeed()
                        "messages" -> onNavigateToMessages()
                        "honor" -> onNavigateToHonor()
                        "report" -> onNavigateToReport()
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // ModalBottomSheet Institucional
        when (activeInfoSheet) {
            "PRIVACY" -> InstitutionInfoSheet(
                title = InstitutionalTexts.PRIVACY_TITLE,
                subtitle = InstitutionalTexts.PRIVACY_SUBTITLE,
                contentText = InstitutionalTexts.PRIVACY_CONTENT,
                icon = Icons.Default.Security,
                onDismiss = { activeInfoSheet = null }
            )
            "BIENESTAR" -> InstitutionInfoSheet(
                title = InstitutionalTexts.BIENESTAR_TITLE,
                subtitle = InstitutionalTexts.BIENESTAR_SUBTITLE,
                contentText = InstitutionalTexts.BIENESTAR_CONTENT,
                icon = Icons.Default.Description,
                onDismiss = { activeInfoSheet = null }
            )
        }

        // HU-09: Consola de Portería
        if (showPorteriaConsole) {
            GuardTicketConsoleSheet(
                onDismiss = { showPorteriaConsole = false },
                onDeliveryCompleted = {
                    scope.launch {
                        UniFindRepository.getUserProfile(user.realId).onSuccess { profileData = it }
                        UniFindRepository.getUserItems(user.realId).onSuccess { myItems = it }
                    }
                }
            )
        }
    }
}
