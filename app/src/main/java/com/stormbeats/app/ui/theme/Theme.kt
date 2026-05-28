package com.stormbeats.app.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.stormbeats.app.R

// ── Fonts ─────────────────────────────────────────────────────────────────────
val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular,  FontWeight.Normal),
    Font(R.font.poppins_medium,   FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold,     FontWeight.Bold),
)

val StormBeatsTypography = Typography(
    displayLarge   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 57.sp),
    headlineLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 26.sp, lineHeight = 34.sp),
    headlineSmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 30.sp),
    titleLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge      = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp),
)

val StormBeatsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// ── Color tokens ──────────────────────────────────────────────────────────────
// Primary purple — matches the screenshot's violet accent
val Purple         = Color(0xFF7C3AED)
val PurpleLight    = Color(0xFF9D65F5)
val PurpleDim      = Color(0xFF5B21B6)
val Pink           = Color(0xFFEC4899)
val PinkLight      = Color(0xFFF472B6)
val Cyan           = Color(0xFF06B6D4)

// Surface scale — very dark, near-black
val Bg             = Color(0xFF0A0A12)  // Page background
val Surface0       = Color(0xFF0F0F1A)  // Cards
val Surface1       = Color(0xFF161625)  // Elevated cards
val Surface2       = Color(0xFF1E1E30)  // Chip / button background
val Surface3       = Color(0xFF252540)  // Border / divider

// Text
val OnBg           = Color(0xFFFFFFFF)
val OnBgSec        = Color(0xFFAAAAAA)
val OnBgTer        = Color(0xFF5A5A7A)

// Convenience aliases kept for backward compat
val VioletPrimary   = Purple
val PinkAccent      = Pink
val CyanAccent      = Cyan
val VioletSoft      = PurpleLight
val PinkSoft        = PinkLight
val GradientStart   = Purple
val GradientEnd     = Pink
val SurfaceDark     = Bg
val SurfaceCard     = Surface0
val SurfaceElevated = Surface1

// ── Color scheme ──────────────────────────────────────────────────────────────
private val DarkScheme = darkColorScheme(
    primary              = Purple,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFF2D1566),
    onPrimaryContainer   = Color(0xFFD8B4FE),
    secondary            = Pink,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFF500724),
    onSecondaryContainer = Color(0xFFFBCFE8),
    tertiary             = Cyan,
    onTertiary           = Color.Black,
    background           = Bg,
    onBackground         = OnBg,
    surface              = Bg,
    onSurface            = OnBg,
    surfaceVariant       = Surface0,
    onSurfaceVariant     = OnBgSec,
    surfaceContainer         = Surface0,
    surfaceContainerHigh     = Surface1,
    surfaceContainerHighest  = Surface2,
    outline              = Surface3,
    outlineVariant       = Surface2,
    inverseSurface       = Color(0xFFF0EFFF),
    inverseOnSurface     = Bg,
    inversePrimary       = PurpleLight,
    error                = Color(0xFFFF6B6B),
)

@Composable
fun StormBeatsTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val scheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        dynamicDarkColorScheme(context) else DarkScheme
    MaterialTheme(colorScheme = scheme, typography = StormBeatsTypography, shapes = StormBeatsShapes, content = content)
}
