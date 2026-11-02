package com.example.calculadoraios.model

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor de cálculo puro: no conoce Android ni la interfaz.
 * Toda regla aritmética y de formato numérico vive aquí.
 */
class CalculatorEngine {

    /** Aplica una operación binaria validando los casos límite. */
    fun calculate(first: Double, second: Double, operation: Operation): CalculationResult {
        if (first.isNaN() || second.isNaN() || first.isInfinite() || second.isInfinite()) {
            return CalculationResult.Failure(CalculatorError.INVALID_INPUT)
        }
        val value = when (operation) {
            Operation.ADD -> first + second
            Operation.SUBTRACT -> first - second
            Operation.MULTIPLY -> first * second
            Operation.DIVIDE -> {
                if (second == 0.0) return CalculationResult.Failure(CalculatorError.DIVISION_BY_ZERO)
                first / second
            }
        }
        return if (value.isInfinite() || value.isNaN()) {
            CalculationResult.Failure(CalculatorError.INVALID_INPUT)
        } else {
            CalculationResult.Success(value)
        }
    }

    /** Convierte el valor a su porcentaje (divide entre 100). */
    fun percent(value: Double): Double = value / PERCENT_DIVISOR

    /** Invierte el signo del valor. */
    fun negate(value: Double): Double = -value

    /** Interpreta el texto ingresado como número; null si no es válido. */
    fun parse(input: String): Double? = input.toDoubleOrNull()

    /**
     * Formatea un número para mostrarlo: sin ceros sobrantes, con punto decimal
     * y sin notación científica para magnitudes razonables.
     */
    fun format(value: Double): String {
        val decimal = BigDecimal(value)
            .setScale(MAX_DECIMALS, RoundingMode.HALF_UP)
            .stripTrailingZeros()
        val plain = decimal.toPlainString()
        return if (plain == "-0") "0" else plain
    }

    private companion object {
        const val PERCENT_DIVISOR = 100.0
        const val MAX_DECIMALS = 10
    }
}
