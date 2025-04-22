package com.example.proyecto_turnos_c.models
//
//import com.google.firebase.Timestamp
//import java.util.Date
//
//data class Event(
//    val id: String = "",
//    val title: String = "",
//    val imageUrl: String = "",
//    val date: Date = Date(),
//    val startTime: String = "",
//    val endTime: String = "",
//    val location: String = "",
//    val description: String = "",
//    val createdAt: Timestamp = Timestamp.now()
//) {

//    companion object {
//        fun fromMap(id: String, data: Map<String, Any>): Event {
//            return Event(
//                id = id,
//                title = data["title"] as? String ?: "",
//                imageUrl = data["imageUrl"] as? String ?: "",
//                date = (data["date"] as? Timestamp)?.toDate() ?: Date(),
//                startTime = data["startTime"] as? String ?: "",
//                endTime = data["endTime"] as? String ?: "",
//                location = data["location"] as? String ?: "",
//                description = data["description"] as? String ?: "",
//                createdAt = data["createdAt"] as? Timestamp ?: Timestamp.now()
//            )
//        }
//    }
//
//    fun toMap(): Map<String, Any> {
//        return hashMapOf(
//            "title" to title,
//            "imageUrl" to imageUrl,
//            "date" to Timestamp(date),
//            "startTime" to startTime,
//            "endTime" to endTime,
//            "location" to location,
//            "description" to description,
//            "createdAt" to createdAt
//        )
//    }
//}