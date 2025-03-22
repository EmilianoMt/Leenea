package com.example.proyecto_turnos_c.ui.screens

import NavBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto_turnos_c.R
import com.example.proyecto_turnos_c.ui.components.CircularImageCard
import com.example.proyecto_turnos_c.ui.components.EventCard


@Preview
@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {


            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier
                    .size(width = 400.dp, height = 600.dp)
            ) {
                EventCard(
                    title = "Sala de usos múltiples",
                    subtitle = "Alta de materias 2025-1",
                    iconButtonAction = { /* Acción personalizada al presionar el botón */ }
                )

                Column {
                    CircularImageCard(
                        imageRes = R.drawable.event1,
                        title = "Woman IT Registro",
                        description = "SOMOS UAQ",
                        isAvailable = true
                    )
                    CircularImageCard(
                        imageRes = R.drawable.event1,
                        title = "Registro materias 2025-1",
                        description = "Detalles de las materias",
                        isAvailable = false
                    )
                    CircularImageCard(
                        imageRes = R.drawable.event1,
                        title = "Registro recurso materias",
                        description = "Materias 2025-1",
                        isAvailable = false
                    )
                }
                Text(
                    text = "Contenido de la tarjeta",
                    color = Color.Black
                )
            }
        }

        NavBar(
            modifier = Modifier
            .align(Alignment.BottomCenter)
        )
    }
}

