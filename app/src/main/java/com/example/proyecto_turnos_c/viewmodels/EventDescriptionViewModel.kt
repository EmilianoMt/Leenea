package com.example.proyecto_turnos_c.viewmodels


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.tasks.await

data class EventDetail(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val currentTurn: String = "",
    val yourTurn: String = ""
)

class EventDescriptionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    private val _eventDetail = MutableStateFlow<EventDetail?>(null)
    val eventDetail: StateFlow<EventDetail?> = _eventDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchEventDetail()
    }

    private fun fetchEventDetail() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val db = FirebaseFirestore.getInstance()
                val document = db.collection("events").document(eventId).get().await()

                if (document.exists()) {
                    val timestamp = document.getTimestamp("date")
                    val dateString = timestamp?.toDate()?.let {
                        // Format date as needed, for example:
                        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it)
                    } ?: ""
                    _eventDetail.value = EventDetail(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        date = dateString,
                        startTime = document.getString("startTime") ?: "",
                        endTime = document.getString("endTime") ?: "",
                        location = document.getString("location") ?: "",
                        currentTurn = document.getString("currentTurn") ?: "001",
                        yourTurn = document.getString("yourTurn") ?: "-"
                    )
                } else {
                    _error.value = "Evento no encontrado"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}




class EventDescriptionViewModelFactory(private val eventId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDescriptionViewModel::class.java)) {
            val savedStateHandle = SavedStateHandle().apply {
                set("eventId", eventId)
            }
            @Suppress("UNCHECKED_CAST")
            return EventDescriptionViewModel(savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}