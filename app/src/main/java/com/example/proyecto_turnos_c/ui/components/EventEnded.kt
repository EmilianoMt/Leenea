package com.example.proyecto_turnos_c.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EventAnnouncementCard(
    imageRes: Int,
    fechaHora: String,
    ubicacion: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Imagen del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Sección de información con filas divididas
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                InfoRow(label = "Fecha y Hora:", info = fechaHora)
                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(label = "Ubicación:", info = ubicacion)
                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(label = "Descripción:", info = descripcion)
            }
            Spacer(modifier = Modifier.height(46.dp))
            // Sección inferior: ícono y mensaje
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Campaign,
                    contentDescription = "Evento finalizado",
                    tint = Color(0xFF191C88),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Este evento ha finalizado y actualmente no puedes entrar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF191C88),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, info: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna de la categoría
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        // Columna de la información
        Text(
            text = info,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EventAnnouncementCardPreview() {
    EventAnnouncementCard(
        imageRes = com.example.proyecto_turnos_c.R.drawable.event1,
        fechaHora = "17/Marzo/2025, 13:00-18:00",
        ubicacion = "Centro de desarrollo",
        descripcion = "Inscripción al Hackathon Troyano 2025-I",
    )
}
