package com.example.ui.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.util.MapUtils
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LocationsScreen(
    onCafeClick: (String) -> Unit = {},
    viewModel: LocationsViewModel = viewModel()
) {
    val experiences by viewModel.experiences.collectAsState()
    val context = LocalContext.current
    val themeRepository = remember { ThemeRepository(context) }
    val isDarkModeState by themeRepository.isDarkMode.collectAsState(initial = null)
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

    val bgColor = if (isDark) Color(0xFF231A16) else Color(0xFFF8F5F0)
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.45f)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
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
                    text = "WHERE YOU'VE BEEN",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = subtextColor
                )
                Text(
                    text = "Places",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.54).sp,
                    color = textColor,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            if (experiences.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No places visited yet.",
                        fontFamily = DMSansFontFamily,
                        color = subtextColor
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(experiences, key = { it.id.ifBlank { it.timestamp.toString() } }) { exp ->
                        PlaceCard(
                            experience = exp,
                            isDark = isDark,
                            onClick = { onCafeClick(exp.id) },
                            onMapsClick = {
                                MapUtils.openGoogleMaps(context, exp.cafeName, exp.location)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceCard(
    experience: CafeExperience,
    isDark: Boolean = false,
    onClick: () -> Unit,
    onMapsClick: () -> Unit
) {
    val initial = remember(experience.cafeName) {
        experience.cafeName.trim().take(1).uppercase()
    }

    val formattedDate = remember(experience.timestamp) {
        val sdf = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        sdf.format(experience.timestamp.toDate())
    }

    val area = experience.location.ifBlank { "Area" }
    val avgFormatted = String.format(Locale.ENGLISH, "%.1f", experience.rating.average)

    val cardBg = if (isDark) Color(0xFF2C221D) else Color.White
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.5f)
    val initialBg = if (isDark) Color(0xFF52281C) else Color(0xFFFFDBD1)
    val initialTextColor = if (isDark) Color(0xFFFFB5A0) else Color(0xFF8F3F27)
    val buttonBg = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)
    val buttonText = if (isDark) Color(0xFFEDE0DB) else Color(0xFF50443D)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Top Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Initial Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(initialBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = initialTextColor
                    )
                }

                // Middle Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = experience.cafeName.ifBlank { "Untitled Café" },
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$area · last visit $formattedDate",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = subtextColor,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }

                // Average Rating
                Text(
                    text = avgFormatted,
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color(0xFFC05A3B)
                )
            }

            // Maps Button
            Surface(
                onClick = onMapsClick,
                shape = RoundedCornerShape(12.dp),
                color = buttonBg,
                contentColor = buttonText,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = buttonText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open in Google Maps",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
