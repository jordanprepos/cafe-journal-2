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
            val cafeName = doc.getString("cafeName") ?: ""
            val location = doc.getString("location") ?: ""
            val coffeeRecommendation = doc.getString("coffeeRecommendation") ?: ""
            val priceRange = doc.getString("priceRange") ?: ""
            val facilitiesTags = (doc.get("facilitiesTags") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            val notes = doc.getString("notes") ?: ""
            val photoUri = doc.getString("photoUri") ?: ""
            val photoCaptionsRaw = doc.get("photoCaptions") as? Map<*, *>
            val photoCaptions = photoCaptionsRaw?.mapNotNull { (k, v) ->
                if (k != null && v != null) k.toString() to v.toString() else null
            }?.toMap() ?: emptyMap()
            val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()

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
}
