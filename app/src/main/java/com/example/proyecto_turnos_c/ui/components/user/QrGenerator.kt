package com.example.proyecto_turnos_c.ui.components.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto_turnos_c.viewmodels.InfoTurn
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun QrGenerator(turnoActual: String, tuTurno: String, data: InfoTurn) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Create a formatted string with all the user data for the QR
        val qrContent = buildString {
            append("ID: ${data.id_user}\n")
            append("Nombre: ${data.fullName}\n")
            append("Expediente: ${data.expediente}\n")
            append("Turno: ${data.turnoNumero}\n")
            append("Fecha: ${data.fechaRegistro}\n")
            append("Evento: ${data.tituloEvento}")
        }

        val qrPainter = rememberQrCodePainter(qrContent)

        Image(
            painter = qrPainter,
            contentDescription = "Código QR del turno",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Sección de los turnos
        Column(modifier = Modifier) {
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
}