package com.example.proyecto_turnos_c.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_turnos_c.services.AppNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel(private val userId: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val tag = "NotificationViewModel"

    // Notifications list
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Errors
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Method to load user's notifications
    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = db.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val notificationsList = result.documents.mapNotNull { doc ->
                    try {
                        AppNotification(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            isSuccess = doc.getBoolean("isSuccess") ?: false,
                            userId = doc.getString("userId") ?: "",
                            eventId = doc.getString("eventId") ?: "",
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    } catch (e: Exception) {
                        Log.e(tag, "Error parsing notification", e)
                        null
                    }
                }

                _notifications.value = notificationsList

                // Mark all as read
                notificationsList.filter { !it.isRead }.forEach { notification ->
                    db.collection("notifications")
                        .document(notification.id)
                        .update("isRead", true)
                }

                Log.d(tag, "Loaded ${notificationsList.size} notifications for user $userId")
            } catch (e: Exception) {
                Log.e(tag, "Error loading notifications", e)
                _error.value = "Error al cargar notificaciones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Factory to create the ViewModel with parameters
    class Factory(private val userId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                return NotificationViewModel(userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}