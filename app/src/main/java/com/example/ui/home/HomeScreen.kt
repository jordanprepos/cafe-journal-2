package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.CafeExperience
import java.text.SimpleDateFormat
import java.util.Locale

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.profile.ProfileViewModel
import com.example.ui.profile.ProfileViewModelFactory
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val experiences by viewModel.experiences.collectAsState()
    
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))
    val isDarkMode by profileViewModel.isDarkMode.collectAsState()
    val isDark = isDarkMode ?: isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Image(
                        painter = painterResource(id = if (isDark) R.drawable.cafe_journal_logo_dark_theme else R.drawable.cafe_journal_logo_light_theme),
                        contentDescription = "Cafe Journal Logo",
                        modifier = Modifier.height(48.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip(text = "All", selected = true)
                Chip(text = "Espresso bar", selected = false)
                Chip(text = "Cosy", selected = false)
                Chip(text = "Work", selected = false)
            }
            
            if (experiences.isEmpty()) {
                EmptyState(isDark = isDark)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(experiences) { experience ->
                        CafeExperienceCard(experience)
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(text: String, selected: Boolean) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier, isDark: Boolean) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = if (isDark) R.drawable.cafe_journal_logo_dark_theme else R.drawable.cafe_journal_logo_light_theme),
            contentDescription = "Empty Journal",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your journal is empty.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Share your first cafe experience!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CafeExperienceCard(experience: CafeExperience) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Polaroid image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = experience.cafeName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(experience.rating.toInt()) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                }
                repeat(5 - experience.rating.toInt()) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                experience.facilitiesTags.take(2).forEach { tag ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
