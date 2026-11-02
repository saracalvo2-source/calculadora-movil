package com.example.calculadoraios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calculadoraios.navigation.AppNavigation
import com.example.calculadoraios.ui.theme.CalculadoraIOsTheme

/** Punto de entrada: solo monta el tema y el grafo de navegación. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraIOsTheme {
                AppNavigation()
            }
        }
    }
}
