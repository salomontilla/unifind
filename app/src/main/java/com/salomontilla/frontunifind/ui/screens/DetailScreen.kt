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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.DividerStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.InputStroke
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
 * PANTALLA 3: DetailScreen (Detalle Consolidado + Acción Directa)
 * Soporta Flujo A (Reclamar Objeto Encontrado) y Flujo B (Avisar Objeto Perdido)
 * Identidad Visual Oficial Uniautónoma del Cauca | Museo Sans Rounded
 * =============================================================================
 */
@Composable
fun DetailScreen(
    itemId: String = "UAC-8841",
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val context = LocalContext.current
    val isLostItem = itemId == "UAC-7720"

    // Estados para Reclamo (Objeto Encontrado)
    var answerText by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    // Estados para Aviso (Objeto Perdido)
    var foundActionOption by remember { mutableIntStateOf(0) }
    var finderNote by remember { mutableStateOf("") }

    Scaffold(
        containerColor = UniautonomaBgLight,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(UniautonomaNavy)
                        .statusBarsPadding()
                        .height(58.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = SurfaceWhite
                            )
                        }
                        Column {
                            Text(
                                text = "Detalle del Objeto",
                                fontFamily = MuseoSansRounded,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SurfaceWhite
                            )
                            Text(
                                text = "Registro Oficial #$itemId",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                color = UniautonomaOrange
                            )
                        }
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Compartiendo reporte #$itemId", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(UniautonomaOrange)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp)
                    .background(SurfaceWhite)
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        if (isLostItem) {
                            val msg = if (foundActionOption == 0) {
                                "¡Aviso enviado a Carlos Mosquera! Ya sabe que su carné está en Portería."
                            } else {
                                "¡Aviso enviado! Abriendo chat con Carlos Mosquera..."
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            onNavigateToChat("chat-2")
                        } else {
                            if (answerText.trim().isEmpty()) {
                                hasError = true
                                Toast.makeText(context, "Describe los detalles para reclamar tu objeto", Toast.LENGTH_SHORT).show()
                            } else {
                                hasError = false
                                Toast.makeText(context, "¡Propiedad validada! Abriendo canal de entrega...", Toast.LENGTH_SHORT).show()
                                onNavigateToChat("chat-1")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UniautonomaBlue,
                        contentColor = SurfaceWhite
                    )
                ) {
                    Text(
                        text = if (isLostItem) {
                            "¡Yo lo encontré! Notificar al Dueño →"
                        } else {
                            "¡Es mío! Validar Detalles y Reclamar →"
                        },
                        fontFamily = MuseoSansRounded,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            // =================================================================
            // TARJETA 1 CONSOLIDADA: IMAGEN + INFO + UBICACIÓN + ATRIBUTOS CLAVE
            // =================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = SurfaceWhite,
                tonalElevation = 0.dp,
                shadowElevation = 5.dp,
                border = BorderStroke(1.dp, CardStroke)
            ) {
                Column {
                    // Cabecera Visual / Fotografía del Objeto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(165.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(UniautonomaNavy, NavyCard)
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(110.dp)
                                .alpha(0.08f)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(50.dp))
                                .background(if (isLostItem) BlueLight else GreenLight)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isLostItem) "🔍 Objeto Perdido" else "🏛 En Portería / Custodia",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLostItem) UniautonomaBlue else GreenSuccess
                            )
                        }

                        Text(
                            text = if (isLostItem) "Ayer, 4:15 PM" else "Hace 2 horas",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SurfaceWhite.copy(alpha = 0.85f),
                            modifier = Modifier.align(Alignment.TopEnd)
                        )

                        Text(
                            text = if (isLostItem) {
                                "Titular: Carlos Mosquera • Ing. Sistemas"
                            } else {
                                "Custodia oficial • Sede La Casona"
                            },
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaOrange,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }

                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isLostItem) {
                                "Carné Estudiantil Ingeniería de Sistemas"
                            } else {
                                "Calculadora Científica Casio fx-570ES PLUS"
                            },
                            fontFamily = MuseoSansRounded,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isLostItem) {
                                "Extraviado en los cubículos individuales del segundo piso de la Biblioteca Central. Porta-carné azul oscuro con cinta institucional Uniautónoma."
                            } else {
                                "Encontrada sobre una de las terminales del fondo en la mesa 4. Tiene tapa protectora gris con señas particulares que el dueño debe indicar para retirarla."
                            },
                            fontFamily = MuseoSansRounded,
                            fontSize = 13.sp,
                            color = UniautonomaTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Ubicación integrada
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(BlueLight)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = UniautonomaBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isLostItem) {
                                        "Biblioteca Central · Cubículos Piso 2"
                                    } else {
                                        "Bloque A · Laboratorio 201 (Retiro en Portería)"
                                    },
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaNavy
                                )
                                Text(
                                    text = "Sede Principal La Casona • Popayán",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 11.sp,
                                    color = UniautonomaTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DividerStroke, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // 3 Píldoras clave de atributos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val pills = if (isLostItem) {
                                listOf(
                                    "Categoría" to "Documento",
                                    "Porta-carné" to "Azul",
                                    "Seña" to "Cinta UAC"
                                )
                            } else {
                                listOf(
                                    "Marca" to "Casio",
                                    "Modelo" to "fx-570ES",
                                    "Tapa" to "Gris Oscuro"
                                )
                            }
                            pills.forEach { (label, value) ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(UniautonomaBgLight)
                                        .border(1.dp, DividerStroke, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 10.sp,
                                        color = UniautonomaTextSecondary
                                    )
                                    Text(
                                        text = value,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UniautonomaTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =================================================================
            // TARJETA 2: ACCIÓN DIRECTA SEGÚN FLUJO
            // =================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = SurfaceWhite,
                tonalElevation = 0.dp,
                shadowElevation = 5.dp,
                border = BorderStroke(
                    1.5.dp,
                    if (isLostItem) UniautonomaBlue else UniautonomaOrange
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (isLostItem) {
                        Text(
                            text = "🤝 ¿Encontraste este objeto o está en Portería?",
                            fontFamily = MuseoSansRounded,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UniautonomaNavy
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (foundActionOption == 0) GreenLight else UniautonomaBgLight)
                                .border(
                                    width = if (foundActionOption == 0) 1.5.dp else 1.dp,
                                    color = if (foundActionOption == 0) GreenSuccess else DividerStroke,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { foundActionOption = 0 }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = foundActionOption == 0,
                                onClick = { foundActionOption = 0 },
                                colors = RadioButtonDefaults.colors(selectedColor = GreenSuccess)
                            )
                            Text(
                                text = "🏛 Ya lo dejé en Portería Principal (Enviar aviso)",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (foundActionOption == 1) BlueLight else UniautonomaBgLight)
                                .border(
                                    width = if (foundActionOption == 1) 1.5.dp else 1.dp,
                                    color = if (foundActionOption == 1) UniautonomaBlue else DividerStroke,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { foundActionOption = 1 }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = foundActionOption == 1,
                                onClick = { foundActionOption = 1 },
                                colors = RadioButtonDefaults.colors(selectedColor = UniautonomaBlue)
                            )
                            Text(
                                text = "💬 Lo tengo conmigo (Abrir chat para entregarlo)",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = finderNote,
                            onValueChange = { finderNote = it },
                            label = { Text("Nota rápida para el dueño (Opcional)", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            placeholder = { Text("Ej. Lo dejé con el guarda de turno...", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = UniautonomaOrange,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reclamar Objeto: Prueba de Propiedad",
                                fontFamily = MuseoSansRounded,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = UniautonomaNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(YellowLight)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Pista de verificación:",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaOrange
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"¿Qué iniciales, sticker o marca tiene en el reverso de la tapa protectora?\"",
                                fontFamily = MuseoSansRounded,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = answerText,
                            onValueChange = {
                                answerText = it
                                if (it.isNotEmpty()) hasError = false
                            },
                            label = { Text("Describe tus señas o responde la pista *", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            placeholder = { Text("Ej. Iniciales CM en marcador negro y sticker...", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            isError = hasError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke
                            )
                        )
                    }
                }
            }
        }
    }
}
