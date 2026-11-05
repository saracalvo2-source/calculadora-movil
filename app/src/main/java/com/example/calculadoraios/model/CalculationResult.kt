package com.example.calculadoraios.model

/**
 * Resultado de una operación: un valor numérico o un error controlado.
 * Evita lanzar excepciones hacia las capas superiores.
 */
sealed interface CalculationResult {  // Respuesta del motor: siempre Success o Failure, así la app no se cierra
    data class Success(val value: Double) : CalculationResult  // Cuenta válida con su resultado
    data class Failure(val error: CalculatorError) : CalculationResult  // Cuenta con error
}
