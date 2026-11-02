# Calculadora iOS — Android nativo (Kotlin + Jetpack Compose)

Proyecto universitario: calculadora inspirada en la interfaz de iOS, construida
exclusivamente con Jetpack Compose y arquitectura MVVM.

## Funcionalidades
- Dígitos 0–9, punto decimal, `+`, `−`, `×`, `÷`, `%`, `+/−`, `AC` y `=`.
- Tecla `⌫` estilo iOS: mientras se escribe un número, `AC` pasa a `⌫` y borra el último dígito; al quedar en 0 o tras un resultado vuelve a ser `AC`.
- Operaciones encadenadas (se resuelven de izquierda a derecha) y `=` repetido.
- División por cero y entradas inválidas se informan en pantalla sin cerrar la app.
- Historial de operaciones (`5 + 3 = 8`) con estado vacío y navegación de ida y vuelta.
- Botón para borrar el historial (se desactiva cuando está vacío); no afecta el cálculo en curso.

## Arquitectura
```
com.example.calculadoraios
├── model/        Lógica de cálculo pura (CalculatorEngine, Operation, CalculatorError, HistoryEntry)
├── viewmodel/    Estado observable (StateFlow) y eventos (CalculatorViewModel, CalculatorUiState, CalculatorEvent)
├── ui/
│   ├── keypad/      Definición declarativa del teclado
│   ├── components/  Composables reutilizables (botón, pantalla, ítem de historial)
│   ├── screens/     CalculatorScreen y HistoryScreen
│   └── theme/       Tema de Material 3
└── navigation/   NavHost con el ViewModel compartido entre pantallas
```
- Los Composables solo representan el estado y envían eventos; no contienen reglas de cálculo.
- El ViewModel expone estados semánticos (p. ej. `CalculatorError.DIVISION_BY_ZERO`) y la UI los traduce con `stringResource` / `colorResource`.
- Textos en `res/values/strings.xml` y colores en `res/values/colors.xml`.

## Pruebas
Pruebas unitarias del motor y del ViewModel en `app/src/test`:
```
./gradlew testDebugUnitTest
```
