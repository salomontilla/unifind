package com.salomontilla.frontunifind.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.data.model.ReportStatus
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
import com.salomontilla.frontunifind.ui.theme.YellowLight

/**
 * =============================================================================
 * PANTALLA 2: FeedScreen (Feed Principal y Buscador de Objetos Unifind)
 * Rediseño Unificado con Identidad Visual Oficial Uniautónoma del Cauca
 * Tipografía: Museo Sans Rounded | Paleta: #062653, #0756A5, #F58220
 * =============================================================================
 */
@Composable
fun FeedScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToHonor: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onShowHonorModal: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ReportStatus.FOUND) }
    var selectedChip by remember { mutableStateOf("Todos") }

    val filterChips = listOf(
        "Todos",
        "🏛 En Portería",
        "Electrónica",
        "Documentos / Carnés",
        "Llaves",
        "Ropa y Accesorios"
    )

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
                    // Marca de agua vectorial del Grifo en el fondo de la cabecera
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

                    // Línea inferior de acento naranja institucional (#F58220)
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
                        // Fila superior: Logotipo Oficial + Notificaciones + Avatar compacto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_uniautonoma),
                                contentDescription = "Uniautónoma del Cauca",
                                contentScale = ContentScale.Fit,
                                alignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .height(42.dp)
                                    .widthIn(max = 175.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "No tienes notificaciones pendientes",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.10f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notificaciones",
                                        tint = SurfaceWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Avatar circular limpio para ir al Login/Perfil
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(UniautonomaBlue)
                                        .border(1.5.dp, UniautonomaOrange, CircleShape)
                                        .clickable { onNavigateToLogin() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "CM",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SurfaceWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buscador limpio estilo Login (sin icono redundante de filtro)
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
                                placeholder = {
                                    Text(
                                        text = "Buscar objeto (ej. calculadora, carné, llaves)...",
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
                            // Pestaña Encontrados
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
                                    text = "Encontrados (28)",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedStatus == ReportStatus.FOUND) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedStatus == ReportStatus.FOUND) SurfaceWhite else Color.White.copy(alpha = 0.70f)
                                )
                            }

                            // Pestaña Perdidos
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
                                    text = "Perdidos (14)",
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
            // CHIPS DE FILTRO RÁPIDO (Incluye "🏛 En Portería")
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
                            Text(
                                text = chip,
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SurfaceWhite else UniautonomaTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ========================================================
            // TARJETAS DE OBJETOS (Orden dinámico según Encontrados vs Perdidos)
            // ========================================================
            if (selectedStatus == ReportStatus.FOUND) {
                item(key = "card-found") {
                    FoundFeedCard(onNavigateToDetail = onNavigateToDetail)
                    Spacer(modifier = Modifier.height(14.dp))
                }
                item(key = "card-lost") {
                    LostFeedCard(onNavigateToDetail = onNavigateToDetail)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                item(key = "card-lost") {
                    LostFeedCard(onNavigateToDetail = onNavigateToDetail)
                    Spacer(modifier = Modifier.height(14.dp))
                }
                item(key = "card-found") {
                    FoundFeedCard(onNavigateToDetail = onNavigateToDetail)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Tarjeta 3: Caso Devuelto con Éxito (Compacta y Limpia)
            item(key = "card-returned") {
                Surface(
                    onClick = { onShowHonorModal() },
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
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(GreenLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "✔ Entregado a su dueño",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess
                                )
                            }
                            Text(
                                text = "Hoy",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                color = UniautonomaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Chaqueta Impermeable Negra Talla M",
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
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cafetería Central • Sede La Casona",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = UniautonomaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(YellowLight)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Reconocimiento de Honor",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaNavy
                                )
                            }
                            Text(
                                text = "+100 Pts",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UniautonomaOrange
                            )
                        }
                    }
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
private fun FoundFeedCard(
    onNavigateToDetail: (String) -> Unit
) {
    Surface(
        onClick = { onNavigateToDetail("UAC-8841") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        tonalElevation = 0.dp,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, CardStroke)
    ) {
        Column {
            // Cabecera visual del objeto encontrado
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

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(50.dp))
                        .background(GreenLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏛 En Portería / Custodia",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenSuccess
                    )
                }

                Text(
                    text = "Hace 2 horas",
                    fontFamily = MuseoSansRounded,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SurfaceWhite.copy(alpha = 0.85f),
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                Text(
                    text = "ID #UAC-8841 • Electrónica",
                    fontFamily = MuseoSansRounded,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniautonomaOrange,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Calculadora Científica Casio fx-570ES",
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
                        text = "Bloque A · Lab 201 (Sistemas)",
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaBlue
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Encontrada sobre una de las terminales del fondo. Tiene tapa protectora gris con señas particulares.",
                    fontFamily = MuseoSansRounded,
                    fontSize = 12.sp,
                    color = UniautonomaTextSecondary,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onNavigateToDetail("UAC-8841") },
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
    }
}

@Composable
private fun LostFeedCard(
    onNavigateToDetail: (String) -> Unit
) {
    Surface(
        onClick = { onNavigateToDetail("UAC-7720") },
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(BlueLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔍 Objeto Perdido",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaBlue
                    )
                }
                Text(
                    text = "Ayer, 4:15 PM",
                    fontFamily = MuseoSansRounded,
                    fontSize = 11.sp,
                    color = UniautonomaTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Carné Estudiantil Ingeniería de Sistemas",
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
                    text = "Biblioteca Central · Sala de Estudio",
                    fontFamily = MuseoSansRounded,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniautonomaBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Extraviado en los cubículos del segundo piso. Porta-carné azul con cinta Uniautónoma. Titular: C. Mosquera.",
                fontFamily = MuseoSansRounded,
                fontSize = 12.sp,
                color = UniautonomaTextSecondary,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = { onNavigateToDetail("UAC-7720") },
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
