package com.example.proyecto_turnos_c.ui.components.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto_turnos_c.viewmodels.NextTurnInfo

@Composable
fun HandleTurns(
    turnoActual: String,
    nextTurnInfo: NextTurnInfo,
    onScanClick: () -> Unit,
    onAdvanceTurn: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onScanClick() },
                modifier = Modifier
                    .height(46.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
            ) {
                Text("Escanea")
                Icon(
                    imageVector = Icons.Outlined.SubdirectoryArrowRight,
                    contentDescription = "Icono de ingreso",
                    tint = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onAdvanceTurn() },
                modifier = Modifier
                    .height(46.dp)
                    .width(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            ) {
                Text("Siguiente Turno")
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

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
                text = "SIGUIENTE TURNO:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = nextTurnInfo.turnNumber,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = nextTurnInfo.userName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                    )
                    if (nextTurnInfo.expediente.isNotEmpty()) {
                        Text(
                            text = "Exp: ${nextTurnInfo.expediente}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                        )
                    }
                }
            }
        }
    }
}