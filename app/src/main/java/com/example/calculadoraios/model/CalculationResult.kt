package com.example.calculadoraios.model

/**
 * Resultado de una operación: un valor numérico o un error controlado.
 * Evita lanzar excepciones hacia las capas superiores.
 */
sealed interface CalculationResult {
    data class Success(val value: Double) : CalculationResult
    data class Failure(val error: CalculatorError) : CalculationResult
}
