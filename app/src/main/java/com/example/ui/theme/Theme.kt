package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PortalDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = MidnightNavy,
    primaryContainer = DarkCyanContainer,
    onPrimaryContainer = SoftCyan,
    secondary = AmberGold,
    onSecondary = MidnightNavy,
    secondaryContainer = DarkAmberContainer,
    onSecondaryContainer = WarmAmberLight,
    tertiary = EmeraldGreen,
    onTertiary = MidnightNavy,
    tertiaryContainer = DarkEmeraldContainer,
    onTertiaryContainer = Color(0xFFD1FAE5),
    background = MidnightNavy,
    onBackground = CrispWhite,
    surface = DeepSlate,
    onSurface = CrispWhite,
    surfaceVariant = CardSlate,
    onSurfaceVariant = MutedSlateText,
    surfaceContainerLow = DeepSlate,
    surfaceContainer = CardSlate,
    surfaceContainerHigh = ElevatedSlate,
    outline = Color(0xFF2B4270),
    error = CoralRed,
    onError = Color.White,
    errorContainer = DarkCoralContainer,
    onErrorContainer = Color(0xFFFEE2E2)
)

private val PortalLightColorScheme = lightColorScheme(
    primary = DeepCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFFAFE),
    onPrimaryContainer = Color(0xFF083344),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = WarmAmberLight,
    onSecondaryContainer = Color(0xFF451A03),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF022C22),
    background = IvoryBackground,
    onBackground = InkText,
    surface = PureCardLight,
    onSurface = InkText,
    surfaceVariant = SoftSurfaceLight,
    onSurfaceVariant = MutedInk,
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = PureCardLight,
    surfaceContainerHigh = SoftSurfaceLight,
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D)
)

val PortalShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun IcmaM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PortalDarkColorScheme else PortalLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PortalTypography,
        shapes = PortalShapes,
        content = content
    )
}
