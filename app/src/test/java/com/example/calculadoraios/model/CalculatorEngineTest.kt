package com.example.calculadoraios.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Pruebas de la lógica aritmética y de formato, independientes de Android. */
class CalculatorEngineTest {

    private val engine = CalculatorEngine()

    @Test
    fun `suma de enteros`() {
        assertEquals(CalculationResult.Success(8.0), engine.calculate(5.0, 3.0, Operation.ADD))
    }

    @Test
    fun `resta puede dar negativo`() {
        assertEquals(CalculationResult.Success(-2.0), engine.calculate(3.0, 5.0, Operation.SUBTRACT))
    }

    @Test
    fun `multiplicacion de decimales`() {
        assertEquals(CalculationResult.Success(0.25), engine.calculate(0.5, 0.5, Operation.MULTIPLY))
    }

    @Test
    fun `division normal`() {
        assertEquals(CalculationResult.Success(2.5), engine.calculate(5.0, 2.0, Operation.DIVIDE))
    }

    @Test
    fun `division por cero devuelve error controlado`() {
        assertEquals(
            CalculationResult.Failure(CalculatorError.DIVISION_BY_ZERO),
            engine.calculate(5.0, 0.0, Operation.DIVIDE)
        )
    }

    @Test
    fun `porcentaje divide entre cien`() {
        assertEquals(0.5, engine.percent(50.0), 0.0)
    }

    @Test
    fun `cambio de signo`() {
        assertEquals(-7.0, engine.negate(7.0), 0.0)
    }

    @Test
    fun `formato elimina decimales innecesarios`() {
        assertEquals("8", engine.format(8.0))
        assertEquals("2.5", engine.format(2.5))
        assertEquals("0", engine.format(-0.0))
    }

    @Test
    fun `formato evita errores de coma flotante`() {
        assertEquals("0.3", engine.format(0.1 + 0.2))
    }

    @Test
    fun `parse rechaza texto invalido`() {
        assertEquals(null, engine.parse("abc"))
        assertEquals(12.5, engine.parse("12.5"))
    }
}
