package com.example.calculadoraios.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calculadoraios.model.CalculationResult
import com.example.calculadoraios.model.CalculatorEngine
import com.example.calculadoraios.model.CalculatorError
import com.example.calculadoraios.model.HistoryEntry
import com.example.calculadoraios.model.Operation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Orquesta la entrada del usuario y expone el estado observable de la calculadora.
 * Delega toda la aritmética en [CalculatorEngine]; no contiene referencias a Android UI.
 *
 * Comportamiento definido:
 * - AC limpia el cálculo en curso (el historial se conserva).
 * - Mientras se escribe un número, la tecla AC pasa a ⌫ y borra el último dígito;
 *   al quedar en 0, o tras un resultado, vuelve a ser AC (como en iOS).
 * - Borrar historial vacía la lista sin afectar el cálculo en curso.
 * - Un operador tras otro operador solo reemplaza la operación pendiente.
 * - Operaciones encadenadas (2 + 3 × ...) resuelven la anterior de izquierda a derecha.
 * - Pulsar = de forma repetida vuelve a aplicar la última operación al resultado.
 * - % divide el valor actual entre 100; +/- invierte su signo.
 * - Un error se muestra sin cerrar la app y se limpia con la siguiente tecla.
 */
class CalculatorViewModel(
    private val engine: CalculatorEngine = CalculatorEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    // Estado interno de edición
    private var currentInput = INITIAL_INPUT
    private var startNewInput = true
    private var accumulator: Double? = null
    private var pendingOperation: Operation? = null

    // Memoria para repetir la última operación con "="
    private var lastOperation: Operation? = null
    private var lastOperand: Double? = null

    /** Punto único de entrada de eventos desde la interfaz. */
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Digit -> onDigit(event.value)
            is CalculatorEvent.Operator -> onOperator(event.operation)
            CalculatorEvent.Decimal -> onDecimal()
            CalculatorEvent.Equals -> onEquals()
            CalculatorEvent.Clear -> onClear()
            CalculatorEvent.ToggleSign -> onToggleSign()
            CalculatorEvent.Percent -> onPercent()
            CalculatorEvent.DeleteDigit -> onDeleteDigit()
            CalculatorEvent.ClearHistory -> onClearHistory()
        }
        publishState()
    }

    private fun onDigit(digit: Int) {
        if (hasError()) resetCalculation()
        currentInput = when {
            startNewInput -> digit.toString()
            currentInput == INITIAL_INPUT -> digit.toString()
            currentInput == NEGATIVE_ZERO -> "-$digit"
            digitCount(currentInput) >= MAX_DIGITS -> currentInput
            else -> currentInput + digit
        }
        startNewInput = false
    }

    private fun onDecimal() {
        if (hasError()) resetCalculation()
        when {
            startNewInput -> currentInput = INITIAL_INPUT + DECIMAL_SEPARATOR
            !currentInput.contains(DECIMAL_SEPARATOR) -> currentInput += DECIMAL_SEPARATOR
        }
        startNewInput = false
    }

    private fun onOperator(operation: Operation) {
        if (hasError()) return
        val current = engine.parse(currentInput) ?: return failWith(CalculatorError.INVALID_INPUT)
        when {
            // Primer operando de una nueva expresión
            pendingOperation == null -> accumulator = current
            // Operación encadenada: se resuelve la anterior antes de continuar
            !startNewInput -> if (!resolvePending(current)) return
            // Solo se cambia el operador pendiente
        }
        pendingOperation = operation
        startNewInput = true
    }

    private fun onEquals() {
        if (hasError()) return
        val current = engine.parse(currentInput) ?: return failWith(CalculatorError.INVALID_INPUT)
        val previousOperand = lastOperand
        val previousOperation = lastOperation
        when {
            pendingOperation != null && accumulator != null -> {
                resolvePending(current)
                pendingOperation = null
            }
            previousOperation != null && previousOperand != null -> {
                // "=" repetido: aplica de nuevo la última operación al valor actual
                accumulator = current
                applyOperation(current, previousOperand, previousOperation)
            }
        }
        startNewInput = true
    }

    private fun onClear() {
        resetCalculation()
        lastOperation = null
        lastOperand = null
    }

    private fun onToggleSign() {
        if (hasError()) return
        if (currentInput == INITIAL_INPUT) return
        currentInput = if (currentInput.startsWith(NEGATIVE_SIGN)) {
            currentInput.removePrefix(NEGATIVE_SIGN)
        } else {
            NEGATIVE_SIGN + currentInput
        }
    }

    private fun onPercent() {
        if (hasError()) return
        val value = engine.parse(currentInput) ?: return failWith(CalculatorError.INVALID_INPUT)
        currentInput = engine.format(engine.percent(value))
        startNewInput = true
    }

    /** Borra el último dígito escrito; si no queda ninguno, el valor vuelve a 0. */
    private fun onDeleteDigit() {
        if (!canDeleteDigit()) return
        val remaining = currentInput.dropLast(1)
        currentInput = if (remaining.isEmpty() || remaining == NEGATIVE_SIGN) INITIAL_INPUT else remaining
    }

    private fun onClearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    /**
     * Resuelve la operación pendiente con [second] como segundo operando.
     * @return true si el cálculo fue válido, false si produjo un error.
     */
    private fun resolvePending(second: Double): Boolean {
        val first = accumulator ?: return false
        val operation = pendingOperation ?: return false
        return applyOperation(first, second, operation)
    }

    /** Ejecuta la operación, registra el historial y deja el resultado como valor actual. */
    private fun applyOperation(first: Double, second: Double, operation: Operation): Boolean {
        return when (val result = engine.calculate(first, second, operation)) {
            is CalculationResult.Success -> {
                recordHistory(first, operation, second, result.value)
                accumulator = result.value
                currentInput = engine.format(result.value)
                lastOperation = operation
                lastOperand = second
                true
            }
            is CalculationResult.Failure -> {
                failWith(result.error)
                false
            }
        }
    }

    private fun recordHistory(first: Double, operation: Operation, second: Double, result: Double) {
        val entry = HistoryEntry(
            firstOperand = engine.format(first),
            operation = operation,
            secondOperand = engine.format(second),
            result = engine.format(result)
        )
        _uiState.update { it.copy(history = it.history + entry) }
    }

    /** Deja la calculadora en estado de error, conservando el historial. */
    private fun failWith(error: CalculatorError) {
        resetCalculation()
        lastOperation = null
        lastOperand = null
        _uiState.update { it.copy(error = error) }
    }

    /** Reinicia el cálculo en curso sin tocar el historial. */
    private fun resetCalculation() {
        currentInput = INITIAL_INPUT
        startNewInput = true
        accumulator = null
        pendingOperation = null
        _uiState.update { it.copy(error = null) }
    }

    private fun hasError(): Boolean = _uiState.value.error != null

    /** Solo se borra mientras el usuario escribe un número distinto de 0. */
    private fun canDeleteDigit(): Boolean =
        !hasError() && !startNewInput && currentInput != INITIAL_INPUT

    private fun digitCount(input: String): Int = input.count { it.isDigit() }

    /** Sincroniza el estado interno con el estado observable que consume la UI. */
    private fun publishState() {
        val operation = pendingOperation
        _uiState.update { state ->
            state.copy(
                displayValue = currentInput,
                pendingOperand = if (operation != null) accumulator?.let(engine::format) else null,
                pendingOperation = operation,
                canDeleteDigit = canDeleteDigit()
            )
        }
    }

    private companion object {
        const val INITIAL_INPUT = "0"
        const val NEGATIVE_SIGN = "-"
        const val NEGATIVE_ZERO = "-0"
        const val DECIMAL_SEPARATOR = "."
        const val MAX_DIGITS = 12
    }
}
