package com.example.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CafeExperience
import com.example.data.ThemeRepository
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
    val context = LocalContext.current
    val themeRepository = remember { ThemeRepository(context) }
    val isDarkModeState by themeRepository.isDarkMode.collectAsState(initial = null)
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

    val bgColor = if (isDark) Color(0xFF231A16) else Color(0xFFF8F5F0)
    val cardBgColor = if (isDark) Color(0xFF2C221D) else Color.White
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.45f)
    val dividerColor = if (isDark) Color(0xFF3D322B) else Color(0xFFEFEAE2)

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
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "YOUR COFFEE YEAR",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = subtextColor
                )
                Text(
                    text = "Stats",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.54).sp,
                    color = textColor,
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
                            .background(if (isDark) Color(0xFF1B1310) else Color(0xFF2E241E), RoundedCornerShape(18.dp))
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
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
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
                                color = subtextColor
                            )
                        }
                    }
                }

                // Visit Cadence
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
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
                            color = subtextColor
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
                                    count == 0 -> dividerColor
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

                        HorizontalDivider(color = dividerColor, thickness = 1.dp)

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
                                color = if (isDark) Color(0xFFEDE0DB) else Color(0xFF50443D)
                            )
                        }
                    }
                }

                // TOP RATED
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
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
                            color = subtextColor
                        )

                        if (topRated.isEmpty()) {
                            Text(
                                text = "Log entries to see top rated cafes",
                                fontFamily = DMSansFontFamily,
                                fontSize = 13.sp,
                                color = subtextColor
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
                                        color = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.35f) else Color(0xFF2E241E).copy(alpha = 0.3f),
                                        modifier = Modifier.width(16.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.cafeName.ifBlank { "Untitled" },
                                            fontFamily = DMSansFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = textColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.location.ifBlank { "Area" },
                                            fontFamily = DMSansFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 11.sp,
                                            color = subtextColor
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
                val tasteBg = if (isDark) Color(0xFF423512) else Color(0xFFF3E2A7)
                val tasteLabelColor = if (isDark) Color(0xFFF3E2A7) else Color(0xFF6A5E2F)
                val tasteSubColor = if (isDark) Color(0xFFEBD9A3).copy(alpha = 0.7f) else Color(0xFF211B00).copy(alpha = 0.55f)
                val tasteValColor = if (isDark) Color(0xFFFFF7DB) else Color(0xFF211B00)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tasteBg, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "TASTE PROFILE",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = tasteLabelColor
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
                                    color = tasteSubColor
                                )
                                Text(
                                    text = "Gayo, light",
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = tasteValColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Usual order",
                                    fontFamily = DMSansFontFamily,
                                    fontSize = 10.5.sp,
                                    color = tasteSubColor
                                )
                                Text(
                                    text = usualOrder,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = tasteValColor,
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
                                    color = tasteSubColor
                                )
                                Text(
                                    text = "Rp 30–40K",
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = tasteValColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "You rate highest",
                                    fontFamily = DMSansFontFamily,
                                    fontSize = 10.5.sp,
                                    color = tasteSubColor
                                )
                                Text(
                                    text = highestAxis,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = tasteValColor,
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
