package com.salomontilla.frontunifind.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite

/**
 * =============================================================================
 * Shimmer Skeletons: Componentes Oficiales de Estados de Carga
 * Sistema de Diseño Institucional Uniautónoma del Cauca
 * =============================================================================
 */

@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerTransition = rememberInfiniteTransition(label = "global_shimmer")
    val shimmerTranslate by shimmerTransition.animateFloat(
        initialValue = -350f,
        targetValue = 1100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFE9EDF5),
            Color(0xFFF7F9FC),
            Color(0xFFE9EDF5)
        ),
        start = Offset(shimmerTranslate - 300f, 0f),
        end = Offset(shimmerTranslate, 260f)
    )
}

/**
 * Skeleton para la pestaña "Mis Reportes" en ProfileScreen
 */
@Composable
fun MyReportCardSkeleton(brush: Brush = rememberShimmerBrush()) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
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
                            .width(68.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(brush)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Título
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ubicación y custodia
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Banner o fecha
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )
        }
    }
}

/**
 * Skeleton para tarjetas de Moderación en GuardHomeScreen (Barrera 3)
 */
@Composable
fun GuardModerationCardSkeleton(brush: Brush = rememberShimmerBrush()) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, CardStroke),
        shadowElevation = 3.dp
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
                            .width(70.dp)
                            .height(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(brush)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Imagen / thumbnail de objeto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Título
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Ubicación
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de moderación (Aprobar / Rechazar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Skeleton para la pestaña de Custodia Física / Inventario en GuardHomeScreen
 */
@Composable
fun GuardInventoryCardSkeleton(brush: Brush = rememberShimmerBrush()) {
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
                            .width(60.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
        }
    }
}

/**
 * Skeleton completo para DetailScreen
 */
@Composable
fun DetailScreenSkeleton(brush: Brush = rememberShimmerBrush()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        // Foto principal hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(brush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Píldoras de estado y código
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(brush)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Título del objeto (2 líneas)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de ubicación y custodia
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceWhite,
            border = BorderStroke(1.dp, CardStroke)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción (3 líneas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón de acción principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(brush)
        )
    }
}
