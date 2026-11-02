package com.example.calculadoraios.viewmodel

import com.example.calculadoraios.model.Operation

/**
 * Eventos que la interfaz envía al ViewModel.
 * La UI nunca decide qué hacer: solo informa qué tecla se pulsó.
 */
sealed interface CalculatorEvent {
    data class Digit(val value: Int) : CalculatorEvent
    data class Operator(val operation: Operation) : CalculatorEvent
    data object Decimal : CalculatorEvent
    data object Equals : CalculatorEvent
    data object Clear : CalculatorEvent
    data object ToggleSign : CalculatorEvent
    data object Percent : CalculatorEvent
}
