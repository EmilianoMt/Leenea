package com.example.proyecto_turnos_c.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_turnos_c.ui.screens.addAdmin.AddAmin
import com.example.proyecto_turnos_c.ui.screens.adminAddEvent.AdminAddEventsScreen
import com.example.proyecto_turnos_c.ui.screens.adminEvents.AdminEventsScreen
import com.example.proyecto_turnos_c.ui.screens.adminEventsDescription.AdminEventsDescScreen
import com.example.proyecto_turnos_c.ui.screens.eventsDescription.EventsDescScreen
import com.example.proyecto_turnos_c.ui.screens.eventsEnded.EventsEndedScreen
import com.example.proyecto_turnos_c.ui.screens.myEvents.MyEventsScreen
import com.example.proyecto_turnos_c.ui.screens.home.HomeScreen
import com.example.proyecto_turnos_c.ui.screens.login.Login
import com.example.proyecto_turnos_c.ui.screens.notifications.NotificatonsScreen
import com.example.proyecto_turnos_c.ui.screens.profile.ProfileScreen
import com.example.proyecto_turnos_c.ui.screens.register.Register

@Composable
fun NavigationGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
//        composable("splash") { SplashScreen(navController) }
        composable("login") { Login(navController) }
        composable("register") { Register(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("myEvents") { MyEventsScreen(navController) }
        composable("notifications") { NotificatonsScreen(navController) }
        composable(
            route = "EventsEnded/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventsEndedScreen(
                navController = navController,
                eventId = eventId
            )
        }
        composable(
            route = "EventsDesc/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            EventsDescScreen(
                navController = navController,
                eventId = eventId,
            )
        }
        composable(
            route = "AdminEventsDesc/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            AdminEventsDescScreen(
                navController = navController,
                eventId = eventId,
            )
        }
        composable("adminEvents"){ AdminEventsScreen(navController) }
        composable("createEvent"){ AdminAddEventsScreen(navController) }
        composable("addAdmin"){ AddAmin(navController) }
    }
}
