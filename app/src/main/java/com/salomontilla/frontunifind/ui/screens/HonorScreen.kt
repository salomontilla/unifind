package com.salomontilla.frontunifind.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.ui.components.UnifindBottomBar
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
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
 * PANTALLA DEDICADA: HonorScreen (Perfil del Estudiante & Ranking de Honor)
 * Rediseño Unificado con Identidad Visual Oficial Uniautónoma del Cauca
 * =============================================================================
 */
@Composable
fun HonorScreen(
    onNavigateToFeed: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
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
            // CABECERA INSTITUCIONAL (#062653) CON MARCA DE AGUA DEL GRIFO
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
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .offset(x = 20.dp, y = 10.dp)
                            .size(150.dp)
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
                            .padding(horizontal = 20.dp, vertical = 22.dp)
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
                                Text(
                                    text = "Honor y Solidaridad",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Sede Popayán",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Identidad del Estudiante
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(UniautonomaBlue)
                                    .border(2.dp, UniautonomaOrange, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CM",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Carlos Mosquera",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SurfaceWhite
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = UniautonomaOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "cmosquera@uniautonoma.edu.co",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = SurfaceWhite.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = "Cód. 202410293 • Ingeniería de Sistemas",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaOrange
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // TARJETA HERO DE PUNTOS Y NIVEL (#F58220)
            // =================================================================
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 5.dp,
                    border = BorderStroke(1.5.dp, UniautonomaOrange)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(YellowLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = UniautonomaOrange,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Puntos de Honor Acumulados",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                    Text(
                                        text = "450 Pts",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UniautonomaNavy
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
                                    text = "RANGO 3",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Héroe del Campus • Progreso a Rango 4",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaTextPrimary
                            )
                            Text(
                                text = "450 / 600",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UniautonomaBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { 450f / 600f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = UniautonomaOrange,
                            trackColor = YellowLight
                        )
                    }
                }
            }

            // =================================================================
            // MÉTRICAS COMUNITARIAS
            // =================================================================
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        title = "Objetos Devueltos",
                        value = "4",
                        subtitle = "100% efectividad",
                        modifier = Modifier.weight(1f),
                        valueColor = UniautonomaBlue
                    )
                    MetricBox(
                        title = "Reputación Campus",
                        value = "4.9 ★",
                        subtitle = "Honestidad ejemplar",
                        modifier = Modifier.weight(1f),
                        valueColor = UniautonomaOrange
                    )
                }
            }

            // =================================================================
            // MEDALLAS E INSIGNIAS DESBLOQUEADAS
            // =================================================================
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Insignias de Honor Desbloqueadas",
                    fontFamily = MuseoSansRounded,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UniautonomaTextPrimary,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

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
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        BadgeRow(
                            icon = Icons.Default.MilitaryTech,
                            iconTint = UniautonomaOrange,
                            title = "Honestidad Impecable",
                            desc = "Por restituir pertenencias de alto valor en el campus.",
                            points = "+100 Pts"
                        )
                        BadgeRow(
                            icon = Icons.Default.Check,
                            iconTint = GreenSuccess,
                            title = "Puntualidad en Punto Seguro",
                            desc = "Completar la entrega en Cafetería Bloque A a tiempo.",
                            points = "+50 Pts"
                        )
                        BadgeRow(
                            icon = Icons.Default.Security,
                            iconTint = UniautonomaBlue,
                            title = "Custodio Ejemplar",
                            desc = "Compromiso con la seguridad de la comunidad Uniautónoma.",
                            points = "+100 Pts"
                        )
                    }
                }
            }
        }

        UnifindBottomBar(
            currentRoute = "honor",
            onNavigate = { route ->
                when (route) {
                    "feed" -> onNavigateToFeed()
                    "messages" -> onNavigateToMessages()
                    "report" -> onNavigateToReport()
                    "profile" -> onNavigateToLogin()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = UniautonomaBlue
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        tonalElevation = 0.dp,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, CardStroke)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontFamily = MuseoSansRounded,
                fontSize = 11.sp,
                color = UniautonomaTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = MuseoSansRounded,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = MuseoSansRounded,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GreenSuccess
            )
        }
    }
}

@Composable
fun BadgeRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    desc: String,
    points: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = MuseoSansRounded,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = UniautonomaTextPrimary
            )
            Text(
                text = desc,
                fontFamily = MuseoSansRounded,
                fontSize = 11.sp,
                color = UniautonomaTextSecondary,
                lineHeight = 15.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(YellowLight)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = points,
                fontFamily = MuseoSansRounded,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = UniautonomaOrange
            )
        }
    }
}
