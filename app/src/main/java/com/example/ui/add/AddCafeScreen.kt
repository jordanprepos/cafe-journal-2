package com.example.ui.add

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.CafeRating
import com.example.data.ThemeRepository
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.theme.IbmPlexMonoFontFamily
import com.example.ui.theme.NewsreaderFontFamily

val ALL_FACILITY_TAGS = listOf(
    "WiFi", "Power Outlets", "Parking", "Air Conditioning",
    "Outdoor Seating", "Pet Friendly", "Restroom", "Prayer Room",
    "Laptop Friendly", "Open Late", "Card / QRIS", "Halal",
    "Full Food Menu", "Wheelchair Accessible"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCafeScreen(
    cafeId: String? = null,
    onBack: () -> Unit,
    viewModel: AddCafeViewModel = viewModel()
) {
    val isSaving by viewModel.isSaving.collectAsState()
    val saveError by viewModel.saveError.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val loadedExperience by viewModel.loadedExperience.collectAsState()

    val context = LocalContext.current
    val themeRepository = remember { ThemeRepository(context) }
    val isDarkModeState by themeRepository.isDarkMode.collectAsState(initial = null)
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

    val bgColor = if (isDark) Color(0xFF231A16) else Color(0xFFF8F5F0)
    val cardBgColor = if (isDark) Color(0xFF2C221D) else Color.White
    val textColor = if (isDark) Color(0xFFEDE0DB) else Color(0xFF2E241E)
    val subtextColor = if (isDark) Color(0xFFD5C2B9) else Color(0xFF2E241E).copy(alpha = 0.55f)
    val dividerColor = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)
    val photoWellBg = if (isDark) Color(0xFF2C221D) else Color(0xFFEFEAE2)
    val fieldBorderColor = if (isDark) Color(0xFF4A3B33) else Color(0xFFDED7CD)

    var cafeName by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var drink by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var coffeeRating by remember { mutableFloatStateOf(0f) }
    var vibeRating by remember { mutableFloatStateOf(0f) }
    var wifiRating by remember { mutableFloatStateOf(0f) }
    var seatingRating by remember { mutableFloatStateOf(0f) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var photoUri by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val uriStrings = uris.map { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Ignore non-persistable URI errors
                }
                uri.toString()
            }
            val existingList = photoUri.split(",").map { it.trim() }.filter { it.isNotBlank() }
            val newList = (existingList + uriStrings).distinct()
            photoUri = newList.joinToString(",")
        }
    }

    val isEdit = !cafeId.isNullOrBlank()

    LaunchedEffect(cafeId) {
        if (isEdit && cafeId != null) {
            viewModel.loadExperience(cafeId)
        }
    }

    LaunchedEffect(loadedExperience) {
        loadedExperience?.let { exp ->
            cafeName = exp.cafeName
            area = exp.location
            price = exp.priceRange
            drink = exp.coffeeRecommendation
            note = exp.notes
            coffeeRating = exp.rating.coffee
            vibeRating = exp.rating.vibe
            wifiRating = exp.rating.wifi
            seatingRating = exp.rating.seating
            selectedTags = exp.facilitiesTags.toSet()
            photoUri = exp.photoUri
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onBack()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = subtextColor,
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = if (isEdit) "Edit entry" else "New entry",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
                Text(
                    text = "Save",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFFC05A3B),
                    modifier = Modifier.clickable(enabled = !isSaving) {
                        viewModel.saveExperience(
                            id = loadedExperience?.id ?: "",
                            cafeName = cafeName,
                            location = area,
                            rating = CafeRating(coffeeRating, vibeRating, wifiRating, seatingRating),
                            coffeeRecommendation = drink,
                            priceRange = price,
                            facilitiesTags = selectedTags.toList(),
                            notes = note,
                            photoUri = photoUri,
                            timestamp = loadedExperience?.timestamp
                        )
                    }
                )
            }
            HorizontalDivider(color = dividerColor, thickness = 1.dp)

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Photo section
                val strokeColor = if (isDark) Color(0xFFEDE0DB).copy(alpha = 0.3f) else Color(0xFF2E241E).copy(alpha = 0.25f)
                val photoList = remember(photoUri) {
                    photoUri.split(",").map { it.trim() }.filter { it.isNotBlank() }
                }

                if (photoList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(132.dp)
                            .background(photoWellBg, RoundedCornerShape(16.dp))
                            .drawWithContent {
                                drawContent()
                                drawRoundRect(
                                    color = strokeColor,
                                    style = Stroke(
                                        width = 3f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                    ),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                                )
                            }
                            .clickable {
                                photoPickerLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 30.dp, height = 24.dp)
                                    .border(1.8.dp, strokeColor, RoundedCornerShape(5.dp))
                            )
                            Text(
                                text = "ADD PHOTOS",
                                fontFamily = IbmPlexMonoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp,
                                color = subtextColor
                            )
                            Text(
                                text = "Tap to choose images from device",
                                fontFamily = DMSansFontFamily,
                                fontSize = 11.sp,
                                color = subtextColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PHOTOS (${photoList.size})",
                                fontFamily = IbmPlexMonoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 1.4.sp,
                                color = subtextColor
                            )
                            Text(
                                text = "+ Add More",
                                fontFamily = DMSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = Color(0xFFC05A3B),
                                modifier = Modifier.clickable {
                                    photoPickerLauncher.launch("image/*")
                                }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            photoList.forEachIndexed { index, uriStr ->
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(photoWellBg)
                                ) {
                                    AsyncImage(
                                        model = uriStr,
                                        contentDescription = "Selected photo ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Remove photo button
                                    Surface(
                                        onClick = {
                                            val updated = photoList.filterIndexed { i, _ -> i != index }
                                            photoUri = updated.joinToString(",")
                                        },
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.65f),
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove photo",
                                            tint = Color.White,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                            }

                            // Add button box
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .background(photoWellBg, RoundedCornerShape(12.dp))
                                    .drawWithContent {
                                        drawContent()
                                        drawRoundRect(
                                            color = strokeColor,
                                            style = Stroke(
                                                width = 3f,
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                                            ),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                                        )
                                    }
                                    .clickable {
                                        photoPickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add photo",
                                        tint = subtextColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "ADD",
                                        fontFamily = IbmPlexMonoFontFamily,
                                        fontSize = 10.sp,
                                        color = subtextColor
                                    )
                                }
                            }
                        }
                    }
                }

                // CAFE NAME
                FormField(label = "CAFE NAME", subtextColor = subtextColor) {
                    CustomTextField(
                        value = cafeName,
                        onValueChange = { cafeName = it },
                        placeholder = "e.g. Kopi Nako",
                        isDark = isDark,
                        containerColor = cardBgColor,
                        borderColor = fieldBorderColor,
                        textColor = textColor,
                        subtextColor = subtextColor
                    )
                }

                // AREA / PRICE Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormField(label = "AREA", subtextColor = subtextColor) {
                            CustomTextField(
                                value = area,
                                onValueChange = { area = it },
                                placeholder = "Kemang",
                                isDark = isDark,
                                containerColor = cardBgColor,
                                borderColor = fieldBorderColor,
                                textColor = textColor,
                                subtextColor = subtextColor
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FormField(label = "PRICE", subtextColor = subtextColor) {
                            CustomTextField(
                                value = price,
                                onValueChange = { price = it },
                                placeholder = "Rp 35K",
                                isDark = isDark,
                                containerColor = cardBgColor,
                                borderColor = fieldBorderColor,
                                textColor = textColor,
                                subtextColor = subtextColor
                            )
                        }
                    }
                }

                // WHAT YOU DRANK
                FormField(label = "WHAT YOU DRANK", subtextColor = subtextColor) {
                    CustomTextField(
                        value = drink,
                        onValueChange = { drink = it },
                        placeholder = "Flat white, oat",
                        isDark = isDark,
                        containerColor = cardBgColor,
                        borderColor = fieldBorderColor,
                        textColor = textColor,
                        subtextColor = subtextColor
                    )
                }

                // RATE IT
                FormField(label = "RATE IT", subtextColor = subtextColor) {
                    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        RatingDotsRow("Coffee", coffeeRating, textColor, isDark) { coffeeRating = it }
                        RatingDotsRow("Vibe", vibeRating, textColor, isDark) { vibeRating = it }
                        RatingDotsRow("WiFi", wifiRating, textColor, isDark) { wifiRating = it }
                        RatingDotsRow("Seating", seatingRating, textColor, isDark) { seatingRating = it }
                    }
                }

                // FACILITIES
                FormField(label = "FACILITIES", subtextColor = subtextColor) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ALL_FACILITY_TAGS.forEach { tag ->
                            val isSelected = selectedTags.contains(tag)
                            val tagBg = if (isSelected) (if (isDark) Color(0xFFE0A891) else Color(0xFF2E241E)) else (if (isDark) Color(0xFF2C221D) else Color(0xFFEBE6DF))
                            val tagContent = if (isSelected) (if (isDark) Color(0xFF231A16) else Color(0xFFF8F5F0)) else textColor
                            Surface(
                                onClick = {
                                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                },
                                shape = RoundedCornerShape(999.dp),
                                color = tagBg,
                                contentColor = tagContent
                            ) {
                                Text(
                                    text = tag,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // NOTES TO FUTURE YOU
                FormField(label = "NOTES TO FUTURE YOU", subtextColor = subtextColor) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = {
                            Text(
                                "Who you were with, what the light was like, whether you'd go back.",
                                fontFamily = NewsreaderFontFamily,
                                fontStyle = FontStyle.Italic,
                                fontSize = 15.sp,
                                color = subtextColor
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = NewsreaderFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            color = textColor
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBgColor,
                            unfocusedContainerColor = cardBgColor,
                            focusedBorderColor = Color(0xFFC05A3B),
                            unfocusedBorderColor = fieldBorderColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                    )
                }

                // Error Banner
                if (saveError != null) {
                    Surface(
                        color = Color(0xFFFFDAD6),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = saveError!!,
                            fontFamily = DMSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.5.sp,
                            color = Color(0xFFBA1A1A),
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp)
                        )
                    }
                }

                // Save CTA
                Button(
                    onClick = {
                        viewModel.saveExperience(
                            id = loadedExperience?.id ?: "",
                            cafeName = cafeName,
                            location = area,
                            rating = CafeRating(coffeeRating, vibeRating, wifiRating, seatingRating),
                            coffeeRecommendation = drink,
                            priceRange = price,
                            facilitiesTags = selectedTags.toList(),
                            notes = note,
                            photoUri = photoUri,
                            timestamp = loadedExperience?.timestamp
                        )
                    },
                    enabled = !isSaving,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC05A3B),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (isSaving) "Saving..." else if (isEdit) "Save changes" else "Save to journal",
                        fontFamily = DMSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    subtextColor: Color = Color(0xFF2E241E).copy(alpha = 0.45f),
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = label,
            fontFamily = IbmPlexMonoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.4.sp,
            color = subtextColor
        )
        content()
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isDark: Boolean = false,
    containerColor: Color = Color.White,
    borderColor: Color = Color(0xFFDED7CD),
    textColor: Color = Color(0xFF2E241E),
    subtextColor: Color = Color(0xFF2E241E).copy(alpha = 0.4f)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                fontFamily = DMSansFontFamily,
                fontSize = 15.sp,
                color = subtextColor
            )
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontFamily = DMSansFontFamily,
            fontSize = 15.sp,
            color = textColor
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedBorderColor = Color(0xFFC05A3B),
            unfocusedBorderColor = borderColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
fun RatingDotsRow(
    label: String,
    ratingValue: Float,
    labelTextColor: Color = Color(0xFF2E241E),
    isDark: Boolean = false,
    onRatingChange: (Float) -> Unit
) {
    val unselectedDotColor = if (isDark) Color(0xFF3D322B) else Color(0xFFEBE6DF)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontFamily = DMSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = labelTextColor
        )
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            (1..5).forEach { dotIndex ->
                val isFilled = ratingValue >= dotIndex
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            color = if (isFilled) Color(0xFFC05A3B) else unselectedDotColor,
                            shape = CircleShape
                        )
                        .clickable { onRatingChange(dotIndex.toFloat()) }
                )
            }
        }
    }
}
