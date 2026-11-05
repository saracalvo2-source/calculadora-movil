package com.example.calculadoraios.viewmodel

import androidx.lifecycle.ViewModel  // Clase de Android que conserva el estado al girar el celular
import com.example.calculadoraios.model.CalculationResult
import com.example.calculadoraios.model.CalculatorEngine   // Motor de cálculo (model)
import com.example.calculadoraios.model.CalculatorError
import com.example.calculadoraios.model.HistoryEntry
import com.example.calculadoraios.model.Operation
import kotlinx.coroutines.flow.MutableStateFlow  // Herramientas del estado observable (StateFlow)
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
class CalculatorViewModel(  // ViewModel de la calculadora (ViewModel de MVVM)
    private val engine: CalculatorEngine = CalculatorEngine()  // Motor que hace las cuentas (model/CalculatorEngine.kt)
) : ViewModel() {  // Hereda de ViewModel de Android

    private val _uiState = MutableStateFlow(CalculatorUiState())  // Estado privado y modificable, solo lo cambia el ViewModel
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()  // Estado público de solo lectura, lo observa AppNavigation.kt

    // Estado interno de edición
    private var currentInput = INITIAL_INPUT  // Texto del número que se está escribiendo
    private var startNewInput = true  // true = el próximo dígito empieza un número nuevo
    private var accumulator: Double? = null  // Primer número o resultado acumulado
    private var pendingOperation: Operation? = null  // Operación elegida que espera el segundo número

    // Memoria para repetir la última operación con "="
    private var lastOperation: Operation? = null  // Última operación, para el = repetido
    private var lastOperand: Double? = null  // Último segundo número, para el = repetido

    /** Punto único de entrada de eventos desde la interfaz. */
    fun onEvent(event: CalculatorEvent) {  // Aquí llegan todos los eventos de los botones
        when (event) {  // Manda cada evento a la función que lo atiende
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
        publishState()  // Después de cada evento actualiza el estado que ve la UI
    }

    private fun onDigit(digit: Int) {  // Teclas 0 a 9
        if (hasError()) resetCalculation()
        currentInput = when {  // Decide cómo queda el número escrito
            startNewInput -> digit.toString()
            currentInput == INITIAL_INPUT -> digit.toString()  // Evita ceros a la izquierda
            currentInput == NEGATIVE_ZERO -> "-$digit"
            digitCount(currentInput) >= MAX_DIGITS -> currentInput  // Límite de 12 dígitos
            else -> currentInput + digit  // Pega el dígito al final
        }
        startNewInput = false
    }

    private fun onDecimal() {  // Tecla .
        if (hasError()) resetCalculation()
        when {
            startNewInput -> currentInput = INITIAL_INPUT + DECIMAL_SEPARATOR
            !currentInput.contains(DECIMAL_SEPARATOR) -> currentInput += DECIMAL_SEPARATOR  // Solo agrega el punto si no hay uno
        }
        startNewInput = false
    }

    private fun onOperator(operation: Operation) {  // Teclas + − × ÷
        if (hasError()) return
        val current = engine.parse(currentInput) ?: return failWith(CalculatorError.INVALID_INPUT)  // Convierte el texto en número con el motor
        when {
            // Primer operando de una nueva expresión
            pendingOperation == null -> accumulator = current  // Guarda el primer número
            // Operación encadenada: se resuelve la anterior antes de continuar
            !startNewInput -> if (!resolvePending(current)) return  // Resuelve la operación anterior (izquierda a derecha)
            // Solo se cambia el operador pendiente
        }
        pendingOperation = operation  // Guarda la operación (si se tocan dos seguidas queda la última)
        startNewInput = true
    }

    private fun onEquals() {  // Tecla =
        if (hasError()) return
        val current = engine.parse(currentInput) ?: return failWith(CalculatorError.INVALID_INPUT)
        val previousOperand = lastOperand
        val previousOperation = lastOperation
        when {
            pendingOperation != null && accumulator != null -> {  // = normal: resuelve la operación pendiente
                resolvePending(current)
                pendingOperation = null
            }
            previousOperation != null && previousOperand != null -> {  // = repetido: repite la última operación
                // "=" repetido: aplica de nuevo la última operación al valor actual
                accumulator = current
                applyOperation(current, previousOperand, previousOperation)
            }
        }
        startNewInput = true
    }

    private fun onClear() {  // Tecla AC: limpia el cálculo pero no el historial
        resetCalculation()
        lastOperation = null
        lastOperand = null
    }

    private fun onToggleSign() {  // Tecla +/−
        if (hasError()) return
        if (currentInput == INITIAL_INPUT) return
        currentInput = if (currentInput.startsWith(NEGATIVE_SIGN)) {  // Si es negativo le quita el menos, si no se lo pone
            currentInput.removePrefix(NEGATIVE_SIGN)
        } else {
            NEGATIVE_SIGN + currentInput
        }
    }

    private fun onPercent() {  // Tecla %
        if (hasError()) return
        val value = engine.parse(currentInput) ?: return failWith(CalculatorError.INVALID_INPUT)
        currentInput = engine.format(engine.percent(value))  // Divide entre 100 con el motor y lo formatea
        startNewInput = true
    }

    /** Borra el último dígito escrito; si no queda ninguno, el valor vuelve a 0. */
    private fun onDeleteDigit() {  // Tecla ⌫
        if (!canDeleteDigit()) return
        val remaining = currentInput.dropLast(1)  // Quita el último carácter
        currentInput = if (remaining.isEmpty() || remaining == NEGATIVE_SIGN) INITIAL_INPUT else remaining
    }

    private fun onClearHistory() {  // Papelera del historial
        _uiState.update { it.copy(history = emptyList()) }  // Vacía el historial sin tocar el cálculo
    }

    /**
     * Resuelve la operación pendiente con [second] como segundo operando.
     * @return true si el cálculo fue válido, false si produjo un error.
     */
    private fun resolvePending(second: Double): Boolean {  // Resuelve la operación pendiente, true si salió bien
        val first = accumulator ?: return false
        val operation = pendingOperation ?: return false
        return applyOperation(first, second, operation)
    }

    /** Ejecuta la operación, registra el historial y deja el resultado como valor actual. */
    private fun applyOperation(first: Double, second: Double, operation: Operation): Boolean {  // Llama al motor, guarda en el historial y muestra el resultado
        return when (val result = engine.calculate(first, second, operation)) {  // Aquí se llama al motor para hacer la cuenta
            is CalculationResult.Success -> {  // Cuenta válida
                recordHistory(first, operation, second, result.value)  // Guarda la operación en el historial
                accumulator = result.value
                currentInput = engine.format(result.value)
                lastOperation = operation
                lastOperand = second
                true
            }
            is CalculationResult.Failure -> {  // Error, por ejemplo división por cero
                failWith(result.error)  // Muestra el error sin cerrar la app
                false
            }
        }
    }

    private fun recordHistory(first: Double, operation: Operation, second: Double, result: Double) {  // Crea el registro del historial (model/HistoryEntry.kt)
        val entry = HistoryEntry(
            firstOperand = engine.format(first),
            operation = operation,
            secondOperand = engine.format(second),
            result = engine.format(result)
        )
        _uiState.update { it.copy(history = it.history + entry) }  // Lo agrega al final, así se conserva el orden
    }

    /** Deja la calculadora en estado de error, conservando el historial. */
    private fun failWith(error: CalculatorError) {  // Pone la calculadora en estado de error
        resetCalculation()
        lastOperation = null
        lastOperand = null
        _uiState.update { it.copy(error = error) }  // CalculatorDisplay.kt muestra el mensaje en rojo
    }

    /** Reinicia el cálculo en curso sin tocar el historial. */
    private fun resetCalculation() {  // Reinicia el cálculo sin borrar el historial
        currentInput = INITIAL_INPUT
        startNewInput = true
        accumulator = null
        pendingOperation = null
        _uiState.update { it.copy(error = null) }
    }

    private fun hasError(): Boolean = _uiState.value.error != null  // true si hay un error en pantalla

    /** Solo se borra mientras el usuario escribe un número distinto de 0. */
    private fun canDeleteDigit(): Boolean =  // Decide si la tecla AC se muestra como ⌫
        !hasError() && !startNewInput && currentInput != INITIAL_INPUT

    private fun digitCount(input: String): Int = input.count { it.isDigit() }

    /** Sincroniza el estado interno con el estado observable que consume la UI. */
    private fun publishState() {  // Copia los datos internos al estado que observa la UI
        val operation = pendingOperation
        _uiState.update { state ->
            state.copy(
                displayValue = currentInput,  // Número grande de la pantalla
                pendingOperand = if (operation != null) accumulator?.let(engine::format) else null,  // Número pequeño de arriba, ej: 5 +
                pendingOperation = operation,
                canDeleteDigit = canDeleteDigit()  // AC o ⌫
            )
        }
    }

    private companion object {  // Valores fijos del ViewModel
        const val INITIAL_INPUT = "0"
        const val NEGATIVE_SIGN = "-"
        const val NEGATIVE_ZERO = "-0"
        const val DECIMAL_SEPARATOR = "."
        const val MAX_DIGITS = 12
    }
}
