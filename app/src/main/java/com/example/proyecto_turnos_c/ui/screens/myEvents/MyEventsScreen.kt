package com.example.proyecto_turnos_c.ui.screens.myEvents

import NavBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_turnos_c.R
import com.example.proyecto_turnos_c.ui.components.CircularImageCard
import com.example.proyecto_turnos_c.ui.components.EventTabs

// Modelo para los eventos
data class EventData(
    val title: String,
    val subtitle: String,
    val isAvailable: Boolean
)

// Lista de ejemplo de eventos.
val eventList = listOf(
    EventData("Evento Destacado 1", "No te lo pierdas", true),
    EventData("Evento Destacado 2", "No te lo pierdas", false),
    EventData("Evento Destacado 3", "No te lo pierdas", true),
    EventData("Evento Destacado 4", "No te lo pierdas", false)
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(106.dp),
                title = {
                    // Centra el título
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
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { NavBar(navController = navController) }
    ) {
        ScreenContent()
    }
}

@Composable
fun ScreenContent() {
    EventTabs(
        content = { EventosVigentes() },
        finishedContent = { EventosFinalizados() }
    )
}

@Composable
fun EventosVigentes() {
    // Filtra los eventos vigentes (isAvailable == true)
    val upcomingEvents = eventList.filter { it.isAvailable }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(upcomingEvents) { event ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularImageCard(
                    imageRes = R.drawable.event1,
                    title = event.title,
                    description = event.subtitle,
                    isAvailable = event.isAvailable
                )
            }
        }
    }
}

@Composable
fun EventosFinalizados() {
    // Filtra los eventos finalizados (isAvailable == false)
    val finishedEvents = eventList.filter { !it.isAvailable }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(finishedEvents) { event ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularImageCard(
                    imageRes = R.drawable.event1,
                    title = event.title,
                    description = event.subtitle,
                    isAvailable = event.isAvailable
                )
            }
        }
    }
}

@Preview
@Composable
fun MyEventsScreenPreview() {
    MyEventsScreen(navController = rememberNavController())
}
