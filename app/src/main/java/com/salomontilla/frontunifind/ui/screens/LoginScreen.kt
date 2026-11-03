package com.salomontilla.frontunifind.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R
import com.salomontilla.frontunifind.data.repository.UniFindRepository
import com.salomontilla.frontunifind.ui.theme.CardStroke
import com.salomontilla.frontunifind.ui.theme.DividerStroke
import com.salomontilla.frontunifind.ui.theme.GreenLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.InputStroke
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite
import com.salomontilla.frontunifind.ui.theme.UniautonomaBgLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlueLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaNavy
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextPrimary
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary
import kotlinx.coroutines.delay

/**
 * =============================================================================
 * PANTALLA 1: LoginScreen (Onboarding y Login Institucional Uniautónoma)
 * Refactor UI/UX Completo — Identidad Visual Oficial Uniautónoma del Cauca
 * Tipografía: Museo Sans Rounded | Paleta Oficial: #062653, #0756A5, #2476C5, #F58220
 * =============================================================================
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("carlos.munoz@uniautonoma.edu.co") }
    var studentId by remember { mutableStateOf("2023-1001") }
    var rememberMe by remember { mutableStateOf(true) }

    var emailFocused by remember { mutableStateOf(false) }
    var studentIdFocused by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var studentIdError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val normalizedEmail = email.trim().lowercase()
    val isEmailValid = normalizedEmail.endsWith("@uniautonoma.edu.co") &&
        normalizedEmail.length > "@uniautonoma.edu.co".length &&
        !normalizedEmail.contains(" ")
    val isStudentIdValid = studentId.trim().length >= 6 && studentId.trim().all { it.isDigit() || it == '-' }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            val result = UniFindRepository.login(normalizedEmail, studentId.trim())
            isLoading = false
            val user = result.getOrNull()
            Toast.makeText(
                context,
                "Bienvenido ${user?.name ?: "Estudiante"} • Comunidad Uniautónoma",
                Toast.LENGTH_SHORT
            ).show()
            onLoginSuccess()
        }
    }

    fun submitLogin() {
        if (isLoading) return
        focusManager.clearFocus()

        var valid = true
        if (!isEmailValid) {
            emailError = "Ingresa un correo institucional válido (@uniautonoma.edu.co)"
            valid = false
        } else {
            emailError = null
        }

        if (!isStudentIdValid) {
            studentIdError = "Ingresa tu código estudiantil o cédula (mínimo 6 dígitos)"
            valid = false
        } else {
            studentIdError = null
        }

        if (valid) {
            isLoading = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UniautonomaBgLight)
    ) {
        // 1. Imagen de fondo institucional generada a partir de los recursos existentes
        Image(
            painter = painterResource(id = R.drawable.bg_login_uniautonoma),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Marca de agua vectorial del Grifo Uniautónoma en el encabezado (nitidez infinita)
        Image(
            painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .offset(x = 28.dp, y = 78.dp)
                .size(190.dp)
                .alpha(0.08f)
        )

        // Marca de agua sutil inferior del Grifo
        Image(
            painter = painterResource(id = R.drawable.ic_grifo_uniautonoma),
            contentDescription = null,
            colorFilter = ColorFilter.tint(UniautonomaNavy),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-24).dp, y = (-36).dp)
                .size(160.dp)
                .alpha(0.035f)
        )

        // 3. Contenedor desplazable ergonómico (Mobile-First + soporte Tablets)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // =============================================================
                // ENCABEZADO INSTITUCIONAL
                // =============================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logotipo oficial independiente conservando proporción y transparencia
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_uniautonoma),
                        contentDescription = "Logotipo Uniautónoma del Cauca",
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart,
                        modifier = Modifier
                            .height(50.dp)
                            .widthIn(max = 195.dp)
                    )

                    // Identificación discreta de acceso seguro
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.24f),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 11.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_verified),
                            contentDescription = null,
                            tint = UniautonomaOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Portal Seguro",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Etiqueta "Red Institucional de Bienestar"
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(UniautonomaNavy.copy(alpha = 0.52f))
                        .border(
                            width = 1.dp,
                            color = UniautonomaBlueLight.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(UniautonomaOrange)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Red Institucional de Bienestar",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Título de Bienvenida Institucional
                Text(
                    text = "Red de Contacto y Objetos Perdidos Uniautónoma",
                    fontFamily = MuseoSansRounded,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SurfaceWhite,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Conectando honestidad, solidaridad y confianza en nuestra comunidad universitaria.",
                    fontFamily = MuseoSansRounded,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    color = SurfaceWhite.copy(alpha = 0.90f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                // =============================================================
                // TARJETA DE FORMULARIO (#FFFFFF, bordes 22.dp, sombra elegante)
                // =============================================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = SurfaceWhite,
                    tonalElevation = 0.dp,
                    shadowElevation = 16.dp,
                    border = BorderStroke(1.dp, CardStroke)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 22.dp)
                    ) {
                        // Encabezado de la tarjeta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 5.dp, height = 28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(UniautonomaOrange)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Iniciar sesión",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UniautonomaTextPrimary
                                    )
                                    Text(
                                        text = "Accede a tu cuenta institucional",
                                        fontFamily = MuseoSansRounded,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = UniautonomaTextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(UniautonomaBgLight)
                                    .border(1.dp, DividerStroke, RoundedCornerShape(50.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Sede Popayán",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaNavy
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = DividerStroke, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // -----------------------------------------------------
                        // CAMPO 1: Correo institucional
                        // -----------------------------------------------------
                        Text(
                            text = "Correo institucional",
                            fontFamily = MuseoSansRounded,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniautonomaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                if (emailError != null) emailError = null
                            },
                            placeholder = {
                                Text(
                                    text = "usuario@uniautonoma.edu.co",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 13.sp,
                                    color = UniautonomaTextSecondary.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_email),
                                    contentDescription = "Correo institucional",
                                    tint = when {
                                        emailError != null -> Color(0xFFDC2626)
                                        emailFocused -> UniautonomaBlue
                                        else -> UniautonomaTextSecondary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (isEmailValid && emailError == null) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(GreenLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Correo válido",
                                            tint = GreenSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            },
                            isError = emailError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { emailFocused = it.isFocused },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke,
                                errorBorderColor = Color(0xFFDC2626),
                                focusedContainerColor = Color(0xFFFAFCFF),
                                unfocusedContainerColor = SurfaceWhite,
                                errorContainerColor = Color(0xFFFEF2F2),
                                focusedTextColor = UniautonomaTextPrimary,
                                unfocusedTextColor = UniautonomaTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        if (emailError != null) {
                            Text(
                                text = emailError!!,
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFDC2626)
                            )
                        } else {
                            Text(
                                text = "ⓘ Usa tu cuenta de estudiante, docente o administrativo",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = UniautonomaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // -----------------------------------------------------
                        // CAMPO 2: Código estudiantil / Cédula
                        // -----------------------------------------------------
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Código estudiantil / Cédula",
                                fontFamily = MuseoSansRounded,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaTextPrimary
                            )
                            Text(
                                text = "Identificación única",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = UniautonomaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = studentId,
                            onValueChange = { input ->
                                studentId = input.filter { it.isDigit() || it == '-' }
                                if (studentIdError != null) studentIdError = null
                            },
                            placeholder = {
                                Text(
                                    text = "Ej. 202410293",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 13.sp,
                                    color = UniautonomaTextSecondary.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_id_card),
                                    contentDescription = "Código estudiantil o cédula",
                                    tint = when {
                                        studentIdError != null -> Color(0xFFDC2626)
                                        studentIdFocused -> UniautonomaBlue
                                        else -> UniautonomaTextSecondary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lock),
                                    contentDescription = null,
                                    tint = if (isStudentIdValid) UniautonomaBlue else UniautonomaTextSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            isError = studentIdError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { submitLogin() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { studentIdFocused = it.isFocused },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UniautonomaBlue,
                                unfocusedBorderColor = InputStroke,
                                errorBorderColor = Color(0xFFDC2626),
                                focusedContainerColor = Color(0xFFFAFCFF),
                                unfocusedContainerColor = SurfaceWhite,
                                errorContainerColor = Color(0xFFFEF2F2),
                                focusedTextColor = UniautonomaTextPrimary,
                                unfocusedTextColor = UniautonomaTextPrimary
                            )
                        )

                        if (studentIdError != null) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = studentIdError!!,
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFDC2626)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // -----------------------------------------------------
                        // OPCIONES ADICIONALES (Áreas táctiles >= 48.dp)
                        // -----------------------------------------------------
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { rememberMe = !rememberMe }
                                    .padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = UniautonomaBlue,
                                        uncheckedColor = InputStroke
                                    )
                                )
                                Text(
                                    text = "Recordar sesión",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = UniautonomaTextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        Toast.makeText(
                                            context,
                                            "Mesa de Ayuda TIC Uniautónoma: soporte@uniautonoma.edu.co",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ayuda institucional",
                                    fontFamily = MuseoSansRounded,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniautonomaBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // -----------------------------------------------------
                        // BOTÓN PRINCIPAL CON ESTADO DE CARGA Y TRANSICIÓN
                        // -----------------------------------------------------
                        Button(
                            onClick = { submitLogin() },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UniautonomaBlue,
                                contentColor = SurfaceWhite,
                                disabledContainerColor = UniautonomaBlue.copy(alpha = 0.8f),
                                disabledContentColor = SurfaceWhite
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp)
                        ) {
                            AnimatedContent(
                                targetState = isLoading,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "login_button_state"
                            ) { loading ->
                                if (loading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = SurfaceWhite,
                                            strokeWidth = 2.5.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Verificando credenciales...",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SurfaceWhite
                                        )
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.width(18.dp))
                                        Text(
                                            text = "Ingresar con Correo Uniautónoma",
                                            fontFamily = MuseoSansRounded,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SurfaceWhite,
                                            textAlign = TextAlign.Center
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // -----------------------------------------------------
                        // AVISO DE SEGURIDAD INSTITUCIONAL
                        // -----------------------------------------------------
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(UniautonomaBgLight)
                                .border(
                                    width = 1.dp,
                                    color = UniautonomaOrange.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(UniautonomaBlue.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_shield),
                                    contentDescription = null,
                                    tint = UniautonomaBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = UniautonomaTextPrimary
                                        )
                                    ) {
                                        append("Acceso protegido y exclusivo: ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Normal,
                                            color = UniautonomaTextSecondary
                                        )
                                    ) {
                                        append("Estudiantes, docentes y administrativos de la Corporación Universitaria Autónoma del Cauca.")
                                    }
                                },
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // =============================================================
                // FOOTER INSTITUCIONAL
                // =============================================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(DividerStroke)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "© Corporación Universitaria Autónoma del Cauca.",
                        fontFamily = MuseoSansRounded,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = UniautonomaTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Políticas de Tratamiento de Datos Personales • Uniautónoma",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Políticas de privacidad",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaBlue
                            )
                        }

                        Text(
                            text = "•",
                            fontFamily = MuseoSansRounded,
                            fontSize = 11.sp,
                            color = UniautonomaTextSecondary
                        )

                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Reglamento de Bienestar Universitario • Uniautónoma",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Reglamento de Bienestar",
                                fontFamily = MuseoSansRounded,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniautonomaBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
