package com.example.calculadoraios.viewmodel

import com.example.calculadoraios.model.CalculatorError
import com.example.calculadoraios.model.Operation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Pruebas del comportamiento de la calculadora según el flujo de teclas. */
class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel()
    }

    private fun press(vararg events: CalculatorEvent) = events.forEach(viewModel::onEvent)
    private fun digit(value: Int) = CalculatorEvent.Digit(value)
    private fun operator(operation: Operation) = CalculatorEvent.Operator(operation)
    private val display get() = viewModel.uiState.value.displayValue

    @Test
    fun `estado inicial muestra cero y sin historial`() {
        assertEquals("0", display)
        assertTrue(viewModel.uiState.value.history.isEmpty())
    }

    @Test
    fun `escribe digitos sin ceros a la izquierda`() {
        press(digit(0), digit(0), digit(7), digit(2))
        assertEquals("72", display)
    }

    @Test
    fun `suma basica actualiza display e historial`() {
        press(digit(5), operator(Operation.ADD), digit(3), CalculatorEvent.Equals)
        assertEquals("8", display)
        val entry = viewModel.uiState.value.history.single()
        assertEquals("5", entry.firstOperand)
        assertEquals(Operation.ADD, entry.operation)
        assertEquals("3", entry.secondOperand)
        assertEquals("8", entry.result)
    }

    @Test
    fun `operaciones encadenadas se resuelven de izquierda a derecha`() {
        press(digit(2), operator(Operation.ADD), digit(3), operator(Operation.MULTIPLY))
        assertEquals("5", display)
        press(digit(4), CalculatorEvent.Equals)
        assertEquals("20", display)
    }

    @Test
    fun `cambiar de operador no calcula nada`() {
        press(digit(9), operator(Operation.ADD), operator(Operation.SUBTRACT), digit(4), CalculatorEvent.Equals)
        assertEquals("5", display)
        assertEquals(Operation.SUBTRACT, viewModel.uiState.value.history.single().operation)
    }

    @Test
    fun `igual repetido vuelve a aplicar la ultima operacion`() {
        press(digit(2), operator(Operation.MULTIPLY), digit(3), CalculatorEvent.Equals)
        assertEquals("6", display)
        press(CalculatorEvent.Equals)
        assertEquals("18", display)
        assertEquals(2, viewModel.uiState.value.history.size)
    }

    @Test
    fun `decimales se procesan con punto y sin duplicarlo`() {
        press(digit(1), CalculatorEvent.Decimal, CalculatorEvent.Decimal, digit(5))
        assertEquals("1.5", display)
        press(operator(Operation.ADD), CalculatorEvent.Decimal, digit(5), CalculatorEvent.Equals)
        assertEquals("2", display)
    }

    @Test
    fun `division por cero muestra error sin cerrar y se limpia al escribir`() {
        press(digit(8), operator(Operation.DIVIDE), digit(0), CalculatorEvent.Equals)
        assertEquals(CalculatorError.DIVISION_BY_ZERO, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.history.isEmpty())
        press(digit(4))
        assertNull(viewModel.uiState.value.error)
        assertEquals("4", display)
    }

    @Test
    fun `AC limpia el calculo pero conserva el historial`() {
        press(digit(5), operator(Operation.ADD), digit(3), CalculatorEvent.Equals, CalculatorEvent.Clear)
        assertEquals("0", display)
        assertNull(viewModel.uiState.value.pendingOperation)
        assertEquals(1, viewModel.uiState.value.history.size)
    }

    @Test
    fun `cambio de signo y porcentaje`() {
        press(digit(5), digit(0), CalculatorEvent.ToggleSign)
        assertEquals("-50", display)
        press(CalculatorEvent.ToggleSign, CalculatorEvent.Percent)
        assertEquals("0.5", display)
    }

    @Test
    fun `operador pendiente se expone para resaltar la tecla`() {
        press(digit(7), operator(Operation.DIVIDE))
        assertEquals(Operation.DIVIDE, viewModel.uiState.value.pendingOperation)
        assertEquals("7", viewModel.uiState.value.pendingOperand)
    }

    @Test
    fun `borrar digito quita el ultimo caracter y vuelve a cero`() {
        press(digit(1), digit(2), CalculatorEvent.Decimal, digit(5))
        assertTrue(viewModel.uiState.value.canDeleteDigit)
        press(CalculatorEvent.DeleteDigit)
        assertEquals("12.", display)
        press(CalculatorEvent.DeleteDigit, CalculatorEvent.DeleteDigit, CalculatorEvent.DeleteDigit)
        assertEquals("0", display)
        assertFalse(viewModel.uiState.value.canDeleteDigit)
    }

    @Test
    fun `borrar digito en negativo no deja el signo solo`() {
        press(digit(7), CalculatorEvent.ToggleSign, CalculatorEvent.DeleteDigit)
        assertEquals("0", display)
    }

    @Test
    fun `borrar digito no modifica un resultado`() {
        press(digit(5), operator(Operation.ADD), digit(3), CalculatorEvent.Equals)
        assertFalse(viewModel.uiState.value.canDeleteDigit)
        press(CalculatorEvent.DeleteDigit)
        assertEquals("8", display)
    }

    @Test
    fun `borrar digito corrige el segundo operando`() {
        press(digit(9), operator(Operation.SUBTRACT), digit(4), digit(5), CalculatorEvent.DeleteDigit, CalculatorEvent.Equals)
        assertEquals("5", display)
    }

    @Test
    fun `borrar historial vacia la lista y conserva el calculo`() {
        press(digit(5), operator(Operation.ADD), digit(3), CalculatorEvent.Equals)
        press(CalculatorEvent.ClearHistory)
        assertTrue(viewModel.uiState.value.history.isEmpty())
        assertEquals("8", display)
    }
}
