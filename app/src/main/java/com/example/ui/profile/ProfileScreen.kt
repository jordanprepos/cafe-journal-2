package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.theme.IbmPlexMonoFontFamily

@Composable
fun ProfileScreen(
    onWrappedClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current))
) {
    val isDarkModeState by viewModel.isDarkMode.collectAsState()
    val journalView by viewModel.journalView.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

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
                    text = "ACCOUNT",
                    fontFamily = IbmPlexMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp,
                    color = Color(0xFF2E241E).copy(alpha = 0.45f)
                )
                Text(
                    text = "Profile",
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar Circle (80.dp)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFC05A3B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "J",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }

                // Name & Subtitle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "Jordan",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Color(0xFF2E241E)
                    )
                    Text(
                        text = "Private journal · since Mar 2025",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = Color(0xFF2E241E).copy(alpha = 0.5f)
                    )
                }

                // 2026 WRAPPED CARD
                Card(
                    onClick = onWrappedClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC05A3B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "2026 WRAPPED",
                                fontFamily = IbmPlexMonoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 1.4.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "View your recap",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "View Recap",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Settings Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Row 1: Dark Mode Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dark mode",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.5.sp,
                                color = Color(0xFF2E241E)
                            )
                            Switch(
                                checked = isDark,
                                onCheckedChange = { viewModel.setDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFC05A3B),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFDED7CD)
                                )
                            )
                        }
                        HorizontalDivider(color = Color(0xFFEFEAE2), thickness = 1.dp)

                        // Row 2: Default journal view
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Default journal view",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.5.sp,
                                color = Color(0xFF2E241E)
                            )
                            Text(
                                text = journalView.replaceFirstChar { it.uppercase() },
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.5.sp,
                                color = Color(0xFF2E241E).copy(alpha = 0.45f)
                            )
                        }
                        HorizontalDivider(color = Color(0xFFEFEAE2), thickness = 1.dp)

                        // Row 3: Export journal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Export journal",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.5.sp,
                                color = Color(0xFF2E241E)
                            )
                            Text(
                                text = "CSV",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.5.sp,
                                color = Color(0xFF2E241E).copy(alpha = 0.45f)
                            )
                        }
                    }
                }

                // Log out button
                OutlinedButton(
                    onClick = { /* Handle logout */ },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDED7CD)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC05A3B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Log out",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.5.sp
                    )
                }
            }
        }
    }
}
