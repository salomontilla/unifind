package com.salomontilla.frontunifind.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.data.model.ItemDto
import com.salomontilla.frontunifind.data.model.ReportStatus
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.components.UnifindBottomBar
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.DividerStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.NavyCard
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite
import com.salomontilla.frontunifind.ui.theme.UniautonomaBgLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaNavy
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextPrimary
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary
import kotlinx.coroutines.delay

/**
 * =============================================================================
 * PANTALLA 2: FeedScreen (Feed Principal y Buscador de Objetos Unifind)
 * Conectado en tiempo real a UniFindRepository (API REST Node.js + MongoDB)
 * Con Shimmer Skeleton Loading e Iconografía Vectorial Material 3 sin Emojis
 * =============================================================================
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun FeedScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToHonor: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onShowHonorModal: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ReportStatus.FOUND) }
    var selectedChip by remember { mutableStateOf("Todos") }

    var itemsList by remember { mutableStateOf<List<ItemDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val filterChips = listOf(
        "Todos",
        "En Portería",
        "Electrónica",
        "Documentos / Carnés",
        "Llaves",
        "Ropa y Accesorios"
    )

    // Transición infinita para el efecto Shimmer de las tarjetas Skeleton
    val shimmerTransition = rememberInfiniteTransition(label = "feed_shimmer")
    val shimmerTranslate by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE2E8F0),
            Color(0xFFCBD5E1),
            Color(0xFFE2E8F0)
        ),
        start = Offset(shimmerTranslate - 300f, 0f),
        end = Offset(shimmerTranslate, 260f)
    )

    // Consulta reactiva a la API con debounce en búsqueda
    LaunchedEffect(selectedStatus, selectedChip, searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(250)
        }
        isLoading = true
        val typeParam = if (selectedStatus == ReportStatus.FOUND) "found" else "lost"
        val custodyParam = if (selectedChip == "En Portería") "PORTERIA" else null
        val categoryParam = when (selectedChip) {
            "Todos", "En Portería" -> null
            else -> selectedChip
        }
        val result = UniFindRepository.getItems(
            type = typeParam,
            category = categoryParam,
            search = searchQuery.trim().takeIf { it.isNotEmpty() },
            custodyType = custodyParam
        )
        itemsList = result.getOrDefault(emptyList())
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UniautonomaBgLight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 115.dp)
        ) {
            // ========================================================
            // CABECERA INSTITUCIONAL UNIAUTÓNOMA (#062653)
            // ========================================================
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
                            .size(150.dp)
                            .alpha(0.06f)
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
                            .padding(horizontal = 18.dp, vertical = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_logo_uniautonoma),
                                    contentDescription = "Uniautónoma del Cauca",
                                    contentScale = ContentScale.Fit,
                                    alignment = Alignment.CenterStart,
                                    modifier = Modifier
                                        .height(36.dp)
                                        .widthIn(max = 150.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .border(
                                            1.dp,
                                            UniautonomaOrange.copy(alpha = 0.7f),
                                            RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "UniFind",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SurfaceWhite
                                    )
                                }
                            }

                            Box(contentAlignment = Alignment.TopEnd) {
                                IconButton(
                                    onClick = { /* Notificaciones institucionales */ },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.12f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notificaciones",
                                        tint = SurfaceWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp, end = 6.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(UniautonomaOrange)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buscador predictivo conectado a GET /items?search=...
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = UniautonomaBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = { focusManager.clearFocus() }
                                ),
                                placeholder = {
                                    Text(
                                        text = "Buscar objeto (ej. calculadora, termo, carné)...",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 13.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = UniautonomaTextPrimary,
                                    unfocusedTextColor = UniautonomaTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = UniautonomaTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (UniFindRepository.currentUser.isGuard) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToLogin() },
                                shape = RoundedCornerShape(12.dp),
                                color = UniautonomaOrange
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Sesión Oficial de Vigilancia",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SurfaceWhite
                                        )
                                    }
                                    Text(
                                        text = "Volver a Consola →",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SurfaceWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Control Segmentado: Encontrados vs Perdidos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(NavyCard)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        if (selectedStatus == ReportStatus.FOUND) UniautonomaBlue else Color.Transparent
                                    )
                                    .clickable { selectedStatus = ReportStatus.FOUND },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedStatus == ReportStatus.FOUND) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(UniautonomaOrange)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = if (selectedStatus == ReportStatus.FOUND && !isLoading) {
                                        "Encontrados (${itemsList.size})"
                                    } else {
                                        "Encontrados"
                                    },
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedStatus == ReportStatus.FOUND) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedStatus == ReportStatus.FOUND) SurfaceWhite else Color.White.copy(alpha = 0.70f)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        if (selectedStatus == ReportStatus.LOST) UniautonomaBlue else Color.Transparent
                                    )
                                    .clickable { selectedStatus = ReportStatus.LOST },
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedStatus == ReportStatus.LOST) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(UniautonomaOrange)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = if (selectedStatus == ReportStatus.LOST && !isLoading) {
                                        "Perdidos (${itemsList.size})"
                                    } else {
                                        "Perdidos"
                                    },
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedStatus == ReportStatus.LOST) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedStatus == ReportStatus.LOST) SurfaceWhite else Color.White.copy(alpha = 0.70f)
                                )
                            }
                        }
                    }
                }
            }

            // ========================================================
            // CHIPS DE FILTRO RÁPIDO (Sin Emojis, Icono Vectorial en "En Portería")
            // ========================================================
            item {
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterChips) { chip ->
                        val isSelected = selectedChip == chip
                        Surface(
                            onClick = { selectedChip = chip },
                            shape = RoundedCornerShape(50.dp),
                            color = if (isSelected) UniautonomaBlue else SurfaceWhite,
                            tonalElevation = 0.dp,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) UniautonomaBlue else DividerStroke
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (chip == "En Portería") {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (isSelected) UniautonomaOrange else UniautonomaBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = chip,
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SurfaceWhite else UniautonomaTextPrimary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ========================================================
            // ESTADOS DE CARGA SKELETON O LISTA DINÁMICA DE OBJETOS
            // ========================================================
            if (isLoading) {
                items(count = 3, key = { "skeleton-$it" }) {
                    FeedCardSkeleton(brush = shimmerBrush)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else if (itemsList.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceWhite,
                        tonalElevation = 0.dp,
                        shadowElevation = 3.dp,
                        border = BorderStroke(1.dp, CardStroke)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No se encontraron objetos con este filtro",
                                fontFamily = MuseoSansRounded,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Prueba limpiando la búsqueda o cambia entre Encontrados y Perdidos.",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                color = UniautonomaTextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                items(
                    items = itemsList,
                    key = { it.code.ifBlank { it.id } }
                ) { itemDto ->
                    DynamicFeedItemCard(
                        item = itemDto,
                        onNavigateToDetail = onNavigateToDetail
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        UnifindBottomBar(
            currentRoute = "feed",
            onNavigate = { route ->
                when (route) {
                    "messages" -> onNavigateToMessages()
                    "honor" -> onNavigateToHonor()
                    "report" -> onNavigateToReport()
                    "profile" -> onNavigateToLogin()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FeedCardSkeleton(brush: Brush) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        tonalElevation = 0.dp,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, CardStroke)
    ) {
        Column {
            // Cabecera Skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .background(brush)
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .width(135.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(SurfaceWhite.copy(alpha = 0.55f))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Línea de título
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Subtítulo de ubicación
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.52f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Descripción simulada (2 líneas)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(11.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botón simulado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
private fun DynamicFeedItemCard(
    item: ItemDto,
    onNavigateToDetail: (String) -> Unit
) {
    val itemCode = item.code.ifBlank { item.id }
    val isLost = item.type.equals("lost", ignoreCase = true)
    val isPorteria = item.custodyType.equals("PORTERIA", ignoreCase = true)

    Surface(
        onClick = { onNavigateToDetail(itemCode) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        tonalElevation = 0.dp,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, CardStroke)
    ) {
        if (!isLost) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(124.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(UniautonomaNavy, NavyCard)
                            )
                        )
                        .padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(86.dp)
                            .alpha(0.08f)
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (isPorteria) GreenLight else BlueLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPorteria) Icons.Default.Security else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isPorteria) GreenSuccess else UniautonomaBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isPorteria) {
                                "En Portería / Custodia"
                            } else {
                                "En Custodia de Estudiante"
                            },
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPorteria) GreenSuccess else UniautonomaBlue
                        )
                    }

                    Text(
                        text = item.timeAgo ?: "Reciente",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SurfaceWhite.copy(alpha = 0.85f),
                        modifier = Modifier.align(Alignment.TopEnd)
                    )

                    Text(
                        text = "ID #$itemCode • ${item.category.ifBlank { "General" }}",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaOrange,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.title,
                        fontFamily = MuseoSansRounded,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = UniautonomaBlue,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.displayLocation,
                            fontFamily = MuseoSansRounded,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.displayDescription,
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        color = UniautonomaTextSecondary,
                        lineHeight = 17.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onNavigateToDetail(itemCode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UniautonomaBlue,
                            contentColor = SurfaceWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡Es mío! Reclamar →",
                            fontFamily = MuseoSansRounded,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(BlueLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = UniautonomaBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Objeto Perdido • #$itemCode",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaBlue
                        )
                    }
                    Text(
                        text = item.timeAgo ?: "Reciente",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        color = UniautonomaTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = item.title,
                    fontFamily = MuseoSansRounded,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniautonomaTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = UniautonomaBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.displayLocation,
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaBlue
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.displayDescription,
                    fontFamily = MuseoSansRounded,
                    fontSize = 12.sp,
                    color = UniautonomaTextSecondary,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { onNavigateToDetail(itemCode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, UniautonomaBlue)
                ) {
                    Text(
                        text = "¡Lo encontré! Avisar →",
                        fontFamily = MuseoSansRounded,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaBlue
                    )
                }
            }
        }
    }
}
