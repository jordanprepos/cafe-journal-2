package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.R
import com.example.data.CafeExperience
import com.example.ui.components.ExperiencePhotoPlaceholder
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.theme.IbmPlexMonoFontFamily
import com.example.ui.theme.NewsreaderFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onCafeClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val experiences by viewModel.experiences.collectAsState()
    val journalView by viewModel.journalView.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isDarkModeState by viewModel.isDarkMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

    val filteredExperiences = remember(experiences, selectedFilter) {
        if (selectedFilter == "All" || selectedFilter.startsWith("All ")) {
            experiences
        } else {
            experiences.filter { exp -> exp.facilitiesTags.contains(selectedFilter) }
        }
    }

    val isAlbum = journalView == "album"
    val backgroundColor = if (isDark) Color(0xFF231A16) else if (isAlbum) Color(0xFFF3EEE6) else Color(0xFFF8F5F0)
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.45f)
    val controlBgColor = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFFC05A3B),
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 4.dp)
                    .size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Entry",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "${experiences.size} VISITS · 2026",
                        fontFamily = IbmPlexMonoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        color = subtextColor,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = if (isDark) R.drawable.cafe_journal_logo_dark_theme else R.drawable.cafe_journal_logo_light_theme),
                            contentDescription = "Journal Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = "Journal",
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 27.sp,
                            lineHeight = 30.sp,
                            letterSpacing = (-0.54).sp,
                            color = textColor
                        )
                    }
                }

                // Top Controls (Theme Toggle & View Toggle)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dark/Light Theme Toggle Button
                    Surface(
                        onClick = { viewModel.setDarkMode(!isDark) },
                        shape = RoundedCornerShape(11.dp),
                        color = controlBgColor,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                                tint = if (isDark) Color(0xFFFFC107) else Color(0xFF2E241E),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isDark) "Dark" else "Light",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = textColor
                            )
                        }
                    }

                    // View Toggle
                    Row(
                        modifier = Modifier
                            .background(controlBgColor, RoundedCornerShape(11.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Grid Segment
                        ViewToggleSegment(
                            isSelected = !isAlbum,
                            isDark = isDark,
                            onClick = { viewModel.setJournalView("grid") }
                        ) {
                            // Grid Icon (2x2)
                            Column(verticalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                    Box(modifier = Modifier.size(6.dp).background(if (!isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                                    Box(modifier = Modifier.size(6.dp).background(if (!isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                    Box(modifier = Modifier.size(6.dp).background(if (!isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                                    Box(modifier = Modifier.size(6.dp).background(if (!isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                                }
                            }
                        }

                        // Album Segment
                        ViewToggleSegment(
                            isSelected = isAlbum,
                            isDark = isDark,
                            onClick = { viewModel.setJournalView("album") }
                        ) {
                            // Album Icon (3 stacked bars)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.size(width = 15.dp, height = 4.dp).background(if (isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                                Box(modifier = Modifier.size(width = 15.dp, height = 4.dp).background(if (isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                                Box(modifier = Modifier.size(width = 15.dp, height = 4.dp).background(if (isAlbum) textColor else textColor.copy(alpha = 0.35f), RoundedCornerShape(1.5.dp)))
                            }
                        }
                    }
                }
            }

            // Filter Chips Row
            val filters = listOf("All", "Laptop Friendly", "Open Late", "Outdoor Seating", "Halal")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = (filter == "All" && (selectedFilter == "All" || selectedFilter.startsWith("All"))) || (filter == selectedFilter)
                    val label = if (filter == "All") "All ${experiences.size}" else filter
                    Surface(
                        onClick = { viewModel.setFilter(filter) },
                        shape = RoundedCornerShape(999.dp),
                        color = if (isSelected) (if (isDark) Color(0xFFC05A3B) else Color(0xFF2E241E)) else controlBgColor,
                        contentColor = if (isSelected) Color.White else (if (isDark) Color(0xFFEDE0DB) else Color(0xFF50443D))
                    ) {
                        Text(
                            text = label,
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.5.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Feed
            if (filteredExperiences.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No journal entries found.",
                        fontFamily = DMSansFontFamily,
                        color = subtextColor
                    )
                }
            } else {
                Crossfade(targetState = isAlbum, label = "FeedCrossfade") { albumMode ->
                    if (albumMode) {
                        AlbumFeed(
                            experiences = filteredExperiences,
                            isDark = isDark,
                            onCafeClick = onCafeClick
                        )
                    } else {
                        GridFeed(
                            experiences = filteredExperiences,
                            isDark = isDark,
                            onCafeClick = onCafeClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ViewToggleSegment(
    isSelected: Boolean,
    isDark: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(width = 34.dp, height = 30.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) (if (isDark) Color(0xFF2C221D) else Color.White) else Color.Transparent,
        shadowElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
fun GridFeed(
    experiences: List<CafeExperience>,
    isDark: Boolean = false,
    onCafeClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(experiences, key = { _, item -> item.id.ifBlank { item.timestamp.toString() } }) { index, exp ->
            GridCard(
                experience = exp,
                index = index,
                isDark = isDark,
                onClick = { onCafeClick(exp.id) }
            )
        }
    }
}

@Composable
fun GridCard(
    experience: CafeExperience,
    index: Int,
    isDark: Boolean = false,
    onClick: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF2C221D) else Color.White
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.5f)
    val chipBg = if (isDark) Color(0xCC2C221D) else Color(0xF2F8F5F0)
    val chipTextColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.7f) else Color(0xFF2E241E).copy(alpha = 0.55f)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Photo Area (118.dp tall)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                val firstPhoto = experience.photoUri.split(",").firstOrNull { it.isNotBlank() } ?: ""
                if (firstPhoto.isNotBlank()) {
                    AsyncImage(
                        model = firstPhoto,
                        contentDescription = experience.cafeName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ExperiencePhotoPlaceholder(
                        index = index,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Drink Caption Chip (bottom-left, 8.dp inset)
                val drinkText = experience.coffeeRecommendation.ifBlank { "COFFEE" }.uppercase()
                Surface(
                    color = chipBg,
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = drinkText,
                        fontFamily = IbmPlexMonoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 8.sp,
                        letterSpacing = 1.sp,
                        color = chipTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            // Body
            Column(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = experience.cafeName.ifBlank { "Untitled Café" },
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 17.5.sp,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val formattedDate = remember(experience.timestamp) {
                    val sdf = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                    sdf.format(experience.timestamp.toDate())
                }
                val metaText = "${experience.location.ifBlank { "Area" }} · $formattedDate"
                Text(
                    text = metaText,
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = subtextColor,
                    maxLines = 1
                )

                // 3 Rating Bars (COF, VIB, SEA)
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    RatingBarRow("COF", experience.rating.coffee, isDark)
                    RatingBarRow("VIB", experience.rating.vibe, isDark)
                    RatingBarRow("SEA", experience.rating.seating, isDark)
                }
            }
        }
    }
}

@Composable
fun RatingBarRow(key: String, value: Float, isDark: Boolean = false) {
    val keyColor = if (isDark) Color(0xFFD5C2B9).copy(alpha = 0.6f) else Color(0xFF2E241E).copy(alpha = 0.45f)
    val trackColor = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = key,
            fontFamily = IbmPlexMonoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 8.5.sp,
            color = keyColor,
            modifier = Modifier.width(24.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(3.dp)
                .background(trackColor, RoundedCornerShape(2.dp))
        ) {
            val fraction = (value / 5f).coerceIn(0f, 1f)
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .background(Color(0xFFC05A3B), RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

@Composable
fun AlbumFeed(
    experiences: List<CafeExperience>,
    isDark: Boolean = false,
    onCafeClick: (String) -> Unit
) {
    val monthSdf = remember { SimpleDateFormat("MMMM yyyy", Locale.ENGLISH) }
    val dayNumSdf = remember { SimpleDateFormat("dd", Locale.ENGLISH) }
    val dayNameSdf = remember { SimpleDateFormat("EEE", Locale.ENGLISH) }
    val dateLabelSdf = remember { SimpleDateFormat("dd MMM", Locale.ENGLISH) }

    val monthHeaderColor = if (isDark) Color(0xFFD5C2B9).copy(alpha = 0.6f) else Color(0xFF2E241E).copy(alpha = 0.42f)
    val dividerLineColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.15f) else Color(0xFF2E241E).copy(alpha = 0.12f)
    val dayNumColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val dayNameColor = if (isDark) Color(0xFFD5C2B9).copy(alpha = 0.6f) else Color(0xFF2E241E).copy(alpha = 0.4f)
    val nameColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val metaColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.45f)
    val notesColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.85f) else Color(0xFF2E241E).copy(alpha = 0.78f)
    val captionBg = if (isDark) Color(0xCC2C221D) else Color(0xDCF3EEE6)
    val captionTextColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.7f) else Color(0xFF2E241E).copy(alpha = 0.55f)
    val tagBorderColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.2f) else Color(0xFF2E241E).copy(alpha = 0.18f)
    val tagTextColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF50443D)

    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        var lastMonthHeader = ""

        experiences.forEachIndexed { index, exp ->
            val date = exp.timestamp.toDate()
            val monthHeader = monthSdf.format(date).uppercase()

            if (monthHeader != lastMonthHeader) {
                lastMonthHeader = monthHeader
                item(key = "month_" + monthHeader + "_" + index) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 6.dp)
                    ) {
                        Text(
                            text = monthHeader,
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            color = monthHeaderColor
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(dividerLineColor)
                        )
                    }
                }
            }

            item(key = exp.id.ifBlank { "item_$index" }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Date Rail
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(38.dp)
                    ) {
                        Text(
                            text = dayNumSdf.format(date),
                            fontFamily = NewsreaderFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 19.sp,
                            color = dayNumColor
                        )
                        Text(
                            text = dayNameSdf.format(date).uppercase(),
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 9.sp,
                            color = dayNameColor
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(1.dp)
                                .fillMaxHeight()
                                .defaultMinSize(minHeight = 40.dp)
                                .background(dividerLineColor)
                        )
                    }

                    // Right Entry Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 24.dp)
                            .clickable { onCafeClick(exp.id) },
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        // Photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(186.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            val firstPhoto = exp.photoUri.split(",").firstOrNull { it.isNotBlank() } ?: ""
                            if (firstPhoto.isNotBlank()) {
                                AsyncImage(
                                    model = firstPhoto,
                                    contentDescription = exp.cafeName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                ExperiencePhotoPlaceholder(
                                    index = index,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            val captionText = "PHOTO — ${exp.coffeeRecommendation.ifBlank { exp.cafeName }}"
                            Surface(
                                color = captionBg,
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = captionText,
                                    fontFamily = IbmPlexMonoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 8.5.sp,
                                    letterSpacing = 0.8.sp,
                                    color = captionTextColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Name ↔ Derived average
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = exp.cafeName.ifBlank { "Untitled Café" },
                                fontFamily = NewsreaderFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 19.sp,
                                lineHeight = 23.sp,
                                color = nameColor,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            val avgFormatted = String.format(Locale.ENGLISH, "%.1f", exp.rating.average)
                            Text(
                                text = avgFormatted,
                                fontFamily = IbmPlexMonoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                color = Color(0xFFC05A3B)
                            )
                        }

                        // Metadata strip: AREA · DRINK · PRICE
                        val area = exp.location.ifBlank { "AREA" }.uppercase()
                        val drink = exp.coffeeRecommendation.ifBlank { "COFFEE" }.uppercase()
                        val price = exp.priceRange.ifBlank { "—" }.uppercase()
                        Text(
                            text = "$area · $drink · $price",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 0.4.sp,
                            color = metaColor
                        )

                        // Notes
                        if (exp.notes.isNotBlank()) {
                            Text(
                                text = exp.notes,
                                fontFamily = NewsreaderFontFamily,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.5.sp,
                                lineHeight = 22.5.sp,
                                color = notesColor
                            )
                        }

                        // Facility tags (up to 3)
                        if (exp.facilitiesTags.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                exp.facilitiesTags.take(3).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, tagBorderColor, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = tag,
                                            fontFamily = DMSansFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 10.5.sp,
                                            color = tagTextColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
