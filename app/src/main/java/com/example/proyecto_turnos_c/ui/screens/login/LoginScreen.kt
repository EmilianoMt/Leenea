package com.example.proyecto_turnos_c.ui.screens.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto_turnos_c.R

@Composable
fun Login() {
    Scaffold {
        it.calculateTopPadding()
        LoginForm()
    }
}

@Composable
fun LoginForm() {
    // Estados para los campos de texto
    val expedienteState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder circular para la imagen del perfil
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
            color =  Color(0xFF191C88),
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Campo de texto para el correo electrónico
        OutlinedTextField(
            value = expedienteState.value,
            onValueChange = { expedienteState.value = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFFFA73B),
                focusedLabelColor = Color(0xFFFFA73B),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFF191C88),
                unfocusedTextColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Campo de texto para Contraseña
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
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
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFFFA73B),
                focusedLabelColor = Color(0xFFFFA73B),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFF191C88),
                unfocusedTextColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Botón de inicio de sesión en color naranja
        Button(
            onClick = { /* Acción de iniciar sesión */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA73B))
        ) {
            Text(text = "Iniciar sesión", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Enlace de registro
        Text(
            text = "Registrate",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF191C88),
            modifier = Modifier.clickable {
                /* Acción para ir a la pantalla de registro */
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}
