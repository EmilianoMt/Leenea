package com.example.proyecto_turnos_c.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EventDetailCard(
    buildingImageRes: Int,
    fechaHora: String,
    ubicacion: String,
    descripcion: String,
    turnoActual: String,
    tuTurno: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(700.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen superior
            Image(
                painter = painterResource(id = buildingImageRes),
                contentDescription = "Imagen del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(26.dp))
            // Sección de información en dos columnas
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                InfoRow(label = "Fecha y Hora:", info = fechaHora)
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                InfoRow(label = "Ubicación:", info = ubicacion)
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                InfoRow(label = "Descripción:", info = descripcion)
            }
            Spacer(modifier = Modifier.height(44.dp))
            // Sección inferior: dos columnas (QR a la izquierda, turnos a la derecha)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna izquierda: icono QR
                Icon(
                    imageVector = Icons.Outlined.QrCode,
                    contentDescription = "Código QR",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Columna derecha: turnos
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TURNO ACTUAL:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = turnoActual,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "TU TURNO:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = tuTurno,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EventDetailCardPreview() {
    EventDetailCard(
        buildingImageRes = com.example.proyecto_turnos_c.R.drawable.event1, // Reemplaza con tu recurso de imagen
        fechaHora = "14/Marzo/2025\n13:00-18:00",
        ubicacion = "Sala de Usos Múltiples",
        descripcion = "Alta de materias para alumnos de 2° semestre en adelante",
        turnoActual = "022",
        tuTurno = "045"
    )
}
