package com.example.calculadoraios.ui.keypad

import androidx.annotation.StringRes
import com.example.calculadoraios.viewmodel.CalculatorEvent

/** Estilo visual de una tecla, según su función. */
enum class KeyStyle {
    NUMBER,
    OPERATOR,
    CONTROL
}

/**
 * Descripción de una tecla del teclado: qué texto muestra, qué evento emite
 * y cuánto espacio ocupa en su fila (el 0 de iOS ocupa el doble).
 */
data class CalculatorKey(
    @StringRes val labelRes: Int,
    val event: CalculatorEvent,
    val style: KeyStyle,
    val widthWeight: Float = 1f
)
