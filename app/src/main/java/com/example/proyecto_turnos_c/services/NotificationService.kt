package com.example.proyecto_turnos_c.services


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = false,
    val userId: String = "",
    val eventId: String = "",
    val isRead: Boolean = false
)

class NotificationService {
    private val db = FirebaseFirestore.getInstance()
    private val tag = "NotificationService"

    /**
     * Sends a notification to a specific user
     */
    suspend fun sendNotificationToUser(
        userId: String,
        title: String,
        message: String,
        isSuccess: Boolean,
        eventId: String = ""
    ): Boolean {
        return try {
            val notification = AppNotification(
                id = db.collection("notifications").document().id,
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                isSuccess = isSuccess,
                userId = userId,
                eventId = eventId
            )

            db.collection("notifications")
                .document(notification.id)
                .set(notification)
                .await()

            Log.d(tag, "Notification sent to user: $userId")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error sending notification to user: $userId", e)
            false
        }
    }

    /**
     * Sends a notification to all users registered for a specific event
     */
    suspend fun sendNotificationToEventUsers(
        eventId: String,
        eventName: String,
        title: String,
        message: String,
        isSuccess: Boolean
    ): Boolean {
        try {
            // Get all users registered for this event
            val registrationSnapshot = db.collection("events")
                .document(eventId)
                .collection("registrations")
                .get()
                .await()

            if (registrationSnapshot.isEmpty) {
                Log.d(tag, "No users registered for event: $eventId")
                return true
            }

            // Collect all user IDs
            val userIds = registrationSnapshot.documents.mapNotNull { doc ->
                doc.getString("userId")
            }

            Log.d(tag, "Sending notifications to ${userIds.size} users for event: $eventId")

            // Send notification to each user
            userIds.forEach { userId ->
                val notification = AppNotification(
                    id = db.collection("notifications").document().id,
                    title = title,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    isSuccess = isSuccess,
                    userId = userId,
                    eventId = eventId
                )

                db.collection("notifications")
                    .document(notification.id)
                    .set(notification)
                    .await()
            }

            return true
        } catch (e: Exception) {
            Log.e(tag, "Error sending event notifications for event: $eventId", e)
            return false
        }
    }

    /**
     * Formats a timestamp for display in notifications
     */
    fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "hace un momento"
            diff < 3600_000 -> "hace ${diff / 60_000} min."
            diff < 86400_000 -> "hace ${diff / 3600_000} horas"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}