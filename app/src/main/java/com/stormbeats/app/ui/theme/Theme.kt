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

// Signature accent — YouTube Music red
val AccentRed        = Color(0xFFFF0000)
val AccentRedDim     = Color(0xFFCC0000)
val AccentRedSoft    = Color(0xFFFF4444)
val OnAccent         = Color(0xFFFFFFFF)

// Dark palette — deep black like YouTube Music
private val DarkColorScheme = darkColorScheme(
    primary              = AccentRed,
    onPrimary            = OnAccent,
    primaryContainer     = Color(0xFF3A0000),
    onPrimaryContainer   = Color(0xFFFFB3B3),
    secondary            = Color(0xFFB0B0B0),
    onSecondary          = Color(0xFF000000),
    secondaryContainer   = Color(0xFF1F1F1F),
    onSecondaryContainer = Color(0xFFE0E0E0),
    tertiary             = Color(0xFFFF8A65),
    onTertiary           = Color(0xFF000000),
    tertiaryContainer    = Color(0xFF3E1A00),
    onTertiaryContainer  = Color(0xFFFFD0B3),
    background           = Color(0xFF0F0F0F),
    onBackground         = Color(0xFFFFFFFF),
    surface              = Color(0xFF0F0F0F),
    onSurface            = Color(0xFFFFFFFF),
    surfaceVariant       = Color(0xFF1A1A1A),
    onSurfaceVariant     = Color(0xFFAAAAAA),
    surfaceContainer         = Color(0xFF181818),
    surfaceContainerHigh     = Color(0xFF212121),
    surfaceContainerHighest  = Color(0xFF2A2A2A),
    outline              = Color(0xFF333333),
    outlineVariant       = Color(0xFF282828),
    inverseSurface       = Color(0xFFF5F5F5),
    inverseOnSurface     = Color(0xFF0F0F0F),
    inversePrimary       = AccentRedDim,
    error                = Color(0xFFFF5252),
    errorContainer       = Color(0xFF3B0000),
    onError              = Color(0xFFFFFFFF),
    onErrorContainer     = Color(0xFFFFB3B3),
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
