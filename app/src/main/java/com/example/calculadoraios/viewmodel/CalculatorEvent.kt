package com.example.calculadoraios.viewmodel

import com.example.calculadoraios.model.Operation

/**
 * Eventos que la interfaz envía al ViewModel.
 * La UI nunca decide qué hacer: solo informa qué tecla se pulsó.
 */
sealed interface CalculatorEvent {  // Eventos que envían los botones al ViewModel
    data class Digit(val value: Int) : CalculatorEvent  // Teclas 0 a 9
    data class Operator(val operation: Operation) : CalculatorEvent  // Teclas + − × ÷
    data object Decimal : CalculatorEvent  // Tecla .
    data object Equals : CalculatorEvent  // Tecla =
    data object Clear : CalculatorEvent  // Tecla AC
    data object ToggleSign : CalculatorEvent  // Tecla +/−
    data object Percent : CalculatorEvent  // Tecla %
    data object DeleteDigit : CalculatorEvent  // Tecla ⌫
    data object ClearHistory : CalculatorEvent  // Papelera del historial
}
