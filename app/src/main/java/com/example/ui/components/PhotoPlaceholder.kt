package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

val PhotoTintColors = listOf(
    Color(0xFFE4D6CB),
    Color(0xFFD9DCCF),
    Color(0xFFEADFC8),
    Color(0xFFDCD3CB),
    Color(0xFFE2D9D2),
    Color(0xFFD5DAD3)
)

@Composable
fun ExperiencePhotoPlaceholder(
    index: Int = 0,
    modifier: Modifier = Modifier
) {
    val bgColor = PhotoTintColors[index % PhotoTintColors.size]
    Box(
        modifier = modifier.background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stripeWidth = 5f
            val stripeGap = 10f
            val strokeColor = Color(0x1C2E241E)
            val strokeWidth = 3f

            val totalWidth = size.width + size.height
            var x = -size.height
            while (x < totalWidth) {
                drawLine(
                    color = strokeColor,
                    start = Offset(x, 0f),
                    end = Offset(x + size.height, size.height),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Square
                )
                x += stripeGap
            }
        }
    }
}
