package com.example.proyecto_turnos_c.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val isAvailable: Boolean
)

class HomeViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())

    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val db = FirebaseFirestore.getInstance()
                val documents = db.collection("events").get().await()

                val eventsList = documents.map { doc ->
                    Event(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        startTime = doc.getString("startTime") ?: "",
                        endTime = doc.getString("endTime") ?: "",
                        location = doc.getString("location") ?: "",
                        isAvailable = doc.getBoolean("isAvailable") ?: false
                    )
                }

                _events.value = eventsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}