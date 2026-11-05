package com.example.calculadoraios.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.calculadoraios.R
import com.example.calculadoraios.model.CalculatorError
import com.example.calculadoraios.model.Operation

private const val LONG_VALUE_THRESHOLD = 8
private const val VERY_LONG_VALUE_THRESHOLD = 12

/**
 * Pantalla de la calculadora: muestra la expresión pendiente en pequeño
 * y el valor actual (o el mensaje de error) en grande.
 */
@Composable
fun CalculatorDisplay(  // Display de números (se llama desde CalculatorScreen.kt)
    displayValue: String,
    pendingOperand: String?,
    pendingOperation: Operation?,
    error: CalculatorError?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = pendingExpression(pendingOperand, pendingOperation),  // Texto pequeño de arriba, ej: 5 +
            fontSize = 28.sp,
            color = colorResource(R.color.display_secondary_text),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = error?.let { errorMessage(it) } ?: displayValue,  // Si hay error muestra el mensaje, si no el número
            fontSize = mainFontSize(displayValue, error),
            color = colorResource(
                if (error != null) R.color.display_error_text else R.color.display_primary_text  // Rojo si hay error
            ),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Construye la vista previa "operando operador", o vacío si no hay operación pendiente. */
@Composable
private fun pendingExpression(operand: String?, operation: Operation?): String =
    if (operand != null && operation != null) "$operand ${operationSymbol(operation)}" else ""

/** Reduce el tamaño de fuente para que valores largos sigan siendo legibles. */
private fun mainFontSize(value: String, error: CalculatorError?) = when {  // Letra más pequeña si el número es largo
    error != null -> 40.sp
    value.length > VERY_LONG_VALUE_THRESHOLD -> 48.sp
    value.length > LONG_VALUE_THRESHOLD -> 64.sp
    else -> 88.sp
}
