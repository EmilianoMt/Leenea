package com.example.proyecto_turnos_c.ui.screens.notifications

import NavBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_turnos_c.ui.components.Notification

// Definición de la estructura de datos para una notificación
data class NotificationData(
    val title: String,
    val message: String,
    val timestamp: String,
    val isSuccess: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificatonsScreen(navController: NavController) {
    // Lista de ejemplo de notificaciones.
    val notificationsList = listOf(
        NotificationData("Actualización", "Estás a 5 turnos de pasar.", "hace 9 min.", false),
        NotificationData("Nuevo registro", "Te registraste con éxito a (Nombre del evento).", "hace 15 min.", true),
        NotificationData("Recordatorio", "No olvides asistir al evento.", "hace 30 min.", false),
        NotificationData("Confirmación", "Tu registro ha sido confirmado.", "hace 1 hora.", true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(106.dp),
                title = {
                    // Centramos el título con un Box
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Historial de notificaciones",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    // Al presionar el icono se navega a "login"
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            // Se pasa el navController al NavBar para que la navegación funcione de forma global
            NavBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notificationsList) { notification ->
                // Se invoca el componente Notification para cada elemento
                Notification(
                    title = notification.title,
                    message = notification.message,
                    timestamp = notification.timestamp,
                    isSuccess = notification.isSuccess
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotPreview() {
    NotificatonsScreen(navController = rememberNavController())
}
