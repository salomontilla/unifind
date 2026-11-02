package com.salomontilla.frontunifind.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.salomontilla.frontunifind.R

/**
 * =============================================================================
 * Familia Tipográfica Oficial: Museo Sans Rounded (Uniautónoma del Cauca)
 * =============================================================================
 */
val MuseoSansRounded = FontFamily(
    Font(R.font.museo_sans_rounded_100, FontWeight.Thin),
    Font(R.font.museo_sans_rounded_300, FontWeight.Light),
    Font(R.font.museo_sans_rounded_500, FontWeight.Normal),
    Font(R.font.museo_sans_rounded_500, FontWeight.Medium),
    Font(R.font.museo_sans_rounded_700, FontWeight.SemiBold),
    Font(R.font.museo_sans_rounded_700, FontWeight.Bold),
    Font(R.font.museo_sans_rounded_900, FontWeight.ExtraBold),
    Font(R.font.museo_sans_rounded_1000, FontWeight.Black)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        color = TextTitle
    ),
    titleLarge = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = TextTitle
    ),
    titleMedium = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = TextTitle
    ),
    bodyLarge = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextBody
    ),
    bodyMedium = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = TextBody
    ),
    bodySmall = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Light,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = SurfaceWhite
    ),
    labelSmall = TextStyle(
        fontFamily = MuseoSansRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        color = TextSecondary
    )
)
