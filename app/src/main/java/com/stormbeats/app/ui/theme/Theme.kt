package com.stormbeats.app.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stormbeats.app.R

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular,  FontWeight.Normal),
    Font(R.font.poppins_medium,   FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold,     FontWeight.Bold),
)

val StormBeatsTypography = Typography(
    displayLarge   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 57.sp),
    headlineLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 28.sp),
    headlineSmall  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge      = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp),
    labelMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp),
    labelSmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp),
)

val StormBeatsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// Light purple/white theme matching reference image
val Purple600    = Color(0xFF7B2FBE)
val Purple500    = Color(0xFF9747FF)
val Purple400    = Color(0xFFAA6FFF)
val Purple100    = Color(0xFFEDE7FF)
val Purple50     = Color(0xFFF5F0FF)
val PinkAccent   = Color(0xFFE040FB)
val BgLight      = Color(0xFFF2EEFF)   // very light lavender background
val SurfaceWhite = Color(0xFFFFFFFF)
val OnBg         = Color(0xFF1A1A2E)
val OnBgSecond   = Color(0xFF7A7A9A)

private val LightColorScheme = lightColorScheme(
    primary              = Purple500,
    onPrimary            = Color.White,
    primaryContainer     = Purple100,
    onPrimaryContainer   = Purple600,
    secondary            = PinkAccent,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFFFD6FF),
    onSecondaryContainer = Color(0xFF4A004F),
    background           = BgLight,
    onBackground         = OnBg,
    surface              = SurfaceWhite,
    onSurface            = OnBg,
    surfaceVariant       = Color(0xFFEEE8FF),
    onSurfaceVariant     = OnBgSecond,
    surfaceContainer         = Color(0xFFF8F5FF),
    surfaceContainerHigh     = Color(0xFFEFE9FF),
    surfaceContainerHighest  = Color(0xFFE8E0FF),
    outline              = Color(0xFFCCC5E0),
    outlineVariant       = Color(0xFFE5E0F0),
    error                = Color(0xFFBA1A1A),
)

@Composable
fun StormBeatsTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(context)
    } else {
        LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = StormBeatsTypography,
        shapes      = StormBeatsShapes,
        content     = content,
    )
}
