package com.example.proyecto_turnos_c.ui.screens.adminEventsDescription

import NavBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto_turnos_c.ui.components.admin.AdminEventDetailCard
import com.example.proyecto_turnos_c.ui.components.user.EventDetailCard
import com.example.proyecto_turnos_c.viewmodels.EventDescriptionViewModel
import com.example.proyecto_turnos_c.viewmodels.EventDescriptionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventsDescScreen(
    navController: NavController,
    eventId: String,
    viewModel: EventDescriptionViewModel = viewModel(
        factory = EventDescriptionViewModelFactory(eventId)
    )
) {
    //Estados del viewModel
    val eventDetail by viewModel.eventDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mis eventos",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            error?.let {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: $it",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("Volver")
                    }
                }
            }

            eventDetail?.let { event ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBackIosNew,
                                    contentDescription = "Volver",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AdminEventDetailCard(
                                eventId = eventId,
                                imageUrl = event.imageUrl,
                                fechaHora = "${event.date}\n${event.startTime} - ${event.endTime}",
                                ubicacion = event.location,
                                descripcion = event.description,
                                turnoActual = event.currentTurn,
                            )
                        }
                    }
                }
            }
        }
    }
}

