package com.example.proyecto_turnos_c.ui.components.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.proyecto_turnos_c.R
import com.example.proyecto_turnos_c.ui.components.dialogs.WarningDialog
import com.example.proyecto_turnos_c.viewmodels.EventDetailCardViewModel
import com.example.proyecto_turnos_c.viewmodels.EventDetailCardViewModelFactory
import com.example.proyecto_turnos_c.viewmodels.InfoTurn
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun EventDetailCard(
    eventId: String,
    eventTitle: String,
    imageUrl: String? = null,
    buildingImageRes: Int? = null,
    fechaHora: String,
    ubicacion: String,
    descripcion: String,
    turnoActual: String,
    tuTurno: String,
    viewModel: EventDetailCardViewModel = viewModel(
        factory = EventDetailCardViewModelFactory(eventId)
    )
) {
    // Obtener los estados del ViewModel
    val isInQueue by viewModel.isInQueue.collectAsState()
    val userTurn by viewModel.userTurn.collectAsState()
    val currentTurn by viewModel.currentTurn.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val displayCurrentTurn = if (currentTurn.isNotEmpty()) currentTurn else turnoActual
    val displayUserTurn = if (userTurn.isNotEmpty()) userTurn else tuTurno

    val painter = when {
        imageUrl != null && imageUrl.isNotEmpty() -> {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build()
            )
        }
        buildingImageRes != null -> {
            painterResource(id = buildingImageRes)
        }
        else -> {
            painterResource(id = R.drawable.event1)
        }
    }

    if (showDialog) {
        WarningDialog(
            onDismissRequest = { showDialog = false },
            onAccept = {
                viewModel.joinQueue()
                showDialog = false
            },
            onCancel = { showDialog = false }
        )
    }

    // Mostrar mensaje de error si existe
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // Mostrar el error por un tiempo y luego reiniciar
            viewModel.resetError()
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Imagen superior
                Image(
                    painter = painter,
                    contentDescription = "Imagen del evento",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Sección de información en dos columnas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    InfoRow(
                        label = "Fecha y Hora:",
                        info = fechaHora
                    )

                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    InfoRow(
                        label = "Ubicación:",
                        info = ubicacion
                    )

                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    InfoRow(
                        label = "Descripción:",
                        info = descripcion
                    )
                }

                Spacer(modifier = Modifier.height(44.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (isInQueue) {
                        // Format current date to string
                        val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date())

                        val userData = InfoTurn(
                            id_user = viewModel.userProfile?.id ?: "",
                            fullName = viewModel.userProfile?.fullName ?: "",
                            expediente = viewModel.userProfile?.expediente ?: "",
                            turnoNumero = displayUserTurn,
                            fechaRegistro = formattedDate,
                            tituloEvento = eventTitle
                        )

                        QrGenerator(displayCurrentTurn, displayUserTurn, userData)
                    } else {
                        IngresarBtn { showDialog = true }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}

@Composable
fun IngresarBtn(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .height(46.dp)
                .width(250.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
        ) {
            Text("Entra a la fila")
            Icon(
                imageVector = Icons.Outlined.SubdirectoryArrowRight,
                contentDescription = "Icono de ingreso",
                tint = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


