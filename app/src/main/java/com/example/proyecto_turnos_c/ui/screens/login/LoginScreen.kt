
package com.example.proyecto_turnos_c.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_turnos_c.R
import com.example.proyecto_turnos_c.viewmodels.LoginViewModel

@Composable
fun Login(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    Scaffold { innerPadding ->
        val loginState = loginViewModel.loginState

        LaunchedEffect(key1 = loginState.success) {
            if (loginState.success) {
                when(loginState.userRole){
                    "admin" -> navController.navigate("adminEvents")
                    "user" -> navController.navigate("home")
                    else -> navController.navigate("home")
                }
            }
        }

        LoginForm(navController = navController, modifier = Modifier.padding(innerPadding), loginViewModel = loginViewModel)
    }
}

@Composable
fun LoginForm(navController: NavController, loginViewModel: LoginViewModel ,modifier: Modifier = Modifier) {
    // Estados para los campos de texto
    val expedienteState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState = loginViewModel.loginState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen de perfil (placeholder)
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Placeholder de perfil",
                modifier = Modifier.size(240.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Título
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF191C88)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Error message display
        if (loginState.error != null) {
            Text(
                text = loginState.error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Campo para correo electrónico
        OutlinedTextField(
            value = expedienteState.value,
            onValueChange = {
                expedienteState.value = it
                // Clear error when user starts typing
                if (loginState.error != null) {
                    loginViewModel.clearError()
                }
            },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) {
                    // Clear error when clicking on the field
                    if (loginState.error != null) {
                        loginViewModel.clearError()
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFFFA73B),
                focusedLabelColor = Color(0xFFFFA73B),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFF191C88),
                unfocusedTextColor = Color.Gray
            ),
            isError = loginState.error != null
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Campo para Contraseña
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = {
                passwordState.value = it
                // Clear error when user starts typing
                if (loginState.error != null) {
                    loginViewModel.clearError()
                }
            },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Clear error when clicking on the field
                    if (loginState.error != null) {
                        loginViewModel.clearError()
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFFFA73B),
                focusedLabelColor = Color(0xFFFFA73B),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFF191C88),
                unfocusedTextColor = Color.Gray
            ),
            isError = loginState.error != null
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (loginState.loading){
            CircularProgressIndicator(modifier = Modifier
                .padding(16.dp))
        }

        Button(
            onClick = {
                loginViewModel.loginUser(
                    expedienteState.value,
                    passwordState.value
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA73B)),
//            enabled = !loginState.loading && expedienteState.value.isNotEmpty() && passwordState.value.isNotEmpty()
        ) {
            Text(text = "Iniciar sesión", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Registrate",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF191C88),
            modifier = Modifier.clickable {
                navController.navigate("register")
            }
        )
    }
}