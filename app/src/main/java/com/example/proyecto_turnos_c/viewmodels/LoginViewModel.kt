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
    val success: Boolean = false
)

class LoginViewModel : ViewModel(){

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var loginState by mutableStateOf(RegisterState())
        private set

    fun loginUser(email: String, password: String){
        loginState = loginState.copy(loading = true, error = null, success = false)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    if(user != null){
                        firestore.collection("users").document(user.uid)
                            .get()
                            .addOnSuccessListener {
                                loginState = loginState.copy(loading = false, success = true)
                            }
                            .addOnFailureListener{ e ->
                                loginState = loginState.copy(loading = false, error = e.message)
                            }
                    }
                }
                else{
                    loginState = loginState.copy(
                        loading = false,
                        error = task.exception?.message ?: "Login fallido"
                    )
                }
            }
    }
}