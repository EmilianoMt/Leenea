package com.example.proyecto_turnos_c.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

data class QueueRegistration(
    val userId: String = "",
    val eventId: String = "",
    val turnNumber: String = "",
    val timestamp: Date = Date(),
    val status: String = "waiting" // waiting, completed, canceled
)

class EventDetailCardViewModel(private val eventId: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isInQueue = MutableStateFlow(false)
    val isInQueue: StateFlow<Boolean> = _isInQueue

    private val _userTurn = MutableStateFlow("")
    val userTurn: StateFlow<String> = _userTurn

    private val _currentTurn = MutableStateFlow("")
    val currentTurn: StateFlow<String> = _currentTurn

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        checkIfUserInQueue()
        getCurrentTurn()
    }

    private fun checkIfUserInQueue() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                val userRegistration = db.collection("events")
                    .document(eventId)
                    .collection("registrations")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (!userRegistration.isEmpty) {
                    _isInQueue.value = true
                    val registration = userRegistration.documents.first()
                    _userTurn.value = registration.getString("turnNumber") ?: ""
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al verificar tu turno: ${e.message}"
            }
        }
    }

    private fun getCurrentTurn() {
        db.collection("events")
            .document(eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = "Error al obtener el turno actual: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    _currentTurn.value = snapshot.getString("currentTurn") ?: "000"
                }
            }
    }

    fun joinQueue() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid
                    ?: throw Exception("Usuario no autenticado")

                // 1) Checar **antes** en qué turno está (fuera de la tx)
                val existing = db.collection("events")
                    .document(eventId)
                    .collection("registrations")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (!existing.isEmpty) {
                    // Ya está registrado, recuperamos su turno y salimos
                    _userTurn.value = existing.documents.first().getString("turnNumber") ?: ""
                    _isInQueue.value = true
                    return@launch
                }

                // 2) Si no existe, vamos a la transacción sólo para el counter + crear registro
                val newTurn = db.runTransaction { transaction ->
                    val eventRef = db.collection("events").document(eventId)
                    val eventSnap = transaction.get(eventRef)
                    val last = eventSnap.getLong("lastTurnNumber") ?: 0
                    val next = last + 1
                    val formatted = String.format("%03d", next)

                    // Actualizamos counter
                    transaction.update(eventRef, "lastTurnNumber", next)

                    // Preparamos datos de registro
                    val reg = QueueRegistration(
                        userId = userId,
                        eventId = eventId,
                        turnNumber = formatted,
                        timestamp = Date(),
                        status = "waiting"
                    )
                    // Creamos documento
                    val regRef = db.collection("events")
                        .document(eventId)
                        .collection("registrations")
                        .document()
                    transaction.set(regRef, reg)

                    formatted
                }.await()

                _userTurn.value = newTurn
                _isInQueue.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error al unirse a la fila: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun resetError() {
        _errorMessage.value = null
    }
}

class EventDetailCardViewModelFactory(private val eventId: String, private val userId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailCardViewModel::class.java)) {
            return EventDetailCardViewModel(eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}