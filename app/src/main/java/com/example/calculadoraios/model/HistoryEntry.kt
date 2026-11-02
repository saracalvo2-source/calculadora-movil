package com.example.calculadoraios.model

/**
 * Registro de una operación completada, por ejemplo 5 + 3 = 8.
 * Los operandos y el resultado se guardan ya formateados para mostrarse tal cual.
 */
data class HistoryEntry(
    val firstOperand: String,
    val operation: Operation,
    val secondOperand: String,
    val result: String
)
