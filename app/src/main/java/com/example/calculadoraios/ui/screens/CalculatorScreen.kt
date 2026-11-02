package com.example.calculadoraios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoraios.R
import com.example.calculadoraios.model.Operation
import com.example.calculadoraios.ui.components.CalculatorButton
import com.example.calculadoraios.ui.components.CalculatorDisplay
import com.example.calculadoraios.ui.keypad.CalculatorKey
import com.example.calculadoraios.ui.keypad.KeypadLayout
import com.example.calculadoraios.viewmodel.CalculatorEvent
import com.example.calculadoraios.viewmodel.CalculatorUiState

private val SCREEN_PADDING = 16.dp
private val KEY_SPACING = 12.dp

/**
 * Pantalla principal. Solo representa [uiState] y reenvía eventos:
 * no contiene reglas de cálculo.
 */
@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onEvent: (CalculatorEvent) -> Unit,
    onOpenHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.calculator_background))
            .safeDrawingPadding()
            .padding(SCREEN_PADDING),
        verticalArrangement = Arrangement.Bottom
    ) {
        HistoryLink(onClick = onOpenHistory, modifier = Modifier.weight(1f))
        CalculatorDisplay(
            displayValue = uiState.displayValue,
            pendingOperand = uiState.pendingOperand,
            pendingOperation = uiState.pendingOperation,
            error = uiState.error,
            modifier = Modifier.padding(bottom = KEY_SPACING)
        )
        Keypad(
            activeOperation = uiState.pendingOperation,
            onEvent = onEvent
        )
    }
}

/** Acceso al historial, ubicado en la zona superior libre de la pantalla. */
@Composable
private fun HistoryLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = stringResource(R.string.history_button),
                fontSize = 18.sp,
                color = colorResource(R.color.button_operator_background)
            )
        }
    }
}

/** Dibuja el teclado a partir de la distribución declarada en [KeypadLayout]. */
@Composable
private fun Keypad(
    activeOperation: Operation?,
    onEvent: (CalculatorEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(KEY_SPACING)) {
        KeypadLayout.rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KEY_SPACING)
            ) {
                row.forEach { key ->
                    CalculatorButton(
                        key = key,
                        isActive = isActiveOperator(key, activeOperation),
                        onClick = { onEvent(key.event) },
                        modifier = Modifier.weight(key.widthWeight)
                    )
                }
            }
        }
    }
}

/** Una tecla se resalta solo si es el operador pendiente de resolver. */
private fun isActiveOperator(key: CalculatorKey, activeOperation: Operation?): Boolean {
    val event = key.event
    return event is CalculatorEvent.Operator && event.operation == activeOperation
}
