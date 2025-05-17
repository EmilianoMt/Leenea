package com.example.proyecto_turnos_c.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class QRCodeData(
    val userId: String = "",
    val fullName: String = "",
    val expediente: String = "",
    val turno: String = "",
    val fecha: String = "",
    val evento: String = ""
)

data class NextTurnInfo(
    val turnNumber: String = "",
    val userName: String = "",
    val userId: String = "",
    val expediente: String = ""
)

class AdminEventDetailCardViewModel(private val eventId: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val tag = "AdminEventDetailVM"

    // Estado para el turno actual
    private val _currentTurn = MutableStateFlow("")
    val currentTurn: StateFlow<String> = _currentTurn

    // Estado para el siguiente turno (incluye número y nombre de usuario)
    private val _nextTurnInfo = MutableStateFlow(NextTurnInfo())
    val nextTurnInfo: StateFlow<NextTurnInfo> = _nextTurnInfo

    // Estado para mostrar mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado para indicar si se está procesando una operación
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para el resultado del escaneo QR
    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult

    // Estado para la información del usuario escaneado
    private val _scannedUserInfo = MutableStateFlow<UserProfile?>(null)
    val scannedUserInfo: StateFlow<UserProfile?> = _scannedUserInfo

    // Estado para el permiso de la cámara
    private val _cameraPermissionGranted = MutableStateFlow(false)
    val cameraPermissionGranted: StateFlow<Boolean> = _cameraPermissionGranted

    init {
        getCurrentTurn()
        getNextTurnInfo()
    }

    private fun parseQRContent(qrContent: String): QRCodeData {
        val lines = qrContent.trim().lines()
        var userId = ""
        var fullName = ""
        var expediente = ""
        var turno = ""
        var fecha = ""
        var evento = ""

        if (lines.size > 1) {
            for (line in lines) {
                when {
                    line.contains("ID:", ignoreCase = true) ||
                            line.contains("ID de usuario:", ignoreCase = true) -> {
                        userId = line.substringAfter(':').trim()
                    }
                    line.contains("Nombre:", ignoreCase = true) -> {
                        fullName = line.substringAfter(':').trim()
                    }
                    line.contains("Expediente:", ignoreCase = true) -> {
                        expediente = line.substringAfter(':').trim()
                    }
                    line.contains("Turno:", ignoreCase = true) -> {
                        turno = line.substringAfter(':').trim()
                    }
                    line.contains("Fecha:", ignoreCase = true) -> {
                        fecha = line.substringAfter(':').trim()
                    }
                    line.contains("Evento:", ignoreCase = true) -> {
                        evento = line.substringAfter(':').trim()
                    }
                }
            }
        } else if (!qrContent.contains(":")) {
            userId = qrContent.trim()
        }

        return QRCodeData(
            userId = userId,
            fullName = fullName,
            expediente = expediente,
            turno = turno,
            fecha = fecha,
            evento = evento
        )
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

                    // Cuando cambie el turno actual, actualizar información del siguiente turno
                    getNextTurnInfo()
                }
            }
    }

    private fun getNextTurnInfo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener el número del siguiente turno (turno actual + 1)
                val currentTurnNumber = _currentTurn.value.toIntOrNull() ?: 0
                val nextTurnNumber = currentTurnNumber + 1
                val formattedNextTurn = String.format("%03d", nextTurnNumber)

                // Buscar la información del usuario que tiene ese turno
                val registrationSnapshot = db.collection("events")
                    .document(eventId)
                    .collection("registrations")
                    .whereEqualTo("turnNumber", formattedNextTurn)
                    .whereEqualTo("status", "waiting") // Solo turnos en espera
                    .limit(1)
                    .get()
                    .await()

                if (registrationSnapshot.isEmpty) {
                    _nextTurnInfo.value = NextTurnInfo(
                        turnNumber = formattedNextTurn,
                        userName = "No encontrado",
                        userId = "",
                        expediente = ""
                    )
                    return@launch
                }

                // Obtener el ID de usuario del registro
                val registration = registrationSnapshot.documents.first()
                val userId = registration.getString("userId") ?: ""

                // Buscar la información del usuario en la colección de usuarios
                if (userId.isNotEmpty()) {
                    val userDoc = db.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    if (userDoc.exists()) {
                        val userName = userDoc.getString("fullName") ?: "Usuario"
                        val expediente = userDoc.getString("expediente") ?: ""
                        _nextTurnInfo.value = NextTurnInfo(
                            turnNumber = formattedNextTurn,
                            userName = userName,
                            userId = userId,
                            expediente = expediente
                        )
                    } else {
                        _nextTurnInfo.value = NextTurnInfo(
                            turnNumber = formattedNextTurn,
                            userName = "Usuario desconocido",
                            userId = userId,
                            expediente = ""
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error al obtener información del siguiente turno", e)
                _errorMessage.value = "Error al obtener información del siguiente turno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun advanceToNextTurn() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener el próximo número de turno
                val nextTurn = _nextTurnInfo.value.turnNumber

                // Actualizar el turno actual en el evento
                db.collection("events")
                    .document(eventId)
                    .update("currentTurn", nextTurn)
                    .await()

                // La actualización del listener se encargará de actualizar los estados
            } catch (e: Exception) {
                Log.e(tag, "Error al avanzar al siguiente turno", e)
                _errorMessage.value = "Error al avanzar al siguiente turno: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun handleQRScanResult(result: String) {
        _scanResult.value = result
        handleQRScanR(result)
    }

    fun handleQRScanR(result: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Parsear el contenido del QR
                val qrData = parseQRContent(result)
                Log.d(tag, "QR parseado: $qrData")

                // Usuario ID a buscar
                val userIdToSearch = if (qrData.userId.isNotEmpty()) qrData.userId else result.trim()

                Log.d(tag, "Buscando usuario con ID: $userIdToSearch")

                // Verificar si el usuario existe
                val userDoc = db.collection("users")
                    .document(userIdToSearch)
                    .get()
                    .await()

                if (userDoc.exists()) {
                    // Obtener datos del usuario de Firestore
                    val firestoreFullName = userDoc.getString("fullName") ?: "Usuario"
                    val firestoreEmail = userDoc.getString("email") ?: ""
                    val firestoreExpediente = userDoc.getString("expediente") ?: ""

                    // Combinamos datos del QR con datos de Firestore, priorizando Firestore
                    val profile = UserProfile(
                        id = userIdToSearch,
                        fullName = firestoreFullName,
                        email = firestoreEmail,
                        expediente = firestoreExpediente
                    )

                    // Actualizar los valores para mostrar solo el diálogo de usuario encontrado
                    _scanResult.value = userIdToSearch
                    _scannedUserInfo.value = profile
                    Log.d(tag, "Usuario encontrado: ${profile.fullName}")

                    // Verificar si este usuario tiene un turno en el evento
                    val registrationSnapshot = db.collection("events")
                        .document(eventId)
                        .collection("registrations")
                        .whereEqualTo("userId", userIdToSearch)
                        .get()
                        .await()

                    if (!registrationSnapshot.isEmpty) {
                        val registration = registrationSnapshot.documents.first()
                        val userTurn = registration.getString("turnNumber") ?: ""
                        val userStatus = registration.getString("status") ?: "waiting"

                        // Realizar acciones basadas en el turno y estado del usuario
                        when {
                            userTurn == _currentTurn.value -> {
                                // El usuario tiene el turno actual
                                // Marcar como atendido
                                registration.reference.update("status", "attended")
                                _errorMessage.value = "Usuario con turno actual identificado correctamente"
                            }
                            userStatus == "attended" -> {
                                _errorMessage.value = "Este usuario ya fue atendido"
                            }
                            else -> {
                                _errorMessage.value = "El usuario tiene el turno $userTurn, no el actual"
                            }
                        }
                    } else {
                        _errorMessage.value = "El usuario no tiene turno en este evento"
                    }
                } else {
                    _errorMessage.value = "Usuario no encontrado"
                }
            } catch (e: Exception) {
                Log.e(tag, "Error al procesar datos del QR", e)
                _errorMessage.value = "Error al procesar el código QR: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetScanResult() {
        _scanResult.value = null
        _scannedUserInfo.value = null
    }

    fun resetError() {
        _errorMessage.value = null
    }
}

class AdminEventDetailViewModelFactory(private val eventId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminEventDetailCardViewModel::class.java)) {
            return AdminEventDetailCardViewModel(eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}