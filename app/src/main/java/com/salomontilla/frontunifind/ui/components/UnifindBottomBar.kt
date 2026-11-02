package com.salomontilla.frontunifind.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
) {
    object Objects : BottomNavItem("feed", "Objetos", Icons.Default.Inventory2)
    object Messages : BottomNavItem("messages", "Mensajes", Icons.AutoMirrored.Filled.Chat, badgeCount = 2)
    object Honor : BottomNavItem("honor", "Honor", Icons.Default.EmojiEvents)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
}

/**
 * =============================================================================
 * UnifindBottomBar: Barra Flotante Tipo Cápsula / Dock Simétrica (5 Elementos)
 * Unificada con Tipografía Museo Sans Rounded y Paleta Oficial Uniautónoma
 * =============================================================================
 */
@Composable
fun UnifindBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leftItems = listOf(BottomNavItem.Objects, BottomNavItem.Messages)
    val rightItems = listOf(BottomNavItem.Honor, BottomNavItem.Profile)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            color = SurfaceWhite,
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, CardStroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Izquierda: Objetos y Mensajes
                leftItems.forEach { item ->
                    BottomBarNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (currentRoute != item.route) {
                                onNavigate(item.route)
                            }
                        }
                    )
                }

                // Centro: Botón Redondo Destacado de Reportar (+)
                Box(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        onClick = { onNavigate("report") },
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        color = UniautonomaBlue,
                        tonalElevation = 0.dp,
                        shadowElevation = 8.dp,
                        border = BorderStroke(2.dp, UniautonomaOrange)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Reportar",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Derecha: Honor y Perfil
                rightItems.forEach { item ->
                    BottomBarNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (currentRoute != item.route) {
                                onNavigate(item.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) UniautonomaBlue else UniautonomaTextSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "nav_item_color"
    )

    val pillBackground by animateColorAsState(
        targetValue = if (isSelected) BlueLight else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "nav_pill_bg"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(pillBackground)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (item.badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = UniautonomaOrange,
                            contentColor = SurfaceWhite
                        ) {
                            Text(
                                text = "${item.badgeCount}",
                                fontFamily = MuseoSansRounded,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.title,
                fontFamily = MuseoSansRounded,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
