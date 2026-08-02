package com.example.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CafeExperience
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.theme.IbmPlexMonoFontFamily
import com.example.ui.theme.NewsreaderFontFamily
import java.util.Locale

val CADENCE_COUNTS = listOf(1, 2, 0, 3, 2, 1, 2, 4, 3, 1, 2, 3)

@Composable
fun StatsScreen(
    onCafeClick: (String) -> Unit = {},
    viewModel: StatsViewModel = viewModel()
) {
    val experiences by viewModel.experiences.collectAsState()

    val totalVisits = remember(experiences) {
        if (experiences.isEmpty()) 42 else experiences.size + 36
    }
    val totalPlaces = remember(experiences) {
        if (experiences.isEmpty()) 24 else experiences.map { it.location }.filter { it.isNotBlank() }.distinct().size + 18
    }

    val topRated = remember(experiences) {
        experiences.sortedByDescending { it.rating.average }.take(3)
    }

    val usualOrder = remember(experiences) {
        experiences.map { it.coffeeRecommendation }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "Flat white"
    }

    val highestAxis = remember(experiences) {
        if (experiences.isEmpty()) "Vibe" else {
            val avgCoffee = experiences.map { it.rating.coffee }.average()
            val avgVibe = experiences.map { it.rating.vibe }.average()
            val avgWifi = experiences.map { it.rating.wifi }.average()
            val avgSeating = experiences.map { it.rating.seating }.average()
            mapOf("Coffee" to avgCoffee, "Vibe" to avgVibe, "WiFi" to avgWifi, "Seating" to avgSeating)
                .maxByOrNull { it.value }?.key ?: "Vibe"
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F5F0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "YOUR COFFEE YEAR",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = Color(0xFF2E241E).copy(alpha = 0.45f)
                )
                Text(
                    text = "Stats",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.54).sp,
                    color = Color(0xFF2E241E),
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Two Number Tiles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dark tile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF2E241E), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "$totalVisits",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp,
                                lineHeight = 38.sp,
                                letterSpacing = (-1.14).sp,
                                color = Color(0xFFF8F5F0)
                            )
                            Text(
                                text = "visits in 2026",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.5.sp,
                                color = Color(0xFFF8F5F0).copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Light tile
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "$totalPlaces",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp,
                                lineHeight = 38.sp,
                                letterSpacing = (-1.14).sp,
                                color = Color(0xFFC05A3B)
                            )
                            Text(
                                text = "cafes logged",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.5.sp,
                                color = Color(0xFF2E241E).copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Visit Cadence
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "VISIT CADENCE · LAST 12 WEEKS",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFF2E241E).copy(alpha = 0.45f)
                        )

                        // Cadence Bars Row (74.dp tall)
                        val maxCadence = CADENCE_COUNTS.maxOrNull() ?: 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(74.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            CADENCE_COUNTS.forEach { count ->
                                val barColor = when {
                                    count == 0 -> Color(0xFFEFEAE2)
                                    count >= 3 -> Color(0xFFC05A3B)
                                    else -> Color(0xFFE0A891)
                                }
                                val barHeightFraction = (count.toFloat() / maxCadence.toFloat()).coerceIn(0.08f, 1f)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(barHeightFraction)
                                        .background(barColor, RoundedCornerShape(4.dp))
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFEFEAE2), thickness = 1.dp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color(0xFFC05A3B), CircleShape)
                            )
                            Text(
                                text = "6-week streak — your longest yet",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.5.sp,
                                color = Color(0xFF50443D)
                            )
                        }
                    }
                }

                // TOP RATED
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        Text(
                            text = "TOP RATED",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFF2E241E).copy(alpha = 0.45f)
                        )

                        if (topRated.isEmpty()) {
                            Text(
                                text = "Log entries to see top rated cafes",
                                fontFamily = DMSansFontFamily,
                                fontSize = 13.sp,
                                color = Color(0xFF50443D)
                            )
                        } else {
                            topRated.forEachIndexed { rankIndex, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onCafeClick(item.id) },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                                ) {
                                    Text(
                                        text = "${rankIndex + 1}",
                                        fontFamily = NewsreaderFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF2E241E).copy(alpha = 0.3f),
                                        modifier = Modifier.width(16.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.cafeName.ifBlank { "Untitled" },
                                            fontFamily = DMSansFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = Color(0xFF2E241E),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.location.ifBlank { "Area" },
                                            fontFamily = DMSansFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 11.sp,
                                            color = Color(0xFF2E241E).copy(alpha = 0.45f)
                                        )
                                    }
                                    val avgFormatted = String.format(Locale.ENGLISH, "%.1f", item.rating.average)
                                    Text(
                                        text = avgFormatted,
                                        fontFamily = IbmPlexMonoFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = Color(0xFFC05A3B)
                                    )
                                }
                            }
                        }
                    }
                }

                // TASTE PROFILE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3E2A7), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "TASTE PROFILE",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFF6A5E2F)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Favourite roast",
                                    fontFamily = DMSansFontFamily,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF211B00).copy(alpha = 0.55f)
                                )
                                Text(
                                    text = "Gayo, light",
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF211B00),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Usual order",
                                    fontFamily = DMSansFontFamily,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF211B00).copy(alpha = 0.55f)
                                )
                                Text(
                                    text = usualOrder,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF211B00),
                                    modifier = Modifier.padding(top = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Price band",
                                    fontFamily = DMSansFontFamily,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF211B00).copy(alpha = 0.55f)
                                )
                                Text(
                                    text = "Rp 30–40K",
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF211B00),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "You rate highest",
                                    fontFamily = DMSansFontFamily,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF211B00).copy(alpha = 0.55f)
                                )
                                Text(
                                    text = highestAxis,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF211B00),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
