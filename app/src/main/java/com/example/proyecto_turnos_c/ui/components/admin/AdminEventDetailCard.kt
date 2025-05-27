package com.example.proyecto_turnos_c.ui.components.admin

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.proyecto_turnos_c.R
import com.example.proyecto_turnos_c.ui.components.dialogs.QRScanResultDialog
import com.example.proyecto_turnos_c.ui.components.user.InfoRow
import com.example.proyecto_turnos_c.viewmodels.AdminEventDetailCardViewModel
import com.example.proyecto_turnos_c.viewmodels.AdminEventDetailViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AdminEventDetailCard(
    eventId: String,
    imageUrl: String? = null,
    buildingImageRes: Int? = null,
    fechaHora: String,
    ubicacion: String,
    descripcion: String,
    turnoActual: String,
    viewModel: AdminEventDetailCardViewModel = viewModel(
        factory = AdminEventDetailViewModelFactory(eventId)
    )
) {
    val currentTurn by viewModel.currentTurn.collectAsState()
    val nextTurnInfo by viewModel.nextTurnInfo.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val scannedUserInfo by viewModel.scannedUserInfo.collectAsState()
    val isEventAvailable by viewModel.isEventAvailable.collectAsState()

    // Estado para mostrar el escáner QR
    var showScanner by remember { mutableStateOf(false) }

    // Estado para confirmar la finalización del evento
    var showEndEventConfirmation by remember { mutableStateOf(false) }

    // Estado para los permisos de la cámara
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Manejador del resultado de la solicitud de permisos
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setCameraPermissionGranted(isGranted)
        if (isGranted) {
            showScanner = true
        }
    }

    // Para mostrar mensaje de error
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            Log.d("AdminEventDetailCard", "Error: $errorMessage")
        }
    }

    val displayCurrentTurn = if (currentTurn.isNotEmpty()) currentTurn else turnoActual

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
                    HandleTurns(
                        turnoActual = displayCurrentTurn,
                        nextTurnInfo = nextTurnInfo,
                        onScanClick = {
                            // Si no tenemos permisos, solicitarlos
                            if (!cameraPermissionState.status.isGranted) {
                                launcher.launch(Manifest.permission.CAMERA)
                            } else {
                                showScanner = true
                            }
                        },
                        onAdvanceTurn = { viewModel.advanceToNextTurn() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de terminar evento
                if (isEventAvailable) {
                    Button(
                        onClick = { showEndEventConfirmation = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Terminar evento", color = Color.White)
                    }
                } else {
                    Text(
                        text = "Este evento ha finalizado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje de error si existe
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )

                    Button(
                        onClick = { viewModel.resetError() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Entendido")
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
            )
        }
    }

    // Diálogo de escáner QR
    if (showScanner) {
        Dialog(
            onDismissRequest = {
                showScanner = false
                viewModel.resetScanResult()
            }
        ) {
            QRScannerScreen(
                onCodeScanned = { qrValue ->
                    viewModel.handleQRScanResult(qrValue)
                    showScanner = false
                },
                onClose = {
                    showScanner = false
                    viewModel.resetScanResult()
                }
            )
        }
    }

    // Mostrar resultado del escaneo QR si hay alguno
    if (scanResult != null && scannedUserInfo != null) {
        QRScanResultDialog(
            scanResult = scanResult ?: "",
            userInfo = scannedUserInfo!!,
            onDismiss = {
                viewModel.resetScanResult()
            }
        )
    }

    // Diálogo de confirmación para terminar evento
    if (showEndEventConfirmation) {
        AlertDialog(
            onDismissRequest = { showEndEventConfirmation = false },
            title = { Text("Confirmar finalización") },
            text = { Text("¿Estás seguro de que deseas finalizar este evento? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.endEvent()
                        showEndEventConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Finalizar evento")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndEventConfirmation = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}