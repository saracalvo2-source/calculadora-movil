package com.example.calculadoraios.model

/**
 * Errores semánticos que puede producir el cálculo.
 * La UI es la encargada de traducirlos a un mensaje legible.
 */
enum class CalculatorError {
    DIVISION_BY_ZERO,
    INVALID_INPUT
}
