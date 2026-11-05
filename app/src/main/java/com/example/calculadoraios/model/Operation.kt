package com.example.calculadoraios.model

/**
 * Operaciones binarias que soporta la calculadora.
 * El símbolo visible se resuelve en la capa de UI mediante recursos de texto.
 */
enum class Operation {  // Las 4 operaciones. El símbolo lo pone OperationSymbol.kt desde strings.xml
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE
}
