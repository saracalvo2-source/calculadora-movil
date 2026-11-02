package com.example.calculadoraios.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoraios.R
import com.example.calculadoraios.ui.keypad.CalculatorKey
import com.example.calculadoraios.ui.keypad.KeyStyle

private const val BUTTON_CORNER_PERCENT = 50
private const val WIDE_BUTTON_EXTRA_RATIO = 0.08f

/**
 * Tecla estilo iOS (circular, o en forma de píldora si es ancha).
 * El color depende del estilo de la tecla y de si es el operador seleccionado.
 */
@Composable
fun CalculatorButton(
    key: CalculatorKey,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = buttonColors(key.style, isActive)
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(BUTTON_CORNER_PERCENT),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.background,
            contentColor = colors.content
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.aspectRatio(aspectRatioFor(key.widthWeight))
    ) {
        Text(
            text = stringResource(key.labelRes),
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** La tecla ancha compensa el espacio entre columnas que absorbe. */
private fun aspectRatioFor(widthWeight: Float): Float =
    if (widthWeight > 1f) widthWeight + WIDE_BUTTON_EXTRA_RATIO else widthWeight

/** Par de colores de fondo y contenido de una tecla. */
private data class ButtonColors(val background: Color, val content: Color)

@Composable
private fun buttonColors(style: KeyStyle, isActive: Boolean): ButtonColors = when (style) {
    KeyStyle.NUMBER -> ButtonColors(
        background = colorResource(R.color.button_number_background),
        content = colorResource(R.color.button_number_text)
    )
    KeyStyle.CONTROL -> ButtonColors(
        background = colorResource(R.color.button_control_background),
        content = colorResource(R.color.button_control_text)
    )
    KeyStyle.OPERATOR -> if (isActive) {
        ButtonColors(
            background = colorResource(R.color.button_operator_active_background),
            content = colorResource(R.color.button_operator_active_text)
        )
    } else {
        ButtonColors(
            background = colorResource(R.color.button_operator_background),
            content = colorResource(R.color.button_operator_text)
        )
    }
}
