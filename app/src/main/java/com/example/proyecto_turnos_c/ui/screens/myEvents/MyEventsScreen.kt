package com.example.proyecto_turnos_c.ui.screens.userEvents

import NavBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto_turnos_c.ui.components.user.CircularImageCard
import com.example.proyecto_turnos_c.ui.components.navigationC.EventTabs
import com.example.proyecto_turnos_c.viewmodels.UserEventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    viewModel: UserEventsViewModel = viewModel()
) {
    val userEvents by viewModel.userEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(106.dp),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mis inscripciones",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.navigate("notifications") }) {
//                        Icon(
//                            imageVector = Icons.Outlined.Notifications,
//                            contentDescription = "Notificaciones",
//                            tint = Color.Black,
//                            modifier = Modifier.size(30.dp)
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { NavBar(navController = navController) }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            viewModel = viewModel,
            isLoading = isLoading,
            error = error
        )
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: UserEventsViewModel,
    isLoading: Boolean,
    error: String?
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: $error", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            EventTabs(
                content = { AvailableRegisteredEvents(navController = navController, viewModel = viewModel) },
                finishedContent = { FinishedRegisteredEvents(navController = navController, viewModel = viewModel) }
            )
        }
    }
}

@Composable
fun AvailableRegisteredEvents(navController: NavController, viewModel: UserEventsViewModel) {
    val availableEvents = viewModel.getAvailableEvents()

    if (availableEvents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No tienes inscripciones activas",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(availableEvents) { event ->
                CircularImageCard(
                    title = event.title,
                    description = "${event.description}\nFecha: ${event.date}\nTurno: ${event.turnNumber}",
                    imageUrl = event.imageUrl,
                    isAvailable = event.isAvailable,
                    action = {
                        navController.navigate("EventsDesc/${event.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun FinishedRegisteredEvents(navController: NavController, viewModel: UserEventsViewModel) {
    val finishedEvents = viewModel.getFinishedEvents()

    if (finishedEvents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No tienes inscripciones finalizadas",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(finishedEvents) { event ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularImageCard(
                        imageUrl = event.imageUrl,
                        title = event.title,
                        description = "${event.description}\nFecha: ${event.date}\nEstado: ${event.status}",
                        isAvailable = event.isAvailable,
                        action = {
                            navController.navigate("EventsEnded/${event.id}")
                        }
                    )
                }
            }
        }
    }
}