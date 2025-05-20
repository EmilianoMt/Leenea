package com.example.proyecto_turnos_c.ui.screens.adminEvents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_turnos_c.ui.components.admin.AdminEventCard
import com.example.proyecto_turnos_c.ui.components.navigationC.AdminNavBar
import com.example.proyecto_turnos_c.ui.components.user.EventCard
import com.example.proyecto_turnos_c.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventsScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(84.dp),
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {navController.navigate("adminEvents")},
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "Menú",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }, bottomBar = {
            AdminNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 35.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Eventos disponibles",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: $error",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                events.isEmpty() -> {
                    Box(modifier = Modifier
                        .size(400.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay eventos disponibles",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    CreateEventButton(navController)
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(events) { event ->
                            AdminEventCard(
                                title = event.title,
                                action = {
                                    navController.navigate("AdminEventsDesc/${event.id}")
                                }
                            )
                        }

                        item {
                            CreateEventButton(navController)
                        }

                        item {
                            BtnLogout(viewModel, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateEventButton(navController: NavController){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { navController.navigate("createEvent") }
            .drawBehind {
                val strokeWidth = 3f
                val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                drawRoundRect(
                    color = Color.Gray,
                    style = Stroke(width = strokeWidth, pathEffect = dash),
                    cornerRadius = CornerRadius(16f, 16f)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = "Agregar nuevo evento",
            tint = Color.Gray,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun BtnLogout(viewModel: HomeViewModel, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(26.dp))
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
        ) {
            Text("Cerrar Sesión")
        }

    }
}

