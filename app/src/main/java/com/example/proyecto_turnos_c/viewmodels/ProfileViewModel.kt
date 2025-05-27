package com.example.proyecto_turnos_c.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val expediente: String = "",
    val email: String = ""
)

class ProfileViewModel : ViewModel() {
    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userProfile = document.toObject(UserProfile::class.java)
                    }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        userProfile = null
        isLoading = false
    }
}
