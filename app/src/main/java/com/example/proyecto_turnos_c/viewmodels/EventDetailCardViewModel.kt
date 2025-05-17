package com.example.proyecto_turnos_c.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

data class InfoTurn(
    val id_user: String = "",
    val fullName: String = "",
    val expediente: String = "",
    val turnoNumero: String = "",
    val fechaRegistro: String = "",
    val tituloEvento: String = ""
)


data class QueueRegistration(
    val userId: String = "",
    val eventId: String = "",
    val turnNumber: String = "",
    val timestamp: Date = Date(),
    val status: String = "waiting"
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

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    init {
        checkIfUserInQueue()
        getCurrentTurn()
        loadUserProfile()
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

                val existing = db.collection("events")
                    .document(eventId)
                    .collection("registrations")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (!existing.isEmpty) {
                    _userTurn.value = existing.documents.first().getString("turnNumber") ?: ""
                    _isInQueue.value = true
                    return@launch
                }

                val newTurn = db.runTransaction { transaction ->
                    val eventRef = db.collection("events").document(eventId)
                    val eventSnap = transaction.get(eventRef)
                    val last = eventSnap.getLong("lastTurnNumber") ?: 0
                    val next = last + 1
                    val formatted = String.format("%03d", next)

                    transaction.update(eventRef, "lastTurnNumber", next)

                    val reg = QueueRegistration(
                        userId = userId,
                        eventId = eventId,
                        turnNumber = formatted,
                        timestamp = Date(),
                        status = "waiting"
                    )
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

    private fun loadUserProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val profile = document.toObject(UserProfile::class.java)
                        userProfile = profile?.copy(id = currentUser.uid) ?: UserProfile(id = currentUser.uid)
                    } else {
                        userProfile = UserProfile(id = currentUser.uid)
                    }
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Error al cargar el perfil: ${exception.message}"
                    userProfile = UserProfile(id = currentUser.uid)
                }
        }
    }
    fun resetError() {
        _errorMessage.value = null
    }
}

    class EventDetailCardViewModelFactory(private val eventId: String) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventDetailCardViewModel::class.java)) {
                return EventDetailCardViewModel(eventId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
