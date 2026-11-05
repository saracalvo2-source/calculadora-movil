package com.example.calculadoraios.ui.keypad

import com.example.calculadoraios.R
import com.example.calculadoraios.model.Operation
import com.example.calculadoraios.viewmodel.CalculatorEvent

/**
 * Distribución del teclado, fila por fila, replicando la calculadora de iOS.
 * Al estar declarada como datos, la pantalla solo la recorre y dibuja.
 */
object KeypadLayout {  // Distribución del teclado igual a iOS

    private const val WIDE_KEY_WEIGHT = 2f

    private val clearKey = CalculatorKey(R.string.action_clear, CalculatorEvent.Clear, KeyStyle.CONTROL)  // Tecla AC
    private val deleteKey = CalculatorKey(R.string.action_delete, CalculatorEvent.DeleteDigit, KeyStyle.CONTROL)  // Tecla ⌫

    /** Filas del teclado; la primera tecla es ⌫ mientras se escribe y AC en otro caso. */
    fun rows(showDeleteKey: Boolean): List<List<CalculatorKey>> = listOf(  // Filas del teclado, de arriba a abajo
        listOf(
            if (showDeleteKey) deleteKey else clearKey,  // ⌫ mientras se escribe, AC si no
            CalculatorKey(R.string.symbol_toggle_sign, CalculatorEvent.ToggleSign, KeyStyle.CONTROL),
            CalculatorKey(R.string.symbol_percent, CalculatorEvent.Percent, KeyStyle.CONTROL),
            operatorKey(R.string.symbol_divide, Operation.DIVIDE)
        ),
        listOf(
            digitKey(R.string.digit_7, 7),
            digitKey(R.string.digit_8, 8),
            digitKey(R.string.digit_9, 9),
            operatorKey(R.string.symbol_multiply, Operation.MULTIPLY)
        ),
        listOf(
            digitKey(R.string.digit_4, 4),
            digitKey(R.string.digit_5, 5),
            digitKey(R.string.digit_6, 6),
            operatorKey(R.string.symbol_subtract, Operation.SUBTRACT)
        ),
        listOf(
            digitKey(R.string.digit_1, 1),
            digitKey(R.string.digit_2, 2),
            digitKey(R.string.digit_3, 3),
            operatorKey(R.string.symbol_add, Operation.ADD)
        ),
        listOf(
            CalculatorKey(R.string.digit_0, CalculatorEvent.Digit(0), KeyStyle.NUMBER, WIDE_KEY_WEIGHT),  // El 0 ocupa doble ancho
            CalculatorKey(R.string.symbol_decimal, CalculatorEvent.Decimal, KeyStyle.NUMBER),
            CalculatorKey(R.string.symbol_equals, CalculatorEvent.Equals, KeyStyle.OPERATOR)
        )
    )

    private fun digitKey(labelRes: Int, digit: Int) =
        CalculatorKey(labelRes, CalculatorEvent.Digit(digit), KeyStyle.NUMBER)

    private fun operatorKey(labelRes: Int, operation: Operation) =
        CalculatorKey(labelRes, CalculatorEvent.Operator(operation), KeyStyle.OPERATOR)
}
