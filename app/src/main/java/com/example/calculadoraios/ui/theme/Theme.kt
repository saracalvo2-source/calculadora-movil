package com.example.calculadoraios.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/** La calculadora siempre usa esquema oscuro, como la referencia de iOS. */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

@Composable
fun CalculadoraIOsTheme(content: @Composable () -> Unit) {  // Tema oscuro de la app (se usa en MainActivity.kt)
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
