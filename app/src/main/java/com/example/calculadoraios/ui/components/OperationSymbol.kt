package com.example.calculadoraios.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.calculadoraios.R
import com.example.calculadoraios.model.CalculatorError
import com.example.calculadoraios.model.Operation

/** Traduce una operación del modelo al símbolo definido en strings.xml. */
@Composable
fun operationSymbol(operation: Operation): String = stringResource(
    when (operation) {
        Operation.ADD -> R.string.symbol_add
        Operation.SUBTRACT -> R.string.symbol_subtract
        Operation.MULTIPLY -> R.string.symbol_multiply
        Operation.DIVIDE -> R.string.symbol_divide
    }
)

/** Traduce un error semántico del modelo al mensaje definido en strings.xml. */
@Composable
fun errorMessage(error: CalculatorError): String = stringResource(
    when (error) {
        CalculatorError.DIVISION_BY_ZERO -> R.string.error_division_by_zero
        CalculatorError.INVALID_INPUT -> R.string.error_invalid_input
    }
)
