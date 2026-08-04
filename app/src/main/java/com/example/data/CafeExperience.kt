package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class CafeRating(
    val coffee: Float = 0f,
    val vibe: Float = 0f,
    val wifi: Float = 0f,
    val seating: Float = 0f
) {
    @get:Exclude
    val average: Float
        get() = (coffee + vibe + wifi + seating) / 4f
}

data class CafeExperience(
    @DocumentId
    val id: String = "",
    val cafeName: String = "",
    val location: String = "",
    val rating: CafeRating = CafeRating(),
    val coffeeRecommendation: String = "",
    val priceRange: String = "",
    val facilitiesTags: List<String> = emptyList(),
    val notes: String = "",
    val photoUri: String = "",
    val photoCaptions: Map<String, String> = emptyMap(),
    val timestamp: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromDocument(doc: DocumentSnapshot): CafeExperience? {
            if (!doc.exists()) return null
            val id = doc.id
            val cafeName = doc.getString("name") ?: doc.getString("cafeName") ?: ""
            val location = doc.getString("address") ?: doc.getString("location") ?: ""
            val coffeeRecommendation = doc.getString("favorite_drink") ?: doc.getString("coffeeRecommendation") ?: ""
            val priceRange = doc.getString("priceRange") ?: ""
            val facilitiesTags = (doc.get("tags") as? List<*>)?.mapNotNull { it?.toString() } 
                ?: (doc.get("facilitiesTags") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            val notes = doc.getString("notes") ?: ""
            
            val photosList = doc.get("photos") as? List<*>
            val photoUri = photosList?.firstOrNull()?.toString() ?: doc.getString("photoUri") ?: ""
            
            val photoCaptionsRaw = doc.get("photoCaptions") as? Map<*, *>
            val photoCaptions = photoCaptionsRaw?.mapNotNull { (k, v) ->
                if (k != null && v != null) k.toString() to v.toString() else null
            }?.toMap() ?: emptyMap()
            
            val timestamp = doc.getTimestamp("created_at") ?: doc.getTimestamp("timestamp") ?: Timestamp.now()

            val ratingRaw = doc.get("rating")
            val rating = when (ratingRaw) {
                is Map<*, *> -> {
                    val coffee = (ratingRaw["coffee"] as? Number)?.toFloat() ?: 0f
                    val vibe = (ratingRaw["vibe"] as? Number)?.toFloat() ?: 0f
                    val wifi = (ratingRaw["wifi"] as? Number)?.toFloat() ?: 0f
                    val seating = (ratingRaw["seating"] as? Number)?.toFloat() ?: 0f
                    CafeRating(coffee, vibe, wifi, seating)
                }
                is Number -> {
                    val valFloat = ratingRaw.toFloat()
                    CafeRating(valFloat, valFloat, valFloat, valFloat)
                }
                else -> CafeRating()
            }

            return CafeExperience(
                id = id,
                cafeName = cafeName,
                location = location,
                rating = rating,
                coffeeRecommendation = coffeeRecommendation,
                priceRange = priceRange,
                facilitiesTags = facilitiesTags,
                notes = notes,
                photoUri = photoUri,
                photoCaptions = photoCaptions,
                timestamp = timestamp
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        val isoDate = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(timestamp.toDate())
        
        val safeName = if (cafeName.isNotBlank()) cafeName.take(200) else "Unknown"

        return mapOf(
            "name" to safeName,
            "created_at" to timestamp,
            "photos" to if (photoUri.isNotBlank()) listOf(photoUri) else emptyList<String>(),
            "location_link" to location,
            "address" to location,
            "notes" to notes.take(20000),
            "rating" to rating.average.toInt().coerceIn(0, 5),
            "favorite_drink" to coffeeRecommendation,
            "visited_date" to isoDate,
            "tags" to facilitiesTags.take(20),
            "photoCaptions" to photoCaptions
        )
    }
}
