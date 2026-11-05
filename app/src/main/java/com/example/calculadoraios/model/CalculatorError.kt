package com.example.calculadoraios.model

/**
 * Errores semánticos que puede producir el cálculo.
 * La UI es la encargada de traducirlos a un mensaje legible.
 */
enum class CalculatorError {  // Errores posibles. El mensaje lo pone OperationSymbol.kt desde strings.xml
    DIVISION_BY_ZERO,  // Al dividir entre 0
    INVALID_INPUT  // Número no válido
}
