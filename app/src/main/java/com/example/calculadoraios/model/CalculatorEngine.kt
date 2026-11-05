package com.example.calculadoraios.model

import java.math.BigDecimal  // Para formatear decimales sin errores de precisión
import java.math.RoundingMode

/**
 * Motor de cálculo puro: no conoce Android ni la interfaz.
 * Toda regla aritmética y de formato numérico vive aquí.
 */
class CalculatorEngine {  // Motor de cálculo (Model de MVVM). Lo usa CalculatorViewModel

    /** Aplica una operación binaria validando los casos límite. */
    fun calculate(first: Double, second: Double, operation: Operation): CalculationResult {  // Hace + − × ÷ y devuelve Success o Failure
        if (first.isNaN() || second.isNaN() || first.isInfinite() || second.isInfinite()) {  // Valida que los números de entrada sean válidos
            return CalculationResult.Failure(CalculatorError.INVALID_INPUT)
        }
        val value = when (operation) {
            Operation.ADD -> first + second
            Operation.SUBTRACT -> first - second
            Operation.MULTIPLY -> first * second
            Operation.DIVIDE -> {  // División
                if (second == 0.0) return CalculationResult.Failure(CalculatorError.DIVISION_BY_ZERO)  // Validación de división por cero
                first / second
            }
        }
        return if (value.isInfinite() || value.isNaN()) {  // Valida que el resultado sea válido
            CalculationResult.Failure(CalculatorError.INVALID_INPUT)
        } else {
            CalculationResult.Success(value)
        }
    }

    /** Convierte el valor a su porcentaje (divide entre 100). */
    fun percent(value: Double): Double = value / PERCENT_DIVISOR  // Tecla %: divide entre 100

    /** Invierte el signo del valor. */
    fun negate(value: Double): Double = -value  // Cambio de signo

    /** Interpreta el texto ingresado como número; null si no es válido. */
    fun parse(input: String): Double? = input.toDoubleOrNull()  // Texto a número, null si no se puede

    /**
     * Formatea un número para mostrarlo: sin ceros sobrantes, con punto decimal
     * y sin notación científica para magnitudes razonables.
     */
    fun format(value: Double): String {  // Número a texto para mostrar en pantalla
        val decimal = BigDecimal(value)  // Número de alta precisión
            .setScale(MAX_DECIMALS, RoundingMode.HALF_UP)  // Redondea a 10 decimales
            .stripTrailingZeros()  // Quita ceros sobrantes (8.0 -> 8)
        val plain = decimal.toPlainString()  // Sin notación científica
        return if (plain == "-0") "0" else plain  // Nunca muestra -0
    }

    private companion object {  // Valores fijos del motor
        const val PERCENT_DIVISOR = 100.0
        const val MAX_DECIMALS = 10
    }
}
