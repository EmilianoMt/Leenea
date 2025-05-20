package com.example.proyecto_turnos_c.ui.screens.addAdmin


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto_turnos_c.R
import com.example.proyecto_turnos_c.ui.components.navigationC.AdminNavBar
import com.example.proyecto_turnos_c.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAmin(navController: NavController, registerViewModel: RegisterViewModel = viewModel()) {
    val registerState = registerViewModel.registerState

    LaunchedEffect(key1 = registerState.success) {
        if (registerState.success) {
            navController.navigate("login")
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(84.dp),
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {navController.navigate("adminEvents") },
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
        RegisterForm(
            navController = navController,
            registerViewModel = registerViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun RegisterForm(navController: NavController, registerViewModel: RegisterViewModel, modifier: Modifier = Modifier) {
    // Estados para los campos de texto y errores
    var fullName by remember { mutableStateOf("") }
    var expediente by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados para errores específicos de cada campo
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var expedienteError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Actualizar errores específicos desde el ViewModel
    LaunchedEffect(registerViewModel.registerState.error) {
        val error = registerViewModel.registerState.error

        when {
            error == null -> {
                // Limpiar todos los errores
                fullNameError = null
                expedienteError = null
                emailError = null
                passwordError = null
                confirmPasswordError = null
            }
            error.contains("nombre completo") -> fullNameError = error
            error.contains("expediente") -> expedienteError = error
            error.contains("correo") || error.contains("email") -> emailError = error
            error.contains("contraseña") -> passwordError = error
            else -> { /* Mantener el error general para mostrarlo al final */ }
        }
    }

    // Validar que las contraseñas coinciden
    LaunchedEffect(password, confirmPassword) {
        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
        } else {
            confirmPasswordError = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Título
        Text(
            text = "Registar Administrador",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF191C88)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo para "Nombre Completo"
        CustomTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = null
            },
            label = "Nombre Completo",
            errorMessage = fullNameError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para "Expediente"
        CustomTextField(
            value = expediente,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    expediente = it
                    expedienteError = null  // Limpiar error al editar
                }
            },
            label = "Expediente (6 dígitos)",
            errorMessage = expedienteError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para "Correo electrónico"
        CustomTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = "Correo electrónico",
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para "Contraseña"
        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = "Contraseña",
            errorMessage = passwordError,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChanged = { passwordVisible = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para "Confirmar Contraseña"
        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = null
            },
            label = "Confirmar Contraseña",
            errorMessage = confirmPasswordError,
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChanged = { confirmPasswordVisible = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (registerViewModel.registerState.loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        // Botón de registro
        Button(
            onClick = {
                registerViewModel.registerUser(
                    fullName,
                    email,
                    password,
                    expediente,
                    rol = "user"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA73B)),
        ) {
            Text(text = "Registrar", color = Color.White)
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChanged: ((Boolean) -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            isError = !errorMessage.isNullOrEmpty(),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = {
                if (isPassword && onPasswordVisibilityChanged != null) {
                    IconButton(onClick = { onPasswordVisibilityChanged(!passwordVisible) }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar Contraseña"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (errorMessage.isNullOrEmpty()) Color(0xFFFFA73B) else Color.Red,
                unfocusedIndicatorColor = if (errorMessage.isNullOrEmpty()) Color.Gray else Color.Red,
                focusedLabelColor = if (errorMessage.isNullOrEmpty()) Color(0xFFFFA73B) else Color.Red,
                unfocusedLabelColor = if (errorMessage.isNullOrEmpty()) Color.Gray else Color.Red,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFF191C88),
                unfocusedTextColor = Color.Gray,
                errorIndicatorColor = Color.Red,
                errorLabelColor = Color.Red,
                errorTextColor = Color(0xFF191C88)
            )
        )

        // Mensaje de error debajo del campo
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

