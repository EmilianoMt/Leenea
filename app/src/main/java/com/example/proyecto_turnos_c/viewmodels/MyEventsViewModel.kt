package com.example.proyecto_turnos_c.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",     // Formatted date string
    val endTime: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val status: String = "",
    val turnNumber: String = ""
)

class UserEventsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userEvents = MutableStateFlow<List<UserEvent>>(emptyList())
    val userEvents: StateFlow<List<UserEvent>> = _userEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadUserEvents()
    }

    fun loadUserEvents() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val currentUserId = auth.currentUser?.uid
                    ?: throw IllegalStateException("Usuario no ha iniciado sesión")

                val userRegisteredEvents = fetchUserRegisteredEvents(currentUserId)
                _userEvents.value = userRegisteredEvents

            } catch (e: Exception) {
                _error.value = "Error al cargar eventos: ${e.message ?: "Error desconocido"}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchUserRegisteredEvents(userId: String): List<UserEvent> {
        return withContext(Dispatchers.IO) {
            try {
                // Step 1: Get all events
                val allEvents = firestore.collection("events")
                    .get()
                    .await()

                val userEvents = mutableListOf<UserEvent>()

                // Step 2: For each event, check if user is registered
                for (eventDoc in allEvents.documents) {
                    val eventId = eventDoc.id

                    // Check if user is registered in this event
                    val registrationQuery = firestore.collection("events")
                        .document(eventId)
                        .collection("registrations")
                        .whereEqualTo("userId", userId)
                        .limit(1)
                        .get()
                        .await()

                    // If user is registered, add event to list
                    if (!registrationQuery.isEmpty) {
                        val registrationDoc = registrationQuery.documents[0]
                        val status = getStringValue(registrationDoc, "status")
                        val turnNumber = getStringValue(registrationDoc, "turnNumber")

                        userEvents.add(
                            UserEvent(
                                id = eventId,
                                title = getStringValue(eventDoc, "title"),
                                description = getStringValue(eventDoc, "description"),
                                date = getFormattedDateValue(eventDoc, "date"),
                                endTime = getStringValue(eventDoc, "endTime"),
                                imageUrl = getStringValue(eventDoc, "imageUrl"),
                                isAvailable = getBooleanValue(eventDoc, "isAvailable", true),
                                status = status,
                                turnNumber = turnNumber
                            )
                        )
                    }
                }

                userEvents
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // Helper functions to safely get values from Firestore
    private fun getStringValue(doc: DocumentSnapshot, field: String): String {
        return doc.getString(field) ?: ""
    }

    private fun getBooleanValue(doc: DocumentSnapshot, field: String, defaultValue: Boolean): Boolean {
        return doc.getBoolean(field) ?: defaultValue
    }

    private fun getFormattedDateValue(doc: DocumentSnapshot, field: String): String {
        val timestamp = doc.getTimestamp(field)
        return if (timestamp != null) {
            formatDate(timestamp.toDate())
        } else {
            // Fallback: try to get as string if it's not a timestamp
            doc.getString(field) ?: ""
        }
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }

    fun getAvailableEvents(): List<UserEvent> {
        return userEvents.value.filter { it.isAvailable }
    }

    fun getFinishedEvents(): List<UserEvent> {
        return userEvents.value.filter { !it.isAvailable }
    }
}