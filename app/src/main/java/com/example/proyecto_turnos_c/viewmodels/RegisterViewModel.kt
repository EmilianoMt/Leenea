package com.example.proyecto_turnos_c.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

data class RegisterState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var registerState by mutableStateOf(RegisterState())
        private set

    fun registerUser(fullName: String, email: String, password: String, expediente: String, rol: String) {
        // Validar campos vacíos
        when {
            fullName.isBlank() -> {
                registerState = registerState.copy(error = "El nombre completo es obligatorio")
                return
            }
            expediente.isBlank() -> {
                registerState = registerState.copy(error = "El número de expediente es obligatorio")
                return
            }
            email.isBlank() -> {
                registerState = registerState.copy(error = "El correo electrónico es obligatorio")
                return
            }
            password.isBlank() -> {
                registerState = registerState.copy(error = "La contraseña es obligatoria")
                return
            }
            password.length < 6 -> {
                registerState = registerState.copy(error = "La contraseña debe tener al menos 6 caracteres")
                return
            }
            !isValidEmail(email) -> {
                registerState = registerState.copy(error = "El formato del correo electrónico no es válido")
                return
            }
            !isValidExpediente(expediente) -> {
                registerState = registerState.copy(error = "El número de expediente debe tener 6 dígitos")
                return
            }
        }

        registerState = registerState.copy(loading = true, error = null, success = false)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "uid" to user.uid,
                            "fullName" to fullName,
                            "email" to email,
                            "expediente" to expediente,
                            "role" to rol
                        )
                        firestore.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                registerState = registerState.copy(loading = false, success = true)
                            }
                            .addOnFailureListener { e ->
                                registerState = registerState.copy(
                                    loading = false,
                                    error = "Hubo un problema al guardar tus datos. Por favor intenta nuevamente."
                                )
                            }
                    } else {
                        registerState = registerState.copy(
                            loading = false,
                            error = "No se pudo completar el registro. Por favor intenta nuevamente."
                        )
                    }
                } else {
                    registerState = registerState.copy(
                        loading = false,
                        error = getReadableErrorMessage(task.exception)
                    )
                }
            }
    }

    private fun getReadableErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil. Usa al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico."
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "El formato del correo electrónico no es válido."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Ya existe una cuenta con este correo electrónico."
                    "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil. Usa al menos 6 caracteres."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "El registro con correo y contraseña no está habilitado."
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Error de conexión a internet. Verifica tu conexión."
                    else -> "Error al crear la cuenta: ${exception.message}"
                }
            }
            else -> "No se pudo completar el registro. Por favor intenta nuevamente."
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidExpediente(expediente: String): Boolean {
        val expedienteRegex = "^\\d{6}$"
        return expediente.matches(expedienteRegex.toRegex())
    }
}