package com.example.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CafeRating
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
        color = Color(0xFFF8F5F0)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F5F0))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFF2E241E).copy(alpha = 0.55f),
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = if (isEdit) "Edit entry" else "New entry",
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2E241E)
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
            HorizontalDivider(color = Color(0xFFEBE6DF), thickness = 1.dp)

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Photo well
                val strokeColor = Color(0xFF2E241E).copy(alpha = 0.25f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .background(Color(0xFFEFEAE2), RoundedCornerShape(16.dp))
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
                            // Photo URI picker could be added here
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
                                .border(1.8.dp, Color(0xFF2E241E).copy(alpha = 0.4f), RoundedCornerShape(5.dp))
                        )
                        Text(
                            text = "ADD A PHOTO",
                            fontFamily = IbmPlexMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp,
                            color = Color(0xFF2E241E).copy(alpha = 0.45f)
                        )
                    }
                }

                // CAFE NAME
                FormField(label = "CAFE NAME") {
                    CustomTextField(
                        value = cafeName,
                        onValueChange = { cafeName = it },
                        placeholder = "e.g. Kopi Nako"
                    )
                }

                // AREA / PRICE Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormField(label = "AREA") {
                            CustomTextField(
                                value = area,
                                onValueChange = { area = it },
                                placeholder = "Kemang"
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FormField(label = "PRICE") {
                            CustomTextField(
                                value = price,
                                onValueChange = { price = it },
                                placeholder = "Rp 35K"
                            )
                        }
                    }
                }

                // WHAT YOU DRANK
                FormField(label = "WHAT YOU DRANK") {
                    CustomTextField(
                        value = drink,
                        onValueChange = { drink = it },
                        placeholder = "Flat white, oat"
                    )
                }

                // RATE IT
                FormField(label = "RATE IT") {
                    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        RatingDotsRow("Coffee", coffeeRating) { coffeeRating = it }
                        RatingDotsRow("Vibe", vibeRating) { vibeRating = it }
                        RatingDotsRow("WiFi", wifiRating) { wifiRating = it }
                        RatingDotsRow("Seating", seatingRating) { seatingRating = it }
                    }
                }

                // FACILITIES
                FormField(label = "FACILITIES") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ALL_FACILITY_TAGS.forEach { tag ->
                            val isSelected = selectedTags.contains(tag)
                            Surface(
                                onClick = {
                                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                },
                                shape = RoundedCornerShape(999.dp),
                                color = if (isSelected) Color(0xFF2E241E) else Color(0xFFEBE6DF),
                                contentColor = if (isSelected) Color(0xFFF8F5F0) else Color(0xFF50443D)
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
                FormField(label = "NOTES TO FUTURE YOU") {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = {
                            Text(
                                "Who you were with, what the light was like, whether you'd go back.",
                                fontFamily = NewsreaderFontFamily,
                                fontStyle = FontStyle.Italic,
                                fontSize = 15.sp,
                                color = Color(0xFF2E241E).copy(alpha = 0.4f)
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = NewsreaderFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            color = Color(0xFF2E241E)
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFFC05A3B),
                            unfocusedBorderColor = Color(0xFFDED7CD)
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
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = label,
            fontFamily = IbmPlexMonoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.4.sp,
            color = Color(0xFF2E241E).copy(alpha = 0.45f)
        )
        content()
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                fontFamily = DMSansFontFamily,
                fontSize = 15.sp,
                color = Color(0xFF2E241E).copy(alpha = 0.4f)
            )
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontFamily = DMSansFontFamily,
            fontSize = 15.sp,
            color = Color(0xFF2E241E)
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFFC05A3B),
            unfocusedBorderColor = Color(0xFFDED7CD)
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
    onRatingChange: (Float) -> Unit
) {
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
            color = Color(0xFF2E241E)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            (1..5).forEach { dotIndex ->
                val isFilled = ratingValue >= dotIndex
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            color = if (isFilled) Color(0xFFC05A3B) else Color(0xFFEBE6DF),
                            shape = CircleShape
                        )
                        .clickable { onRatingChange(dotIndex.toFloat()) }
                )
            }
        }
    }
}
