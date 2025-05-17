package com.example.proyecto_turnos_c.ui.screens.adminAddEvent

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.util.UUID

data class EventFormState(
    val id: String = "",
    val title: String = "",
    val imageUri: Uri? = null,
    val date: Long? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val location: String = "",
    val description: String = ""
)

class AdminAddEventsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventFormState())
    val uiState: StateFlow<EventFormState> = _uiState.asStateFlow()

    private val _eventCreationState = MutableStateFlow<EventCreationState>(EventCreationState.Idle)
    val eventCreationState: StateFlow<EventCreationState> = _eventCreationState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ID único al crear el ViewModel
    init {
        _uiState.value = _uiState.value.copy(id = UUID.randomUUID().toString())
    }

    // Actualización de campos individuales
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun updateDate(date: Long?) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun updateStartTime(time: LocalTime?) {
        _uiState.value = _uiState.value.copy(startTime = time)
    }

    fun updateEndTime(time: LocalTime?) {
        _uiState.value = _uiState.value.copy(endTime = time)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }


//    fun updateId(id: String) {
//        _uiState.value = _uiState.value.copy(id = id)
//    }


    fun validateForm(): Boolean {
        val currentState = _uiState.value

        return currentState.title.isNotBlank() &&
                currentState.imageUri != null &&
                currentState.date != null &&
                currentState.startTime != null &&
                currentState.endTime != null &&
                currentState.location.isNotBlank() &&
                currentState.description.isNotBlank()
    }

    // Crear un evento
    fun createEvent(context: Context) {
        if (!validateForm()) {
            _eventCreationState.value = EventCreationState.Error("Por favor complete todos los campos")
            return
        }

        viewModelScope.launch {
            try {
                _eventCreationState.value = EventCreationState.Loading

                val currentState = _uiState.value

                // Subir la imagen al Storage
                val imageUrl = uploadImage(currentState.imageUri!!, context)

                // Crear el documento del evento en Firestore
                val eventId = saveEventToFirestore(imageUrl)

                // Éxito el evento se creo
                _eventCreationState.value = EventCreationState.Success(eventId)


            } catch (e: Exception) {
                _eventCreationState.value = EventCreationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // Subir imagen a storage
    private suspend fun uploadImage(imageUri: Uri, context: Context): String {
        // Referencia para la imagen
        val storageRef = storage.reference.child("events/${UUID.randomUUID()}")

        return try {
            // Subir la imagen
            val uploadTask = storageRef.putFile(imageUri).await()

            // Obtener la URL
            val downloadUrl = storageRef.downloadUrl.await()

            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("AdminAddEventsViewModel", "Error uploading image", e)
            throw Exception("Error al subir la imagen: ${e.message}")
        }
    }

    // Guardar evento en Firestore
    private suspend fun saveEventToFirestore(imageUrl: String): String {
        val currentState = _uiState.value

        // Mapa con los datos del evento
        val eventData = hashMapOf(
            "id" to currentState.id,
            "title" to currentState.title,
            "imageUrl" to imageUrl,
            "date" to Timestamp(java.util.Date(currentState.date!!)),
            "startTime" to currentState.startTime.toString(),
            "endTime" to currentState.endTime.toString(),
            "location" to currentState.location,
            "description" to currentState.description,
            "createdAt" to Timestamp.now(),
            "isAvailable" to true
        )

        // Guardar en Firestore
        return try {
            db.collection("events").document(currentState.id).set(eventData).await()
            currentState.id
        } catch (e: Exception) {
            Log.e("AdminAddEventsViewModel", "Error saving event to Firestore", e)
            throw Exception("Error al guardar el evento: ${e.message}")
        }
    }

    // Reiniciar el formulario
    fun resetForm() {
        Log.d("AdminAddEventsViewModel", "Reseteando formulario")
        _uiState.value = EventFormState(id = UUID.randomUUID().toString())
        _eventCreationState.value = EventCreationState.Idle
    }

//    // Funcion para actualizar
//    fun loadEventById(eventId: String) {
//        viewModelScope.launch {
//            try {
//                val eventDoc = db.collection("events").document(eventId).get().await()
//                if (eventDoc.exists()) {
//                    val eventData = eventDoc.data ?: return@launch
//
//                    // Extraer los datos del documento
//                    val title = eventData["title"] as? String ?: ""
//                    val location = eventData["location"] as? String ?: ""
//                    val description = eventData["description"] as? String ?: ""
//                    val date = (eventData["date"] as? Timestamp)?.toDate()?.time
//
//                    // Convertir strings a LocalTime
//                    val startTimeStr = eventData["startTime"] as? String
//                    val endTimeStr = eventData["endTime"] as? String
//
//                    val startTime = startTimeStr?.let { LocalTime.parse(it) }
//                    val endTime = endTimeStr?.let { LocalTime.parse(it) }
//
//                    // Actualizar el estado con los datos cargados
//                    _uiState.value = _uiState.value.copy(
//                        id = eventId,
//                        title = title,
//                        location = location,
//                        description = description,
//                        date = date,
//                        startTime = startTime,
//                        endTime = endTime
//                    )
//                }
//            } catch (e: Exception) {
//                Log.e("AdminAddEventsViewModel", "Error loading event", e)
//                _eventCreationState.value = EventCreationState.Error("Error al cargar el evento: ${e.message}")
//            }
//        }
//    }
}



// Estados posibles del evento
sealed class EventCreationState {
    object Idle : EventCreationState()
    object Loading : EventCreationState()
    data class Success(val eventId: String) : EventCreationState()
    data class Error(val message: String) : EventCreationState()
}