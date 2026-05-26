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

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

val StormBeatsTypography = Typography(
    displayLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp),
)

val StormBeatsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// Signature accent — electric green (Spotify-style)
val AccentGreen     = Color(0xFF1DB954)
val AccentGreenDim  = Color(0xFF14833B)
val OnAccent        = Color(0xFF000000)

private val DarkColorScheme = darkColorScheme(
    primary              = AccentGreen,
    onPrimary            = OnAccent,
    primaryContainer     = Color(0xFF0A2E17),
    onPrimaryContainer   = Color(0xFFB7F5C8),
    secondary            = Color(0xFF9E9E9E),
    onSecondary          = Color(0xFF000000),
    secondaryContainer   = Color(0xFF1E1E1E),
    onSecondaryContainer = Color(0xFFE0E0E0),
    tertiary             = Color(0xFF80CBC4),
    onTertiary           = Color(0xFF000000),
    background           = Color(0xFF080808),
    onBackground         = Color(0xFFF5F5F5),
    surface              = Color(0xFF0E0E0E),
    onSurface            = Color(0xFFF5F5F5),
    surfaceVariant       = Color(0xFF1A1A1A),
    onSurfaceVariant     = Color(0xFF9E9E9E),
    surfaceContainer         = Color(0xFF161616),
    surfaceContainerHigh     = Color(0xFF1C1C1C),
    surfaceContainerHighest  = Color(0xFF242424),
    outline              = Color(0xFF2E2E2E),
    outlineVariant       = Color(0xFF1E1E1E),
    inverseSurface       = Color(0xFFF5F5F5),
    inverseOnSurface     = Color(0xFF0E0E0E),
    inversePrimary       = AccentGreenDim,
)

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
