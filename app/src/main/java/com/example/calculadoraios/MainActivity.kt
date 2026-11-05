package com.example.calculadoraios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calculadoraios.navigation.AppNavigation
import com.example.calculadoraios.ui.theme.CalculadoraIOsTheme

/** Punto de entrada: solo monta el tema y el grafo de navegación. */
class MainActivity : ComponentActivity() {  // Punto de entrada de la app (declarada en AndroidManifest.xml)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Usa toda la pantalla
        setContent {  // Desde aquí la interfaz se dibuja con Jetpack Compose
            CalculadoraIOsTheme {  // Tema de la app (ui/theme/Theme.kt)
                AppNavigation()  // Navegación entre pantallas (navigation/AppNavigation.kt)
            }
        }
    }
}
