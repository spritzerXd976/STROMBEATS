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

// ─────────────────────────────────────────────────────────────────────────────
// Fonts
// ─────────────────────────────────────────────────────────────────────────────

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular,  FontWeight.Normal),
    Font(R.font.poppins_medium,   FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold,     FontWeight.Bold),
)

val StormBeatsTypography = Typography(
    displayLarge   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge      = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp),
)

val StormBeatsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// ─────────────────────────────────────────────────────────────────────────────
// Colour palette — Premium dark violet/pink
// ─────────────────────────────────────────────────────────────────────────────

val VioletPrimary   = Color(0xFF7C3AED)
val PinkAccent      = Color(0xFFEC4899)
val CyanAccent      = Color(0xFF06B6D4)
val VioletSoft      = Color(0xFF8B5CF6)
val PinkSoft        = Color(0xFFF472B6)
val GradientStart   = Color(0xFF7C3AED)
val GradientEnd     = Color(0xFFEC4899)
val SurfaceDark     = Color(0xFF0D0D14)
val SurfaceCard     = Color(0xFF13131E)
val SurfaceElevated = Color(0xFF1A1A2E)
val OnAccent        = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary              = VioletPrimary,
    onPrimary            = OnAccent,
    primaryContainer     = Color(0xFF2D1566),
    onPrimaryContainer   = Color(0xFFD8B4FE),
    secondary            = PinkAccent,
    onSecondary          = Color(0xFFFFFFFF),
    secondaryContainer   = Color(0xFF500724),
    onSecondaryContainer = Color(0xFFFBCFE8),
    tertiary             = CyanAccent,
    onTertiary           = Color(0xFF000000),
    tertiaryContainer    = Color(0xFF083344),
    onTertiaryContainer  = Color(0xFFA5F3FC),
    background           = SurfaceDark,
    onBackground         = Color(0xFFFFFFFF),
    surface              = SurfaceDark,
    onSurface            = Color(0xFFFFFFFF),
    surfaceVariant       = SurfaceCard,
    onSurfaceVariant     = Color(0xFFB0B0C8),
    surfaceContainer         = SurfaceCard,
    surfaceContainerHigh     = SurfaceElevated,
    surfaceContainerHighest  = Color(0xFF22223A),
    outline              = Color(0xFF2E2E4A),
    outlineVariant       = Color(0xFF1E1E32),
    inverseSurface       = Color(0xFFF0EFFF),
    inverseOnSurface     = SurfaceDark,
    inversePrimary       = VioletSoft,
    error                = Color(0xFFF87171),
    errorContainer       = Color(0xFF3B0000),
    onError              = Color(0xFFFFFFFF),
    onErrorContainer     = Color(0xFFFFB3B3),
)

// ─────────────────────────────────────────────────────────────────────────────
// Theme
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StormBeatsTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context)
    } else {
        DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = StormBeatsTypography,
        shapes      = StormBeatsShapes,
        content     = content,
    )
}
