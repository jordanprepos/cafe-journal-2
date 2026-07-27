package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object MapUtils {
    fun openGoogleMaps(context: Context, cafeName: String = "", location: String = "") {
        val query = when {
            cafeName.isNotBlank() && location.isNotBlank() -> "$cafeName, $location"
            cafeName.isNotBlank() -> cafeName
            else -> location
        }
        if (query.isBlank()) return

        val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query))
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        
        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Fallback to web search in browser or alternative maps app
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            try {
                context.startActivity(webIntent)
            } catch (_: Exception) {
                // Ignore if no app available to handle web view
            }
        }
    }
}
