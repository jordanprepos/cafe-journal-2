package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CafeExperience(
    @DocumentId
    val id: String = "",
    val cafeName: String = "",
    val location: String = "",
    val rating: Float = 0f,
    val coffeeRecommendation: String = "",
    val priceRange: String = "",
    val facilitiesTags: List<String> = emptyList(),
    val notes: String = "",
    val photoUri: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
