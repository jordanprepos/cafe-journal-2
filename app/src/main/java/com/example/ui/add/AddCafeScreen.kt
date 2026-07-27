package com.example.ui.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale

data class Facility(val key: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCafeScreen(
    onBack: () -> Unit,
    viewModel: AddCafeViewModel = viewModel()
) {
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var cafeName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(5f) }
    var coffeeRecommendation by remember { mutableStateOf("") }
    var priceRange by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var customTags by remember { mutableStateOf(listOf<String>()) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var newTagText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var selectedFacilities by remember { mutableStateOf(setOf<String>()) }

    val facilityOptions = listOf(
        Facility("indoor", "Indoor", Icons.Outlined.Home),
        Facility("outdoor", "Outdoor", Icons.Outlined.Park),
        Facility("wifi", "Wi-Fi", Icons.Outlined.Wifi),
        Facility("smoking_allowed", "Smoking/vape OK", Icons.Outlined.SmokingRooms),
        Facility("power_outlets", "Power outlets", Icons.Outlined.ElectricalServices),
        Facility("parking", "Parking", Icons.Outlined.LocalParking),
        Facility("restroom", "Restroom", Icons.Outlined.Wc),
        Facility("air_conditioning", "Air conditioning", Icons.Outlined.AcUnit),
        Facility("pet_friendly", "Pet friendly", Icons.Outlined.Pets)
    )

    val predefinedTags = listOf("work-friendly", "cosy", "espresso bar")
    val allTags = predefinedTags + customTags

    val currentDate = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onBack()
        }
    }

    LaunchedEffect(saveError) {
        saveError?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    if (showAddTagDialog) {
        AlertDialog(
            onDismissRequest = { showAddTagDialog = false },
            title = { Text("Add Custom Tag") },
            text = {
                OutlinedTextField(
                    value = newTagText,
                    onValueChange = { newTagText = it },
                    label = { Text("Tag Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val tag = newTagText.trim().lowercase()
                        if (tag.isNotEmpty() && !allTags.contains(tag)) {
                            customTags = customTags + listOf(tag)
                            selectedTags = selectedTags + tag
                        }
                        showAddTagDialog = false
                        newTagText = ""
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("New Café Entry", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Photo placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable { /* Handle image pick */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate, 
                            contentDescription = "Add photos",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Upload a Photo", 
                        style = MaterialTheme.typography.titleMedium, 
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Showcase the vibe or your drink", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Form Fields
            OutlinedTextField(
                value = cafeName,
                onValueChange = { cafeName = it },
                label = { Text("Café Name") },
                leadingIcon = { Icon(Icons.Outlined.Coffee, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("e.g. Matcha Symphony") }
            )
            
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Neighborhood or Maps link") }
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Overall Rating", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Star $i",
                                tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { rating = i.toFloat() }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = currentDate,
                    onValueChange = {},
                    label = { Text("Visited") },
                    leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = coffeeRecommendation,
                    onValueChange = { coffeeRecommendation = it },
                    label = { Text("Best Drink") },
                    leadingIcon = { Icon(Icons.Outlined.LocalCafe, contentDescription = null) },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("e.g. Iced Latte") }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("TAGS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allTags.forEach { tag ->
                            val isSelected = selectedTags.contains(tag)
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    selectedTags = if (isSelected) {
                                        selectedTags - tag
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                            ) {
                                Text(
                                    text = if (isSelected) "$tag ✓" else tag,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { showAddTagDialog = true }
                        ) {
                            Text(
                                text = "+ new",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("FACILITIES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        facilityOptions.forEach { facility ->
                            val isSelected = selectedFacilities.contains(facility.label)
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    selectedFacilities = if (isSelected) {
                                        selectedFacilities - facility.label
                                    } else {
                                        selectedFacilities + facility.label
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = facility.icon,
                                        contentDescription = facility.label,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = facility.label,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Journal Notes") },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.padding(bottom = 96.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Write your thoughts about this place...") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val tags = selectedTags.toList() + selectedFacilities.toList()
                    viewModel.saveExperience(
                        cafeName, location, rating, coffeeRecommendation, priceRange, tags, notes
                    )
                },
                enabled = !isSaving && cafeName.isNotBlank() && location.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save Entry",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
