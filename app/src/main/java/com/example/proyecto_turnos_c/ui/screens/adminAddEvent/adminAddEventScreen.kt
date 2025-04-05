package com.example.proyecto_turnos_c.ui.screens.adminAddEvent

import NavBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(navController: NavController) {
    val eventList = listOf(
        "Evento Destacado 1",
        "Evento Destacado 2",
        "Evento Destacado 3"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(84.dp),
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { /* Acción para el menú */ },
                        modifier = Modifier.padding(start = 8.dp) // ← Aquí separas del borde
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Menú",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
        ,
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 35.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título fuera del TopAppBar
            item {
                Text(
                    text = "Eventos disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Tarjetas normales
            items(eventList) { title ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = MaterialTheme.shapes.medium, // Sin sombra
                    border = BorderStroke(1.dp, Color(0xFF0D47A1)) // Borde azul oscuro
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 25.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold // (opcional) para negritas también
                        )
                        Icon(
                            imageVector = Icons.Outlined.ArrowForward,
                            contentDescription = "Ver más",
                            tint = Color.Gra
                        )y
                    }
                }
            }

            // Tarjeta punteada con ícono "+"
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .drawBehind {
                            val strokeWidth = 3f
                            val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawRoundRect(
                                color = Color.Gray,
                                style = Stroke(width = strokeWidth, pathEffect = dash),
                                cornerRadius = CornerRadius(16f, 16f)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Agregar nuevo evento",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyEventsScreenPreview() {
    MyEventsScreen(navController = rememberNavController())
}
