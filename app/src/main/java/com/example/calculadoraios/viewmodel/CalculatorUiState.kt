package com.example.calculadoraios.viewmodel

import com.example.calculadoraios.model.CalculatorError
import com.example.calculadoraios.model.HistoryEntry
import com.example.calculadoraios.model.Operation

/**
 * Estado inmutable que la UI representa.
 * Contiene únicamente datos; la traducción a textos y colores ocurre en Compose.
 */
data class CalculatorUiState(
    /** Número que se está escribiendo o último resultado. */
    val displayValue: String = "0",
    /** Primer operando ya confirmado, mostrado como vista previa de la expresión. */
    val pendingOperand: String? = null,
    /** Operación seleccionada a la espera del segundo operando. */
    val pendingOperation: Operation? = null,
    /** Error activo, si lo hay. */
    val error: CalculatorError? = null,
    /** true mientras se escribe un número que se puede borrar dígito a dígito (la tecla AC pasa a ⌫). */
    val canDeleteDigit: Boolean = false,
    /** Operaciones completadas, en orden cronológico. */
    val history: List<HistoryEntry> = emptyList()
)
