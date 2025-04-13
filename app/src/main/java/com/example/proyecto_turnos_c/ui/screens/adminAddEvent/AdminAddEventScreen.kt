package com.example.proyecto_turnos_c.ui.screens.adminAddEvent

import NavBar
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun AdminAddEventsScreen(navController: NavController) {
    // Estados para todos los campos del formulario
    var title by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estados para los pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            EventAppBar(title = "Creación de evento")
        },
//        bottomBar = {  NavBar(navController = navController)}

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
                        // Componentes del formulario
                        TitleField(title = title, onTitleChange = { title = it })

                        ImageSelector(
                            imageUri = imageUri,
                            onPickImage = { imagePicker.launch("image/*") }
                        )

                        DateSelector(
                            selectedDate = selectedDate,
                            onShowDatePicker = { showDatePicker = true }
                        )

                        TimeSelector(
                            startTime = startTime,
                            endTime = endTime,
                            onStartTimeClick = { showStartTimePicker = true },
                            onEndTimeClick = { showEndTimePicker = true }
                        )

                        LocationField(
                            location = location,
                            onLocationChange = { location = it }
                        )

                        DescriptionField(
                            description = description,
                            onDescriptionChange = { description = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                SaveEventButton(onClick = { /* Guardar evento */ })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // DatePicker
    if (showDatePicker) {
        EventDatePicker(
            initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis(),
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false }
        )
    }

    // TimePicker para hora de inicio
    if (showStartTimePicker) {
        EventTimePicker(
            title = "Seleccionar hora de inicio",
            initialTime = startTime,
            onTimeSelected = { startTime = it },
            onDismiss = { showStartTimePicker = false }
        )
    }

    // TimePicker para hora de finalización
    if (showEndTimePicker) {
        EventTimePicker(
            title = "Seleccionar hora de finalización",
            initialTime = endTime,
            onTimeSelected = { endTime = it },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventAppBar(title: String) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = { /* Abrir menú lateral */ }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Menú"
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


@Composable
fun TitleField(title: String, onTitleChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Inserte un título",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFFFFAB40)
            )
        )
    }
}

@Composable
fun ImageSelector(imageUri: Uri?, onPickImage: () -> Unit) {
    val context = LocalContext.current

    Column {
        Text(
            text = "Imagen",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Seleccione una imagen para el evento",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onPickImage),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir imagen",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
            } else {
                // Usar SubcomposeAsyncImage para manejar los diferentes estados
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del evento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        // Estado de carga
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFFFAB40)
                            )
                        }
                    },
                    error = {
                        // Estado de error
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error al cargar la imagen",
                                    tint = Color.Red
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Error al cargar la imagen",
                                    color = Color.Red
                                )
                            }
                        }
                    }
                )

                // Overlay para indicar que la imagen está seleccionada
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Imagen seleccionada",
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun DateSelector(selectedDate: Long?, onShowDatePicker: () -> Unit) {
    Column {
        Text(
            text = "Fecha",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Seleccione la fecha del evento",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .clickable(onClick = onShowDatePicker),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = selectedDate?.let { convertMillisToDate(it) } ?: "Seleccionar fecha",
                    color = if (selectedDate == null) Color.Gray else Color.Black,
                    fontSize = 14.sp
                )

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha",
                    tint = Color.Gray
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSelector(
    startTime: LocalTime?,
    endTime: LocalTime?,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    Column {
        Text(
            text = "Hora",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Ingrese el horario del evento",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hora de inicio
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .clickable(onClick = onStartTimeClick),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Hora de inicio",
                        color = if (startTime == null) Color.Gray else Color.Black,
                        fontSize = 14.sp
                    )
                }
            }

            // Hora de finalización
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .clickable(onClick = onEndTimeClick),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Hora de finalización",
                        color = if (endTime == null) Color.Gray else Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LocationField(location: String, onLocationChange: (String) -> Unit) {
    Column {
        Text(
            text = "Ubicación",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Ingrese el lugar del evento",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFFFFAB40)
            )
        )
    }
}

@Composable
fun DescriptionField(description: String, onDescriptionChange: (String) -> Unit) {
    Column {
        Text(
            text = "Descripción",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Inserte la descripción del evento",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFFFFAB40)
            )
        )
    }
}

@Composable
fun SaveEventButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40))
    ) {
        Text(
            text = "Guardar evento",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDatePicker(
    initialSelectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                onDismiss()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTimePicker(
    title: String,
    initialTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 12,
        initialMinute = initialTime?.minute ?: 0
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AdminAddEventsScreenPreview() {
    AdminAddEventsScreen(navController = rememberNavController())
}