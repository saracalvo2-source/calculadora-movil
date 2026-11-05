package com.example.calculadoraios.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculadoraios.ui.screens.CalculatorScreen
import com.example.calculadoraios.ui.screens.HistoryScreen
import com.example.calculadoraios.viewmodel.CalculatorEvent
import com.example.calculadoraios.viewmodel.CalculatorViewModel

/** Rutas de navegación de la aplicación. */
object AppRoutes {  // Nombres de las dos pantallas
    const val CALCULATOR = "calculator"
    const val HISTORY = "history"
}

/**
 * Grafo de navegación. El ViewModel se crea aquí, por encima del NavHost,
 * para que ambas pantallas compartan el mismo estado (incluido el historial).
 */
@Composable
fun AppNavigation(viewModel: CalculatorViewModel = viewModel()) {  // Un solo ViewModel compartido por las dos pantallas
    val navController = rememberNavController()  // Controla el cambio de pantalla
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()  // Observa el estado del ViewModel, la UI se redibuja cuando cambia

    NavHost(  // Contenedor de pantallas, arranca en la calculadora
        navController = navController,
        startDestination = AppRoutes.CALCULATOR
    ) {
        composable(AppRoutes.CALCULATOR) {  // Pantalla de la calculadora
            CalculatorScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,  // Los botones envían sus eventos a onEvent del ViewModel
                onOpenHistory = { navController.navigate(AppRoutes.HISTORY) }  // Ir al historial
            )
        }
        composable(AppRoutes.HISTORY) {  // Pantalla del historial
            HistoryScreen(
                history = uiState.history,
                onBack = { navController.popBackStack() },  // Volver a la calculadora
                onClearHistory = { viewModel.onEvent(CalculatorEvent.ClearHistory) }  // Borrar historial
            )
        }
    }
}
