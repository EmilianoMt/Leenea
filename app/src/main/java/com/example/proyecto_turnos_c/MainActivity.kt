// MainActivity.kt
package com.example.proyecto_turnos_c

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.proyecto_turnos_c.ui.navigation.NavigationGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationGraph() // Se inicia con la pantalla de login
        }
    }
}
