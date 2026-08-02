package com.example.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.CafeExperience
import com.example.data.ThemeRepository
import com.example.ui.components.ExperiencePhotoPlaceholder
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.theme.IbmPlexMonoFontFamily
import com.example.ui.theme.NewsreaderFontFamily
import com.example.util.MapUtils
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CafeDetailScreen(
    cafeId: String,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit = {},
    viewModel: CafeDetailViewModel = viewModel(factory = CafeDetailViewModelFactory(cafeId))
) {
    val experience by viewModel.experience.collectAsState()
    val context = LocalContext.current
    val themeRepository = remember { ThemeRepository(context) }
    val isDarkModeState by themeRepository.isDarkMode.collectAsState(initial = null)
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

    val bgColor = if (isDark) Color(0xFF231A16) else Color(0xFFF8F5F0)
    val cardBgColor = if (isDark) Color(0xFF2C221D) else Color.White
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.5f)
    val boxBgColor = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)
    val tagTextColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF50443D)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        val exp = experience
        if (exp == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFC05A3B))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Photo (252.dp tall)
                val detailPhotoList = remember(exp.photoUri) {
                    exp.photoUri.split(",").map { it.trim() }.filter { it.isNotBlank() }
                }
                val primaryPhoto = detailPhotoList.firstOrNull() ?: ""

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(252.dp)
                ) {
                    if (primaryPhoto.isNotBlank()) {
                        AsyncImage(
                            model = primaryPhoto,
                            contentDescription = exp.cafeName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ExperiencePhotoPlaceholder(
                            index = exp.cafeName.hashCode(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Back Button (top-left)
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = if (isDark) Color(0xCC231A16) else Color(0xEBF8F5F0),
                        modifier = Modifier
                            .padding(top = 12.dp, start = 16.dp)
                            .size(36.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Caption Chip (bottom-left)
                    val captionText = "PHOTO — ${exp.coffeeRecommendation.ifBlank { exp.cafeName }}"
                    Surface(
                        color = if (isDark) Color(0xCC2C221D) else Color(0xD9F8F5F0),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = captionText,
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.sp,
                            letterSpacing = 0.9.sp,
                            color = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.7f) else Color(0xFF2E241E).copy(alpha = 0.55f),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name + Metadata strip
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = exp.cafeName.ifBlank { "Untitled Café" },
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            lineHeight = 29.sp,
                            letterSpacing = (-0.5).sp,
                            color = textColor
                        )

                        val formattedDate = remember(exp.timestamp) {
                            val sdf = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                            sdf.format(exp.timestamp.toDate())
                        }
                        val area = exp.location.ifBlank { "AREA" }.uppercase()
                        val price = exp.priceRange.ifBlank { "—" }.uppercase()
                        Text(
                            text = "$area · ${formattedDate.uppercase()} · $price",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.5.sp,
                            letterSpacing = 0.5.sp,
                            color = subtextColor
                        )
                    }

                    // Rating Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "RATING",
                                    fontFamily = IbmPlexMonoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.4.sp,
                                    color = subtextColor
                                )
                                val avgFormatted = String.format(Locale.ENGLISH, "%.1f", exp.rating.average)
                                Text(
                                    text = avgFormatted,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color(0xFFC05A3B)
                                )
                            }

                            DetailRatingRow("Coffee", exp.rating.coffee, isDark)
                            DetailRatingRow("Vibe", exp.rating.vibe, isDark)
                            DetailRatingRow("WiFi", exp.rating.wifi, isDark)
                            DetailRatingRow("Seating", exp.rating.seating, isDark)
                        }
                    }

                    // THE ORDER
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "THE ORDER",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = subtextColor
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Recommended
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(boxBgColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 13.dp, vertical = 11.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Recommended",
                                        fontFamily = DMSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp,
                                        color = subtextColor
                                    )
                                    Text(
                                        text = exp.coffeeRecommendation.ifBlank { "Not noted" },
                                        fontFamily = DMSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = textColor,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            // Price
                            Box(
                                modifier = Modifier
                                    .background(boxBgColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 13.dp, vertical = 11.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Price",
                                        fontFamily = DMSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp,
                                        color = subtextColor
                                    )
                                    Text(
                                        text = exp.priceRange.ifBlank { "—" },
                                        fontFamily = DMSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = textColor,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // NOTES
                    if (exp.notes.isNotBlank()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "NOTES",
                                fontFamily = IbmPlexMonoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 1.4.sp,
                                color = subtextColor
                            )
                            Text(
                                text = exp.notes,
                                fontFamily = NewsreaderFontFamily,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.5.sp,
                                lineHeight = 25.sp,
                                color = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.9f) else Color(0xFF2E241E).copy(alpha = 0.85f)
                            )
                        }
                    }

                    // PHOTO GALLERY
                    if (detailPhotoList.size > 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "PHOTOS (${detailPhotoList.size})",
                                fontFamily = IbmPlexMonoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 1.4.sp,
                                color = subtextColor
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                detailPhotoList.forEachIndexed { idx, pUri ->
                                    Box(
                                        modifier = Modifier
                                            .size(110.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(boxBgColor)
                                    ) {
                                        AsyncImage(
                                            model = pUri,
                                            contentDescription = "Cafe Photo ${idx + 1}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Tags
                    if (exp.facilitiesTags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            exp.facilitiesTags.forEach { tag ->
                                Surface(
                                    color = boxBgColor,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontFamily = DMSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 11.5.sp,
                                        color = tagTextColor,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Actions Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    ) {
                        // Edit entry button
                        OutlinedButton(
                            onClick = { onEditClick(exp.id) },
                            shape = RoundedCornerShape(13.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC05A3B)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC05A3B)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "Edit entry",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }

                        // Open in Maps button
                        Button(
                            onClick = {
                                MapUtils.openGoogleMaps(context, exp.cafeName, exp.location)
                            },
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC05A3B),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "Open in Maps",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRatingRow(label: String, value: Float, isDark: Boolean = false) {
    val labelColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF50443D)
    val trackBg = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)
    val valColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.7f) else Color(0xFF2E241E).copy(alpha = 0.6f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontFamily = DMSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = labelColor,
            modifier = Modifier.width(58.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .background(trackBg, RoundedCornerShape(3.dp))
        ) {
            val fraction = (value / 5f).coerceIn(0f, 1f)
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .background(Color(0xFFC05A3B), RoundedCornerShape(3.dp))
                )
            }
        }
        val valFormatted = String.format(Locale.ENGLISH, "%.1f", value)
        Text(
            text = valFormatted,
            fontFamily = IbmPlexMonoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = valColor,
            modifier = Modifier.width(22.dp)
        )
    }
}
