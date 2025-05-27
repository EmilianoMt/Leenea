package com.example.proyecto_turnos_c.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto_turnos_c.viewmodels.UserProfile

@Composable
fun QRScanResultDialog(
    scanResult: String,
    userInfo: UserProfile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resultado del Escaneo", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Usuario escaneado:", fontWeight = FontWeight.Bold)
                Text(userInfo.fullName)

                if (userInfo.expediente.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Expediente:", fontWeight = FontWeight.Bold)
                    Text(userInfo.expediente)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Email:", fontWeight = FontWeight.Bold)
                Text(userInfo.email)

                Spacer(modifier = Modifier.height(8.dp))
                Text("ID de usuario:", fontWeight = FontWeight.Bold)
                Text(scanResult)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}
