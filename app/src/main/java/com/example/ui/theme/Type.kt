package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val DMSansFontFamily = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal),
    Font(R.font.dm_sans, FontWeight.Medium),
    Font(R.font.dm_sans, FontWeight.Bold)
)

val NewsreaderFontFamily = FontFamily(
    Font(R.font.newsreader, FontWeight.Normal),
    Font(R.font.newsreader, FontWeight.Medium),
    Font(R.font.newsreader, FontWeight.SemiBold),
    Font(R.font.newsreader_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.newsreader_italic, FontWeight.Medium, FontStyle.Italic)
)

val IbmPlexMonoFontFamily = FontFamily(
    Font(R.font.ibm_plex_mono, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.54).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 29.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.5.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = IbmPlexMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.4.sp
    )
)
