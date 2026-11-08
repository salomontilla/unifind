package com.salomontilla.frontunifind.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.ui.theme.BlueLight
import com.salomontilla.frontunifind.ui.theme.GreenSuccess
import com.salomontilla.frontunifind.ui.theme.MuseoSansRounded
import com.salomontilla.frontunifind.ui.theme.SurfaceWhite
import com.salomontilla.frontunifind.ui.theme.TextBody
import com.salomontilla.frontunifind.ui.theme.UniautonomaBgLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlue
import com.salomontilla.frontunifind.ui.theme.UniautonomaBlueLight
import com.salomontilla.frontunifind.ui.theme.UniautonomaNavy
import com.salomontilla.frontunifind.ui.theme.UniautonomaOrange
import com.salomontilla.frontunifind.ui.theme.UniautonomaTextSecondary

/**
 * =============================================================================
 * SISTEMA OFICIAL DE FEEDBACK UI/UX INSTITUCIONAL UNIAUTÓNOMA (COMPOSE)
 * Erradica popups y Toasts toscos del sistema operativo, proveyendo
 * Snackbars flotantes y ModalBottomSheets con la identidad institucional.
 * =============================================================================
 */

enum class FeedbackType {
    SUCCESS,
    ERROR,
    INFO
}

/**
 * Visuales personalizados para Snackbars que soportan categorización institucional.
 */
class UniautonomaSnackbarVisuals(
    override val message: String,
    val type: FeedbackType = FeedbackType.INFO,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals

/**
 * Host de Snackbar institucional estilizado con elevación, bordes de color temático,
 * fondo Navy institucional y tipografía Museo Sans Rounded.
 */
@Composable
fun UniautonomaSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        val customVisuals = data.visuals as? UniautonomaSnackbarVisuals
        val type = customVisuals?.type ?: run {
            val msg = data.visuals.message.lowercase()
            when {
                msg.contains("éxito") || msg.contains("correcto") || msg.contains("guardad") || msg.contains("listo") -> FeedbackType.SUCCESS
                msg.contains("error") || msg.contains("falló") || msg.contains("inválid") || msg.contains("obligatorio") || msg.contains("falta") -> FeedbackType.ERROR
                else -> FeedbackType.INFO
            }
        }

        val borderColor = when (type) {
            FeedbackType.SUCCESS -> GreenSuccess.copy(alpha = 0.85f)
            FeedbackType.ERROR -> UniautonomaOrange.copy(alpha = 0.85f)
            FeedbackType.INFO -> UniautonomaBlueLight.copy(alpha = 0.85f)
        }

        val icon = when (type) {
            FeedbackType.SUCCESS -> Icons.Default.CheckCircle
            FeedbackType.ERROR -> Icons.Default.Warning
            FeedbackType.INFO -> Icons.Default.Info
        }

        val iconTint = when (type) {
            FeedbackType.SUCCESS -> GreenSuccess
            FeedbackType.ERROR -> UniautonomaOrange
            FeedbackType.INFO -> UniautonomaOrange
        }

        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = UniautonomaNavy.copy(alpha = 0.25f),
                    spotColor = UniautonomaNavy.copy(alpha = 0.45f)
                ),
            shape = RoundedCornerShape(16.dp),
            color = UniautonomaNavy,
            border = BorderStroke(1.2.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = data.visuals.message,
                    fontFamily = MuseoSansRounded,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SurfaceWhite,
                    modifier = Modifier.weight(1f),
                    lineHeight = 18.sp
                )

                data.visuals.actionLabel?.let { action ->
                    Text(
                        text = action,
                        fontFamily = MuseoSansRounded,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniautonomaOrange,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { data.performAction() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * ModalBottomSheet institucional de Material 3 para desplegar reglamentos,
 * políticas y soporte TIC de forma ergonómica y profesional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionInfoSheet(
    title: String,
    subtitle: String? = null,
    contentText: String,
    icon: ImageVector = Icons.Default.Info,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = SurfaceWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(UniautonomaTextSecondary.copy(alpha = 0.35f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = UniautonomaBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontFamily = MuseoSansRounded,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = UniautonomaNavy,
                textAlign = TextAlign.Center
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontFamily = MuseoSansRounded,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = UniautonomaOrange,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = false)
                    .clip(RoundedCornerShape(16.dp))
                    .background(UniautonomaBgLight)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = contentText,
                    fontFamily = MuseoSansRounded,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextBody,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UniautonomaNavy,
                    contentColor = SurfaceWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Entendido",
                    fontFamily = MuseoSansRounded,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Textos institucionales predefinidos para la comunidad Uniautónoma.
 */
object InstitutionalTexts {
    const val PRIVACY_TITLE = "Políticas de Privacidad"
    const val PRIVACY_SUBTITLE = "Tratamiento de Datos Personales • Ley 1581 de 2012"
    val PRIVACY_CONTENT = """
        En cumplimiento de la Ley Estatutaria 1581 de 2012 y sus decretos reglamentarios, la Corporación Universitaria Autónoma del Cauca informa a la comunidad universitaria que los datos personales recolectados a través de la plataforma UniFind serán tratados de manera confidencial y segura.

        1. Finalidad del Tratamiento:
        Los datos personales suministrados (nombres, código institucional, correo @uniautonoma.edu.co e información descriptiva de pertenencias) tienen como único fin facilitar la custodia, identificación y restitución de objetos extraviados en las diferentes sedes del campus.

        2. Pistas de Verificación y Seguridad:
        La información sensible asociada a objetos (números de serie completos, marcas ocultas o características privadas) se mantiene protegida y solo se contrasta ante una reclamación formal validada por el dueño o personal de Portería.

        3. Derechos del Titular:
        Como titular de los datos, tienes derecho a conocer, actualizar y rectificar tus datos en cualquier momento mediante solicitud a la Dirección de TIC o a través de los canales institucionales dispuestos para tal fin.
    """.trimIndent()

    const val BIENESTAR_TITLE = "Reglamento de Bienestar"
    const val BIENESTAR_SUBTITLE = "Puntos Seguros y Custodia de Pertenencias"
    val BIENESTAR_CONTENT = """
        El programa de Puntos Seguros de la Vicerrectoría de Bienestar Universitario tiene como propósito fomentar una cultura de honestidad, solidaridad y cuidado del patrimonio dentro de la comunidad uniautónoma.

        Directrices Clave de Custodia:
        
        • Entrega Inmediata en Portería:
        Todo objeto de valor alto (documentos de identidad, carné estudiantil, dispositivos electrónicos o billeteras) hallado en aulas, pasillos o cafeterías debe ser entregado en la Portería Principal o Punto Seguro de la sede más cercana en un lapso no mayor a 24 horas.

        • Protocolo de Reclamación con Token:
        Para retirar un objeto en custodia oficial de Portería, el estudiante o docente debe exhibir el Token Digital de Reclamo (TICKET-XXXX-RET) generado por la plataforma UniFind, junto con su carné físico o digital vigente.

        • Puntos de Honor y Reconocimiento:
        Los miembros de la comunidad que entreguen objetos extraviados acumularán Puntos de Honor e insignias de civismo que respaldan su compromiso con los valores institucionales de la Uniautónoma.
    """.trimIndent()

    const val TIC_TITLE = "Mesa de Ayuda TIC"
    const val TIC_SUBTITLE = "Soporte Técnico Institucional"
    val TIC_CONTENT = """
        ¿Tienes problemas para acceder con tu correo institucional @uniautonoma.edu.co o requieres asistencia técnica en la plataforma UniFind?

        Canales de Atención Oficiales:
        • Correo Institucional: soporte@uniautonoma.edu.co
        • Ubicación: Dirección de TIC • Sede Principal La Casona, Piso 2
        • Horario de Atención: Lunes a Viernes de 8:00 a.m. a 12:00 m. y de 2:00 p.m. a 6:00 p.m.

        Para solicitudes relacionadas con restablecimiento de credenciales de correo universitario, por favor adjunta copia de tu carné estudiantil o documento de identidad.
    """.trimIndent()
}
