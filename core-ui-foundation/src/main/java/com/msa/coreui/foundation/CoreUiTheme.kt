package com.msa.coreui.foundation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandPrimary = Color(0xFF5B3DF5)
private val BrandSecondary = Color(0xFF625B71)
private val BrandTertiary = Color(0xFF7D5260)

val CoreUiLightColorScheme: ColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE7DEFF),
    onPrimaryContainer = Color(0xFF211347),
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1E2F4),
    onSecondaryContainer = Color(0xFF191A2C),
    tertiary = BrandTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF4D9FF),
    onTertiaryContainer = Color(0xFF2D0F43),
    error = Color(0xFFBA1A1A),
    background = Color(0xFFFAF8FF),
    surface = Color(0xFFFAF8FF),
    surfaceVariant = Color(0xFFE4E1EC),
    outline = Color(0xFF777680),
)

val CoreUiDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFCBBEFF),
    onPrimary = Color(0xFF2A146D),
    primaryContainer = Color(0xFF4630B6),
    onPrimaryContainer = Color(0xFFE8DEFF),
    secondary = Color(0xFFC5C5D8),
    onSecondary = Color(0xFF2E2F42),
    secondaryContainer = Color(0xFF454659),
    onSecondaryContainer = Color(0xFFE1E2F4),
    tertiary = Color(0xFFE3B7F6),
    onTertiary = Color(0xFF44265B),
    tertiaryContainer = Color(0xFF5D3C73),
    onTertiaryContainer = Color(0xFFF4D9FF),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF121318),
    surface = Color(0xFF121318),
    surfaceVariant = Color(0xFF46464F),
    outline = Color(0xFF91909A),
)

val CoreUiTypography: Typography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 57.sp, lineHeight = 64.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
)

val CoreUiShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Immutable
data class CoreUiStatusColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
)

val CoreUiLightStatusColors = CoreUiStatusColors(
    success = Color(0xFF176B3A),
    onSuccess = Color.White,
    successContainer = Color(0xFFA9F4BC),
    onSuccessContainer = Color(0xFF00210D),
    warning = Color(0xFF765A00),
    onWarning = Color.White,
    warningContainer = Color(0xFFFFE08A),
    onWarningContainer = Color(0xFF241A00),
    info = Color(0xFF00639A),
    onInfo = Color.White,
    infoContainer = Color(0xFFCDE5FF),
    onInfoContainer = Color(0xFF001D32),
)

val CoreUiDarkStatusColors = CoreUiStatusColors(
    success = Color(0xFF8DD9A2),
    onSuccess = Color(0xFF00391A),
    successContainer = Color(0xFF005227),
    onSuccessContainer = Color(0xFFA9F4BC),
    warning = Color(0xFFF1C64B),
    onWarning = Color(0xFF3D2F00),
    warningContainer = Color(0xFF574500),
    onWarningContainer = Color(0xFFFFE08A),
    info = Color(0xFF96CCFF),
    onInfo = Color(0xFF003352),
    infoContainer = Color(0xFF004B75),
    onInfoContainer = Color(0xFFCDE5FF),
)

private val LocalCoreUiSpacing = staticCompositionLocalOf { CoreUiSpacing() }
private val LocalCoreUiRadii = staticCompositionLocalOf { CoreUiRadii() }
private val LocalCoreUiElevation = staticCompositionLocalOf { CoreUiElevation() }
private val LocalCoreUiAlpha = staticCompositionLocalOf { CoreUiAlpha() }
private val LocalCoreUiStateLayers = staticCompositionLocalOf { CoreUiStateLayers() }
private val LocalCoreUiStroke = staticCompositionLocalOf { CoreUiStroke() }
private val LocalCoreUiMotion = staticCompositionLocalOf { CoreUiMotion() }
private val LocalCoreUiSizing = staticCompositionLocalOf { CoreUiSizing() }
private val LocalCoreUiBreakpoints = staticCompositionLocalOf { CoreUiBreakpoints() }
private val LocalCoreUiStatusColors = staticCompositionLocalOf { CoreUiLightStatusColors }
private val LocalCoreUiStrings = staticCompositionLocalOf { CoreUiStrings.English }

/** Access to design-system tokens from composables. */
object CoreUiTheme {
    val spacing: CoreUiSpacing
        @Composable @ReadOnlyComposable get() = LocalCoreUiSpacing.current
    val radii: CoreUiRadii
        @Composable @ReadOnlyComposable get() = LocalCoreUiRadii.current
    val elevation: CoreUiElevation
        @Composable @ReadOnlyComposable get() = LocalCoreUiElevation.current
    val alpha: CoreUiAlpha
        @Composable @ReadOnlyComposable get() = LocalCoreUiAlpha.current
    val stateLayers: CoreUiStateLayers
        @Composable @ReadOnlyComposable get() = LocalCoreUiStateLayers.current
    val stroke: CoreUiStroke
        @Composable @ReadOnlyComposable get() = LocalCoreUiStroke.current
    val motion: CoreUiMotion
        @Composable @ReadOnlyComposable get() = LocalCoreUiMotion.current
    val sizing: CoreUiSizing
        @Composable @ReadOnlyComposable get() = LocalCoreUiSizing.current
    val breakpoints: CoreUiBreakpoints
        @Composable @ReadOnlyComposable get() = LocalCoreUiBreakpoints.current
    val statusColors: CoreUiStatusColors
        @Composable @ReadOnlyComposable get() = LocalCoreUiStatusColors.current
    val strings: CoreUiStrings
        @Composable @ReadOnlyComposable get() = LocalCoreUiStrings.current
}

/** Root theme for all MSA Core UI components. Dynamic color remains opt-in for brand stability. */
@Composable
fun CoreUiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    lightColorScheme: ColorScheme = CoreUiLightColorScheme,
    darkColorScheme: ColorScheme = CoreUiDarkColorScheme,
    typography: Typography = CoreUiTypography,
    shapes: Shapes = CoreUiShapes,
    spacing: CoreUiSpacing = CoreUiSpacing(),
    radii: CoreUiRadii = CoreUiRadii(),
    elevation: CoreUiElevation = CoreUiElevation(),
    alpha: CoreUiAlpha = CoreUiAlpha(),
    stateLayers: CoreUiStateLayers = CoreUiStateLayers(),
    stroke: CoreUiStroke = CoreUiStroke(),
    motion: CoreUiMotion = CoreUiMotion(),
    sizing: CoreUiSizing = CoreUiSizing(),
    breakpoints: CoreUiBreakpoints = CoreUiBreakpoints(),
    lightStatusColors: CoreUiStatusColors = CoreUiLightStatusColors,
    darkStatusColors: CoreUiStatusColors = CoreUiDarkStatusColors,
    strings: CoreUiStrings = CoreUiStrings.English,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    val statusColors = if (darkTheme) darkStatusColors else lightStatusColors

    CompositionLocalProvider(
        LocalCoreUiSpacing provides spacing,
        LocalCoreUiRadii provides radii,
        LocalCoreUiElevation provides elevation,
        LocalCoreUiAlpha provides alpha,
        LocalCoreUiStateLayers provides stateLayers,
        LocalCoreUiStroke provides stroke,
        LocalCoreUiMotion provides motion,
        LocalCoreUiSizing provides sizing,
        LocalCoreUiBreakpoints provides breakpoints,
        LocalCoreUiStatusColors provides statusColors,
        LocalCoreUiStrings provides strings,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content,
        )
    }
}
