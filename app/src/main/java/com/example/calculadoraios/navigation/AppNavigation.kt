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
object AppRoutes {
    const val CALCULATOR = "calculator"
    const val HISTORY = "history"
}

/**
 * Grafo de navegación. El ViewModel se crea aquí, por encima del NavHost,
 * para que ambas pantallas compartan el mismo estado (incluido el historial).
 */
@Composable
fun AppNavigation(viewModel: CalculatorViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.CALCULATOR
    ) {
        composable(AppRoutes.CALCULATOR) {
            CalculatorScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onOpenHistory = { navController.navigate(AppRoutes.HISTORY) }
            )
        }
        composable(AppRoutes.HISTORY) {
            HistoryScreen(
                history = uiState.history,
                onBack = { navController.popBackStack() },
                onClearHistory = { viewModel.onEvent(CalculatorEvent.ClearHistory) }
            )
        }
    }
}
