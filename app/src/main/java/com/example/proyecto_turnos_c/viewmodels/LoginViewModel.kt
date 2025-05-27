package com.example.proyecto_turnos_c.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class LoginState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val userRole: String = ""
)

class LoginViewModel : ViewModel(){

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var loginState by mutableStateOf(LoginState())
        private set

    fun loginUser(email: String, password: String){
        // Validate inputs first
        if (email.isBlank()) {
            loginState = loginState.copy(error = "El correo electrónico no puede estar vacío")
            return
        }

        if (password.isBlank()) {
            loginState = loginState.copy(error = "La contraseña no puede estar vacía")
            return
        }

        // Start login process
        loginState = loginState.copy(loading = true, error = null, success = false, userRole = "")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    if(user != null){
                        firestore.collection("users").document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")?: "user"
                                loginState = loginState.copy(loading = false, success = true, userRole = role)
                            }
                            .addOnFailureListener{ e ->
                                loginState = loginState.copy(loading = false, error = "Error al obtener el rol: ${e.message}")
                            }
                    }
                }
                else {
                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                            "Usuario y/o contraseña incorrectos"
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                            "Usuario y/o contraseña incorrectos"
                        is com.google.firebase.auth.FirebaseAuthWebException ->
                            "Error de conexión. Verifica tu conexión a internet"
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                            "Esta cuenta ya está en uso con otro proveedor"
                        is com.google.firebase.FirebaseTooManyRequestsException ->
                            "Demasiados intentos fallidos. Intenta más tarde"
                        else ->
                            "Usuario y/o contraseña incorrectos"
                    }

                    loginState = loginState.copy(
                        loading = false,
                        error = errorMessage
                    )
                }
            }
    }

    fun clearError() {
        loginState = loginState.copy(error = null)
    }
}