package com.example.proyecto_turnos_c.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
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

    fun registerUser(fullName: String, email: String, password: String, expediente: String) {
        registerState = registerState.copy(loading = true, error = null, success = false)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "fullName" to fullName,
                            "email" to email,
                            "expediente" to expediente,
                            "uid" to user.uid
                        )
                        firestore.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                registerState = registerState.copy(loading = false, success = true)
                            }
                            .addOnFailureListener { e ->
                                registerState = registerState.copy(loading = false, error = e.message)
                            }
                    } else {
                        registerState = registerState.copy(loading = false, error = "User not found")
                    }
                } else {
                    registerState = registerState.copy(
                        loading = false,
                        error = task.exception?.message ?: "Registro fallido"
                    )
                }
            }
    }
}
