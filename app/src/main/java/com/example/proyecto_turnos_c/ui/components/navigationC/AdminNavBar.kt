package com.example.proyecto_turnos_c.ui.components.navigationC

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AdminNavBar(navController: NavController, modifier: Modifier = Modifier) {
    // Obtenemos la entrada actual del backstack para determinar la ruta actual.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Asociasion de las rutas y etiquetas correspondientes a cada item.
    val routes = listOf("adminEvents", "addAdmin")
    val labels = listOf("Events", "Registro")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.GroupAdd)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.GroupAdd)

    // Determinamos el índice seleccionado en función de la ruta actual:
    val selectedIndex = when (currentRoute) {
        "adminEvents" -> 0
        "addAdmin" -> 1
//        "profile" -> 2
        else -> 0
    }

    NavigationBar(
        containerColor = Color(0xFF191C88),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .then(modifier)
    ) {
        labels.forEachIndexed { index, label ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedIndex == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = label,
                        tint = if (selectedIndex == index) Color(0xFFFFA73B) else Color(0xFFB0BEC5)
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (selectedIndex == index) Color(0xFFFFA73B)
                        else MaterialTheme.colorScheme.onPrimary
                    )
                },
                selected = selectedIndex == index,
                onClick = {
                    navController.navigate(routes[index]) {
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
