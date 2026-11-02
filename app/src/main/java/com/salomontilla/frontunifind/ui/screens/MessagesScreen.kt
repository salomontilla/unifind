package com.salomontilla.frontunifind.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.ui.components.UnifindBottomBar
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.DividerStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
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
 * PANTALLA 5: MessagesScreen (Bandeja de Entrada Simplificada y Unificada)
 * Identidad Visual Oficial Uniautónoma del Cauca | Museo Sans Rounded
 * =============================================================================
 */
@Composable
fun MessagesScreen(
    onNavigateToFeed: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToHonor: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onShowHonorModal: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: Todos, 1: En Proceso, 2: Entregados

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
            // CABECERA INSTITUCIONAL (#062653) CON BUSCADOR INTEGRADO
            // =================================================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(UniautonomaNavy, Color(0xFF08336E))
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .offset(x = 20.dp, y = 8.dp)
                            .size(130.dp)
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
                                    painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                                    contentDescription = null,
                                    modifier = Modifier.size(34.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Mensajes y Entregas",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SurfaceWhite
                                    )
                                    Text(
                                        text = "Coordinación en Puntos Seguros",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        color = SurfaceWhite.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(UniautonomaOrange)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "2 Activos",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buscador limpio sin botón cuadrado redundante
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = UniautonomaBlue,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                placeholder = {
                                    Text(
                                        text = "Buscar conversación u objeto...",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // =================================================================
            // CHIPS DE FILTRO (Todos, En Proceso, Entregados)
            // =================================================================
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterPill(label = "Todos (3)", selected = selectedFilter == 0) { selectedFilter = 0 }
                    FilterPill(label = "En Proceso (2)", selected = selectedFilter == 1) { selectedFilter = 1 }
                    FilterPill(
                        label = "Entregados (1)",
                        selected = selectedFilter == 2,
                        activeColor = GreenSuccess
                    ) { selectedFilter = 2 }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // =================================================================
            // LISTA DE CONVERSACIONES LIMPIAS (1 solo badge de estado por tarjeta)
            // =================================================================
            if (selectedFilter == 0 || selectedFilter == 1) {
                item {
                    ConversationCard(
                        icon = Icons.Default.Inventory2,
                        iconBg = BlueLight,
                        iconTint = UniautonomaBlue,
                        title = "Calculadora Casio fx-570ES",
                        lastMessage = "Estudiante: ¡Qué alivio! ¿Nos vemos a las 3:00 PM en Cafetería?",
                        time = "10:42 AM",
                        statusLabel = "● Entrega en curso",
                        statusBg = YellowLight,
                        statusColor = UniautonomaOrange,
                        hasUnreadDot = true,
                        onClick = { onNavigateToChat("chat-1") }
                    )
                }

                item {
                    ConversationCard(
                        icon = Icons.Default.CreditCard,
                        iconBg = BlueLight,
                        iconTint = UniautonomaBlue,
                        title = "Carné Estudiantil Ing. Sistemas",
                        lastMessage = "Carlos M.: ¡Gracias por dejarlo en Portería Principal!",
                        time = "Ayer",
                        statusLabel = "🏛 En Portería",
                        statusBg = BlueLight,
                        statusColor = UniautonomaBlue,
                        hasUnreadDot = false,
                        onClick = { onNavigateToChat("chat-2") }
                    )
                }
            }

            if (selectedFilter == 0 || selectedFilter == 2) {
                item {
                    ConversationCard(
                        icon = Icons.Default.Check,
                        iconBg = GreenLight,
                        iconTint = GreenSuccess,
                        title = "Chaqueta Impermeable Negra",
                        lastMessage = "¡Objeto restituido con éxito! +100 Puntos de Honor",
                        time = "18 Oct",
                        statusLabel = "✔ Entregado",
                        statusBg = GreenLight,
                        statusColor = GreenSuccess,
                        hasUnreadDot = false,
                        onClick = { onShowHonorModal() }
                    )
                }
            }
        }

        UnifindBottomBar(
            currentRoute = "messages",
            onNavigate = { route ->
                when (route) {
                    "feed" -> onNavigateToFeed()
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
private fun ConversationCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    lastMessage: String,
    time: String,
    statusLabel: String,
    statusBg: Color,
    statusColor: Color,
    hasUnreadDot: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        tonalElevation = 0.dp,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, CardStroke)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontFamily = MuseoSansRounded,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = time,
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = if (hasUnreadDot) FontWeight.Bold else FontWeight.Normal,
                        color = if (hasUnreadDot) UniautonomaBlue else UniautonomaTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = lastMessage,
                    fontFamily = MuseoSansRounded,
                    fontSize = 12.sp,
                    fontWeight = if (hasUnreadDot) FontWeight.Bold else FontWeight.Normal,
                    color = if (hasUnreadDot) UniautonomaTextPrimary else UniautonomaTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(statusBg)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            fontFamily = MuseoSansRounded,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                    if (hasUnreadDot) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(UniautonomaOrange)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean,
    activeColor: Color = UniautonomaBlue,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = if (selected) activeColor else SurfaceWhite,
        tonalElevation = 0.dp,
        shadowElevation = if (selected) 2.dp else 0.dp,
        border = BorderStroke(1.dp, if (selected) activeColor else DividerStroke)
    ) {
        Text(
            text = label,
            fontFamily = MuseoSansRounded,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) SurfaceWhite else UniautonomaTextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}
