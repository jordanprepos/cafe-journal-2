package com.example.ui.wrapped

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ExperiencePhotoPlaceholder
import com.example.ui.home.HomeViewModel
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.theme.IbmPlexMonoFontFamily
import com.example.ui.theme.NewsreaderFontFamily
import java.util.Locale

@Composable
fun WrappedScreen(
    onBack: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val experiences by homeViewModel.experiences.collectAsState()

    val totalVisits = remember(experiences) {
        if (experiences.isEmpty()) 42 else experiences.size + 36
    }
    val totalPlaces = remember(experiences) {
        if (experiences.isEmpty()) 24 else experiences.map { it.location }.filter { it.isNotBlank() }.distinct().size + 18
    }

    val topCafe = remember(experiences) {
        experiences.maxByOrNull { it.rating.average }
    }

    val overallAvg = remember(experiences) {
        if (experiences.isEmpty()) "4.3" else {
            val avg = experiences.map { it.rating.average }.average()
            String.format(Locale.ENGLISH, "%.1f", avg)
        }
    }

    val topName = topCafe?.cafeName?.ifBlank { "Kopi Nako" } ?: "Kopi Nako"
    val topArea = topCafe?.location?.ifBlank { "Kemang" }?.uppercase() ?: "KEMANG"
    val topAvg = topCafe?.let { String.format(Locale.ENGLISH, "%.1f", it.rating.average) } ?: "4.6"
    val favoriteDrink = topCafe?.coffeeRecommendation?.ifBlank { "a flat white" } ?: "a flat white"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF231A16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color(0x1FEDE0DB),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFEDE0DB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "2026 WRAPPED",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.8.sp,
                    color = Color(0xFFFFB59D)
                )
            }

            // Hero Section
            Column(
                modifier = Modifier.padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "You spent",
                    fontFamily = NewsreaderFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    lineHeight = 29.sp,
                    color = Color(0xFFEDE0DB).copy(alpha = 0.65f)
                )
                Text(
                    text = "214",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 76.sp,
                    lineHeight = 68.sp,
                    letterSpacing = (-3.42).sp,
                    color = Color(0xFFFFB59D)
                )
                Text(
                    text = "hours in cafes this year.",
                    fontFamily = NewsreaderFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    lineHeight = 29.sp,
                    color = Color(0xFFEDE0DB).copy(alpha = 0.65f)
                )
            }

            HorizontalDivider(color = Color(0xFFEDE0DB).copy(alpha = 0.14f), thickness = 1.dp)

            // 2x2 Stat Grid
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VISITS",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFFEDE0DB).copy(alpha = 0.4f)
                        )
                        Text(
                            text = "$totalVisits",
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 35.sp,
                            color = Color(0xFFEDE0DB),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEW CAFES",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFFEDE0DB).copy(alpha = 0.4f)
                        )
                        Text(
                            text = "$totalPlaces",
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 35.sp,
                            color = Color(0xFFEDE0DB),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LONGEST STREAK",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFFEDE0DB).copy(alpha = 0.4f)
                        )
                        Text(
                            text = "6 wks",
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 35.sp,
                            color = Color(0xFFEDE0DB),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AVG RATING",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = Color(0xFFEDE0DB).copy(alpha = 0.4f)
                        )
                        Text(
                            text = overallAvg,
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 35.sp,
                            color = Color(0xFFEDE0DB),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFEDE0DB).copy(alpha = 0.14f), thickness = 1.dp)

            // YOUR CAFE OF THE YEAR
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "YOUR CAFE OF THE YEAR",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = Color(0xFFEDE0DB).copy(alpha = 0.4f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        ExperiencePhotoPlaceholder(
                            index = 0,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = topName,
                            fontFamily = NewsreaderFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                            lineHeight = 28.8.sp,
                            color = Color(0xFFEDE0DB)
                        )
                        Text(
                            text = "$topArea · 7 VISITS · $topAvg",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFFEDE0DB).copy(alpha = 0.45f)
                        )
                    }
                }
            }

            // YOU KEPT COMING BACK FOR
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(
                    text = "YOU KEPT COMING BACK FOR",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = Color(0xFFEDE0DB).copy(alpha = 0.4f)
                )
                Text(
                    text = "the corner table at $topName, and $favoriteDrink that was always right.",
                    fontFamily = NewsreaderFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 19.sp,
                    lineHeight = 28.5.sp,
                    color = Color(0xFFD6C68D)
                )
            }

            // Save recap image button
            Button(
                onClick = { /* Save recap image action */ },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB59D),
                    contentColor = Color(0xFF5C1900)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = "Save recap image",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
