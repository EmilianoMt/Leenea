package com.example.proyecto_turnos_c.ui.screens.adminAddEvent

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.DateSelector
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.DescriptionField
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.EventDatePicker
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.EventTimePicker
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.ImageSelector
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.LocationField
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.SaveEventButton
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.TimeSelector
import com.example.proyecto_turnos_c.ui.components.admin.formEvent.TitleField
import com.example.proyecto_turnos_c.ui.components.dialogs.EventComfirm
import kotlinx.coroutines.delay


@Composable
fun AdminAddEventsScreen(
    navController: NavController,
    viewModel: AdminAddEventsViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Obtener los estados del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val creationState by viewModel.eventCreationState.collectAsState()

    // Estados para los pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Estado para controlar la visualización del diálogo de confirmación
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Selector de imágenes
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateImageUri(uri)
    }

    // Observar el estado de creación del evento
    LaunchedEffect(creationState) {
        when (creationState) {
            is EventCreationState.Success -> {
                Log.d("AdminAddEventsScreen", "Evento creado con éxito - Mostrando diálogo")
                showConfirmDialog = true
            }
            else -> {}
        }
    }

    // Efecto para cerrar el diálogo automáticamente después de 3 segundos
    LaunchedEffect(showConfirmDialog) {
        if (showConfirmDialog) {
            Log.d("AdminAddEventsScreen", "Diálogo mostrado - Esperando 3 segundos")
            delay(3000)
            Log.d("AdminAddEventsScreen", "Cerrando diálogo y reseteando formulario")
            showConfirmDialog = false
            viewModel.resetForm()
        }
    }

    // Diálogo de confirmación
    if (showConfirmDialog) {
        EventComfirm(
            onDismissRequest = {
                showConfirmDialog = false
                viewModel.resetForm()
            },
            message = "Evento creado de forma exitosa"
        )
    }

    Scaffold(
        topBar = {
            EventAppBar(
                title = "Creación de evento",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Componentes del formulario actualizados para usar el ViewModel
                        TitleField(
                            title = uiState.title,
                            onTitleChange = { viewModel.updateTitle(it) }
                        )

                        ImageSelector(
                            imageUri = uiState.imageUri,
                            onPickImage = { imagePicker.launch("image/*") }
                        )

                        DateSelector(
                            selectedDate = uiState.date,
                            onShowDatePicker = { showDatePicker = true }
                        )

                        TimeSelector(
                            startTime = uiState.startTime,
                            endTime = uiState.endTime,
                            onStartTimeClick = { showStartTimePicker = true },
                            onEndTimeClick = { showEndTimePicker = true }
                        )

                        LocationField(
                            location = uiState.location,
                            onLocationChange = { viewModel.updateLocation(it) }
                        )

                        DescriptionField(
                            description = uiState.description,
                            onDescriptionChange = { viewModel.updateDescription(it) }
                        )
                    }
                }
                SaveEventButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createEvent(context)
                        }
                    },
                    isLoading = creationState is EventCreationState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }

    // DatePicker
    if (showDatePicker) {
        EventDatePicker(
            initialSelectedDateMillis = uiState.date ?: System.currentTimeMillis(),
            onDateSelected = { viewModel.updateDate(it) },
            onDismiss = { showDatePicker = false }
        )
    }

    // TimePicker para hora de inicio
    if (showStartTimePicker) {
        EventTimePicker(
            title = "Seleccionar hora de inicio",
            initialTime = uiState.startTime,
            onTimeSelected = { viewModel.updateStartTime(it) },
            onDismiss = { showStartTimePicker = false }
        )
    }

    // TimePicker para hora de finalización
    if (showEndTimePicker) {
        EventTimePicker(
            title = "Seleccionar hora de finalización",
            initialTime = uiState.endTime,
            onTimeSelected = { viewModel.updateEndTime(it) },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventAppBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Volver"
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.weight(1f))
        },
        modifier = Modifier.fillMaxWidth()
    )
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(top = 40.dp)
        )
    }
}
