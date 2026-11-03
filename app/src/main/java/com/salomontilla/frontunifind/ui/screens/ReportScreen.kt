package com.salomontilla.frontunifind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.data.model.CreateFoundExpressRequest
import com.salomontilla.frontunifind.data.model.CreateLostDetailedRequest
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.theme.BlueLight
import kotlinx.coroutines.launch
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
 * PANTALLA 4: ReportScreen (Publicar Reporte Dual Simplificado)
 * Identidad Visual Oficial Uniautónoma del Cauca | Museo Sans Rounded
 * =============================================================================
 */
@Composable
fun ReportScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isFound by remember { mutableStateOf(true) }

    // Estados para Modo Exprés (Encontré / Portería)
    var title by remember { mutableStateOf("Audífonos Bluetooth JBL en estuche") }
    var selectedQuickType by remember { mutableStateOf("Audífonos") }
    var custodyOption by remember { mutableIntStateOf(0) } // 0 = Portería (por defecto), 1 = Lo tengo yo
    var selectedFoundZone by remember { mutableStateOf("Bloque A") }
    var room by remember { mutableStateOf("Laboratorio de Redes 203") }
    var showExtraFields by remember { mutableStateOf(false) }
    var brand by remember { mutableStateOf("JBL") }
    var model by remember { mutableStateOf("Tune 510BT") }
    var isAnonymous by remember { mutableStateOf(true) }
    var securityQuestion by remember { mutableStateOf("¿Qué sticker o marca tiene en el estuche?") }
    var securityAnswer by remember { mutableStateOf("Sticker de calavera con iniciales CM") }

    // Estados para Modo Objeto Perdido (Dueño)
    var lostTitle by remember { mutableStateOf("Carné Estudiantil Ingeniería de Sistemas") }
    var lostBrandColor by remember { mutableStateOf("Porta-carné azul con cinta institucional") }
    var lostDistinguishingMarks by remember { mutableStateOf("Sticker del semillero de programación al respaldo") }
    var selectedLostZone by remember { mutableStateOf("Biblioteca") }
    var lostLocationRef by remember { mutableStateOf("Cubículos individuales Piso 2") }

    var hasError by remember { mutableStateOf(false) }
    var isPublishing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val quickItemSuggestions = listOf(
        "Llaves" to "Manojo de llaves con llavero",
        "Carné / Doc" to "Carné estudiantil / Documento",
        "Calculadora" to "Calculadora científica con tapa",
        "Audífonos" to "Audífonos Bluetooth JBL en estuche",
        "Cargador" to "Cargador de portátil / celular",
        "Termo / Ropa" to "Termo metálico / Prenda"
    )

    val campusZones = listOf(
        "Portería Principal",
        "Bloque A",
        "Biblioteca",
        "Cafetería",
        "Salón / Lab"
    )

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
                                text = "Publicar Reporte",
                                fontFamily = MuseoSansRounded,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SurfaceWhite
                            )
                            Text(
                                text = if (isFound) "Modo Exprés • Hallazgo / Portería" else "Modo Búsqueda • Objeto Perdido",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                color = SurfaceWhite.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(34.dp)
                            .alpha(0.85f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(UniautonomaOrange)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            // =================================================================
            // SELECTOR DE MODO REDISEÑADO (Segmented Control Institucional)
            // =================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                tonalElevation = 0.dp,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, CardStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = UniautonomaBgLight,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, CardStroke)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Pestaña 1: Encontré un objeto
                            Surface(
                                onClick = {
                                    isFound = true
                                    hasError = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isFound) UniautonomaBlue else SurfaceWhite,
                                contentColor = if (isFound) SurfaceWhite else UniautonomaTextPrimary,
                                tonalElevation = 0.dp,
                                shadowElevation = if (isFound) 3.dp else 1.dp,
                                border = if (isFound) null else BorderStroke(1.dp, DividerStroke)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Inventory2,
                                        contentDescription = null,
                                        tint = if (isFound) UniautonomaOrange else UniautonomaBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Encontré un objeto",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = if (isFound) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isFound) SurfaceWhite else UniautonomaTextPrimary,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Pestaña 2: Perdí un objeto
                            Surface(
                                onClick = {
                                    isFound = false
                                    hasError = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (!isFound) UniautonomaBlue else SurfaceWhite,
                                contentColor = if (!isFound) SurfaceWhite else UniautonomaTextPrimary,
                                tonalElevation = 0.dp,
                                shadowElevation = if (!isFound) 3.dp else 1.dp,
                                border = if (!isFound) null else BorderStroke(1.dp, DividerStroke)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = if (!isFound) UniautonomaOrange else UniautonomaBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Perdí un objeto",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = if (!isFound) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (!isFound) SurfaceWhite else UniautonomaTextPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isFound) {
                            "Registra el hallazgo en Portería o custodia personal para devolverlo a su dueño."
                        } else {
                            "Detalla las señas particulares de tu objeto para recibir aviso inmediato si aparece."
                        },
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        color = UniautonomaTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isFound) {
                // =============================================================
                // FLUJO A: ENCONTRÉ / PORTERÍA (MODO EXPRÉS)
                // =============================================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Fotografía y ¿Qué objeto es?",
                            fontFamily = MuseoSansRounded,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NavyCard)
                                    .padding(6.dp)
                            ) {
                                IconButton(
                                    onClick = { Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show() },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = UniautonomaOrange)
                                }
                                Row(
                                    modifier = Modifier.align(Alignment.BottomStart),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GreenSuccess,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Lista",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SurfaceWhite
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(86.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BlueLight)
                                    .border(1.5.dp, UniautonomaBlue, RoundedCornerShape(14.dp))
                                    .clickable {
                                        Toast.makeText(context, "Abriendo cámara rápida...", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint = UniautonomaBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tomar / Cambiar Foto",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickItemSuggestions.forEach { (chipLabel, suggestedTitle) ->
                                val selected = selectedQuickType == chipLabel
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(if (selected) UniautonomaBlue else UniautonomaBgLight)
                                        .border(
                                            1.dp,
                                            if (selected) UniautonomaBlue else DividerStroke,
                                            RoundedCornerShape(50.dp)
                                        )
                                        .clickable {
                                            selectedQuickType = chipLabel
                                            title = suggestedTitle
                                            hasError = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = chipLabel,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) SurfaceWhite else UniautonomaTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (it.isNotEmpty()) hasError = false
                            },
                            label = { Text("¿Qué objeto es? *", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            isError = hasError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tarjeta 2: Custodia y Zona
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. ¿Dónde quedó guardado?",
                            fontFamily = MuseoSansRounded,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Opción Portería
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (custodyOption == 0) YellowLight else UniautonomaBgLight)
                                .border(
                                    width = if (custodyOption == 0) 1.5.dp else 1.dp,
                                    color = if (custodyOption == 0) UniautonomaOrange else DividerStroke,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { custodyOption = 0 }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = custodyOption == 0,
                                onClick = { custodyOption = 0 },
                                colors = RadioButtonDefaults.colors(selectedColor = UniautonomaOrange)
                            )
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = UniautonomaOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Está en Portería / Vigilancia",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Opción Lo tengo yo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (custodyOption == 1) BlueLight else UniautonomaBgLight)
                                .border(
                                    width = if (custodyOption == 1) 1.5.dp else 1.dp,
                                    color = if (custodyOption == 1) UniautonomaBlue else DividerStroke,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { custodyOption = 1 }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = custodyOption == 1,
                                onClick = { custodyOption = 1 },
                                colors = RadioButtonDefaults.colors(selectedColor = UniautonomaBlue)
                            )
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = UniautonomaBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lo tengo yo (Coordinar entrega por chat)",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            campusZones.forEach { zone ->
                                val selected = selectedFoundZone == zone
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(if (selected) UniautonomaNavy else UniautonomaBgLight)
                                        .border(
                                            1.dp,
                                            if (selected) UniautonomaNavy else DividerStroke,
                                            RoundedCornerShape(50.dp)
                                        )
                                        .clickable { selectedFoundZone = zone }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = zone,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) SurfaceWhite else UniautonomaTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = room,
                            onValueChange = { room = it },
                            label = { Text("Salón o referencia (Opcional)", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = UniautonomaBlue) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sección Opcional Colapsable
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showExtraFields = !showExtraFields }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = UniautonomaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showExtraFields) {
                                    "Ocultar pregunta clave o detalles opcionales"
                                } else {
                                    "Agregar pregunta clave o detalles (Opcional)"
                                },
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaBlue
                            )
                        }

                        if (showExtraFields) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = brand,
                                    onValueChange = { brand = it },
                                    label = { Text("Marca", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = model,
                                    onValueChange = { model = it },
                                    label = { Text("Modelo", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(YellowLight)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Publicar como Hallador Anónimo",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaNavy
                                )
                                Switch(
                                    checked = isAnonymous,
                                    onCheckedChange = { isAnonymous = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SurfaceWhite,
                                        checkedTrackColor = UniautonomaBlue
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = securityQuestion,
                                onValueChange = { securityQuestion = it },
                                label = { Text("Pregunta clave para el dueño", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = securityAnswer,
                                onValueChange = { securityAnswer = it },
                                label = { Text("Respuesta esperada (Confidencial)", fontFamily = MuseoSansRounded, fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            } else {
                // =============================================================
                // FLUJO B: PERDÍ UN OBJETO (MODO DETALLADO DEL DUEÑO)
                // =============================================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Datos de tu objeto perdido",
                            fontFamily = MuseoSansRounded,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = lostTitle,
                            onValueChange = {
                                lostTitle = it
                                if (it.isNotEmpty()) hasError = false
                            },
                            label = { Text("¿Qué objeto se te perdió? *", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            isError = hasError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = lostBrandColor,
                            onValueChange = { lostBrandColor = it },
                            label = { Text("Marca / Color", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = lostDistinguishingMarks,
                            onValueChange = { lostDistinguishingMarks = it },
                            label = { Text("Señas particulares (stickers, forro, iniciales) *", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. ¿Dónde crees que se perdió?",
                            fontFamily = MuseoSansRounded,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            campusZones.forEach { zone ->
                                val selected = selectedLostZone == zone
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(if (selected) UniautonomaBlue else UniautonomaBgLight)
                                        .border(
                                            1.dp,
                                            if (selected) UniautonomaBlue else DividerStroke,
                                            RoundedCornerShape(50.dp)
                                        )
                                        .clickable { selectedLostZone = zone }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = zone,
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) SurfaceWhite else UniautonomaTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = lostLocationRef,
                            onValueChange = { lostLocationRef = it },
                            label = { Text("Salón, piso o referencia", fontFamily = MuseoSansRounded, fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = UniautonomaBlue) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(BlueLight)
                                .border(1.dp, UniautonomaBlue, RoundedCornerShape(14.dp))
                                .clickable {
                                    Toast.makeText(context, "Seleccionar foto de galería", Toast.LENGTH_SHORT).show()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = UniautonomaBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Adjuntar foto de referencia (Opcional)",
                                fontFamily = MuseoSansRounded,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Único Principal (54.dp en #0756A5) conectado a POST /items
            Button(
                onClick = {
                    if (isPublishing) return@Button
                    val activeTitle = if (isFound) title else lostTitle
                    if (activeTitle.trim().isEmpty()) {
                        hasError = true
                        Toast.makeText(context, "Por favor indica qué objeto es", Toast.LENGTH_SHORT).show()
                    } else {
                        hasError = false
                        isPublishing = true
                        scope.launch {
                            if (isFound) {
                                val categoryMapped = when (selectedQuickType) {
                                    "Carné / Doc" -> "Documentos / Carnés"
                                    "Llaves" -> "Llaves"
                                    "Termo / Ropa" -> "Ropa y Accesorios"
                                    else -> "Electrónica"
                                }
                                val res = UniFindRepository.createFoundExpress(
                                    CreateFoundExpressRequest(
                                        title = title.trim(),
                                        type = "found",
                                        category = categoryMapped,
                                        custodyType = if (custodyOption == 0) "PORTERIA" else "PERSONAL",
                                        campusZone = selectedFoundZone,
                                        locationReference = room.trim().takeIf { it.isNotEmpty() },
                                        brandAndColor = listOf(brand.trim(), model.trim())
                                            .filter { it.isNotEmpty() }
                                            .joinToString(" ")
                                            .takeIf { it.isNotEmpty() },
                                        photoUrl = "https://images.unsplash.com/photo-1587145820266-a5951ee6f620"
                                    )
                                )
                                isPublishing = false
                                val createdCode = res.getOrNull()?.code ?: "UAC-NUEVO"
                                Toast.makeText(
                                    context,
                                    "¡Hallazgo #$createdCode publicado exitosamente en UniFind!",
                                    Toast.LENGTH_LONG
                                ).show()
                                onBack()
                            } else {
                                val res = UniFindRepository.createLostDetailed(
                                    CreateLostDetailedRequest(
                                        title = lostTitle.trim(),
                                        type = "lost",
                                        category = "Documentos / Carnés",
                                        distinguishingMarks = lostDistinguishingMarks.trim()
                                            .ifEmpty { "Reportado por el titular en UniFind" },
                                        brandAndColor = lostBrandColor.trim().takeIf { it.isNotEmpty() },
                                        campusZone = selectedLostZone,
                                        locationReference = lostLocationRef.trim().takeIf { it.isNotEmpty() },
                                        photoUrl = null
                                    )
                                )
                                isPublishing = false
                                val createdCode = res.getOrNull()?.code ?: "UAC-PERDIDO"
                                Toast.makeText(
                                    context,
                                    "¡Reporte #$createdCode publicado! Te avisaremos si llega a Portería.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onBack()
                            }
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
                if (isPublishing) {
                    CircularProgressIndicator(
                        color = SurfaceWhite,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Publicando en UniFind API...",
                        fontFamily = MuseoSansRounded,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFound) "Publicar Hallazgo Rápido" else "Publicar Reporte de Pérdida",
                        fontFamily = MuseoSansRounded,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
