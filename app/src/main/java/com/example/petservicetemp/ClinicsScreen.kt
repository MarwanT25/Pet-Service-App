package com.example.petservicetemp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.petservicetemp.ui.theme.PetServiceTempTheme
import java.net.URLEncoder

class ClinicsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetServiceTempTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F8F8)
                ) {
                    ClinicScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun ClinicScreen(navController: NavHostController? = null) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val secondary = Color(0xFFD9CBA3)
    val backgroundLight = Color(0xFFF8F8F8)
    val context = LocalContext.current

    // State لإدارة التبويب النشط
    var selectedTab by remember { mutableStateOf(0) }

    val menuModifier = Modifier
        .background(color = primary, shape = RoundedCornerShape(16.dp))
        .border(2.dp, primaryDark, RoundedCornerShape(16.dp))

    // 2️⃣ List of Clinics - Sorted by Rating (Highest to Lowest)
    val clinics = remember {
        val clinicList = listOf(
            Clinic(
                id = "1",
                name = "Cat Clinic",
                email = "catclinic@example.com",
                phoneNumber = "123456789",
                location = "Cairo, Egypt",
                workingHours = "9am - 8pm",
                logoBase64 = "",
                licenseBase64 = "",
                password = "1234",
                services = listOf("Vaccination", "Checkup"),
                rating = 4.8,
                isOpen = true,
                reviews = 150
            ),
            Clinic(
                id = "2",
                name = "Paws Vet",
                email = "pawsvet@example.com",
                phoneNumber = "852369741",
                location = "Giza, Egypt",
                workingHours = "8am - 6pm",
                logoBase64 = "",
                licenseBase64 = "",
                password = "1234",
                services = listOf("Surgery", "Dental Care"),
                rating = 4.7,
                isOpen = true,
                reviews = 120
            ),
            Clinic(
                id = "3",
                name = "Happy Tail",
                email = "happytail@example.com",
                phoneNumber = "741258963",
                location = "Alexandria, Egypt",
                workingHours = "10am - 7pm",
                logoBase64 = "",
                licenseBase64 = "",
                password = "1234",
                services = listOf("Checkup", "Grooming"),
                rating = 4.5,
                isOpen = true,
                reviews = 95
            ),
            Clinic(
                id = "4",
                name = "Pet Care Center",
                email = "petcare@example.com",
                phoneNumber = "852369741",
                location = "Giza, Egypt",
                workingHours = "9am - 5pm",
                logoBase64 = "",
                licenseBase64 = "",
                password = "1234",
                services = listOf("Vaccination"),
                rating = 4.5,
                isOpen = false,
                reviews = 120
            ),
            Clinic(
                id = "5",
                name = "Animal Clinic",
                email = "animalclinic@example.com",
                phoneNumber = "741258963",
                location = "Alexandria, Egypt",
                workingHours = "8am - 6pm",
                logoBase64 = "",
                licenseBase64 = "",
                password = "1234",
                services = listOf("Surgery", "Checkup"),
                rating = 4.2,
                isOpen = true,
                reviews = 95
            ),
            Clinic(
                id = "6",
                name = "Furry Friends",
                email = "furryfriends@example.com",
                phoneNumber = "789456123",
                location = "Giza, Egypt",
                workingHours = "10am - 8pm",
                logoBase64 = "",
                licenseBase64 = "",
                password = "1234",
                services = listOf("Grooming"),
                rating = 4.3,
                isOpen = false,
                reviews = 75
            )
        ).sortedByDescending { it.rating }

        mutableStateListOf<Clinic>().apply { addAll(clinicList) }
    }

    // State for filters and search (لتبويب العيادات فقط)
    var searchQuery by remember { mutableStateOf("") }
    var selectedRatingFilter by remember { mutableStateOf<String?>(null) }
    var selectedServiceFilter by remember { mutableStateOf<String?>(null) }
    var selectedNearbyFilter by remember { mutableStateOf(false) }

    // Filtered clinics based on search and filters
    val filteredClinics = remember(clinics, searchQuery, selectedRatingFilter, selectedServiceFilter) {
        clinics.filter { clinic ->
            val matchesSearch = searchQuery.isEmpty() ||
                    clinic.name.contains(searchQuery, ignoreCase = true) ||
                    clinic.location.contains(searchQuery, ignoreCase = true)

            // Service filter - نستخدم الـ services list الفعلي
            val matchesService = selectedServiceFilter == null || when (selectedServiceFilter) {
                "Basic Care" -> clinic.services.any { service ->
                    service.contains("Checkup", ignoreCase = true) ||
                            service.contains("Vaccination", ignoreCase = true)
                }
                "Grooming Services" -> clinic.services.any { service ->
                    service.contains("Grooming", ignoreCase = true)
                }
                "Boarding & Daycare" -> clinic.services.any { service ->
                    service.contains("Boarding", ignoreCase = true) ||
                            service.contains("Daycare", ignoreCase = true)
                }
                "Medical & Surgical Care" -> clinic.services.any { service ->
                    service.contains("Surgery", ignoreCase = true) ||
                            service.contains("Dental", ignoreCase = true) ||
                            service.contains("Medical", ignoreCase = true)
                }
                else -> true
            }

            matchesSearch && matchesService
        }.let { filtered ->
            if (selectedRatingFilter == "Low to High") {
                filtered.sortedBy { it.rating }
            } else {
                // Default: High to Low (from highest to lowest)
                filtered.sortedByDescending { it.rating }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (selectedTab) {
                            0 -> "Clinics"
                            1 -> "Schedule"
                            2 -> "Map"
                            else -> "Clinics"
                        },
                        textAlign = TextAlign.Center
                    )
                },
                backgroundColor = primary,
                contentColor = Color.White,
                actions = {
                    // زر البروفايل في ال App Bar
                    IconButton(
                        onClick = {
                            navController?.navigate("user_profile")
                        }
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                }
            )
        },
        content = { padding ->
            // نعرض المحتوى بناءً على التبويب النشط
            when (selectedTab) {
                0 -> { // Clinics Tab
                    Column(modifier = Modifier.padding(padding)) {
                        // Filters & Search
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var ratingExpanded by remember { mutableStateOf(false) }
                            var expanded by remember { mutableStateOf(false) }
                            var serviceExpanded by remember { mutableStateOf(false) }

                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = secondary)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = menuModifier
                            ) {
                                DropdownMenuItem(onClick = {
                                    selectedNearbyFilter = !selectedNearbyFilter
                                    expanded = false
                                }) {
                                    Text(if (selectedNearbyFilter) "✓ Nearby me" else "Nearby me")
                                }
                                DropdownMenuItem(onClick = {
                                    ratingExpanded = true
                                }) {
                                    Text("Rating")
                                }
                                DropdownMenuItem(onClick = {
                                    serviceExpanded = true
                                }) {
                                    Text("Service")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedRatingFilter = null
                                    selectedServiceFilter = null
                                    selectedNearbyFilter = false
                                    expanded = false
                                }) {
                                    Text("Clear Filters")
                                }
                            }

                            DropdownMenu(
                                expanded = ratingExpanded,
                                onDismissRequest = { ratingExpanded = false },
                                offset = DpOffset(x = 120.dp, y = 0.dp),
                                modifier = menuModifier
                            ) {
                                DropdownMenuItem(onClick = {
                                    selectedRatingFilter = "High to Low"
                                    ratingExpanded = false
                                    expanded = false
                                }) {
                                    Text(if (selectedRatingFilter == "High to Low") "✓ High to Low" else "High to Low")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedRatingFilter = "Low to High"
                                    ratingExpanded = false
                                    expanded = false
                                }) {
                                    Text(if (selectedRatingFilter == "Low to High") "✓ Low to High" else "Low to High")
                                }
                            }

                            DropdownMenu(
                                expanded = serviceExpanded,
                                onDismissRequest = { serviceExpanded = false },
                                offset = DpOffset(x = 120.dp, y = 80.dp),
                                modifier = menuModifier
                            ) {
                                DropdownMenuItem(onClick = {
                                    selectedServiceFilter = "Basic Care"
                                    serviceExpanded = false
                                    expanded = false
                                }) {
                                    Text(if (selectedServiceFilter == "Basic Care") "✓ Basic Care" else "Basic Care")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedServiceFilter = "Grooming Services"
                                    serviceExpanded = false
                                    expanded = false
                                }) {
                                    Text(if (selectedServiceFilter == "Grooming Services") "✓ Grooming Services" else "Grooming Services")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedServiceFilter = "Boarding & Daycare"
                                    serviceExpanded = false
                                    expanded = false
                                }) {
                                    Text(if (selectedServiceFilter == "Boarding & Daycare") "✓ Boarding & Daycare" else "Boarding & Daycare")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedServiceFilter = "Medical & Surgical Care"
                                    serviceExpanded = false
                                    expanded = false
                                }) {
                                    Text(if (selectedServiceFilter == "Medical & Surgical Care") "✓ Medical & Surgical Care" else "Medical & Surgical Care")
                                }
                            }

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search...") },
                                modifier = Modifier.weight(1f).padding(end = 4.dp),
                                shape = RoundedCornerShape(20.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    backgroundColor = Color.White,
                                    focusedBorderColor = primaryDark,
                                    unfocusedBorderColor = secondary
                                ),
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = secondary) }
                            )
                        }

                        // 3️⃣ List of Cards
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredClinics.size) { index ->
                                val clinic = filteredClinics[index]
                                CardOfClinics(
                                    clinic = clinic,
                                    onCardClick = {
                                        val encodedName = URLEncoder.encode(clinic.name, "UTF-8")
                                        val encodedLocation = URLEncoder.encode(clinic.location, "UTF-8")
                                        val encodedPhone = URLEncoder.encode(clinic.phoneNumber, "UTF-8")
                                        navController?.navigate(
                                            "clinic_details/$encodedName/${clinic.rating}/${clinic.isOpen}/$encodedLocation/${clinic.reviews}/$encodedPhone"
                                        )
                                    },
                                    onCallNow = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${clinic.phoneNumber}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    onBookAppointment = {
                                        val encodedName = URLEncoder.encode(clinic.name, "UTF-8")
                                        val encodedLocation = URLEncoder.encode(clinic.location, "UTF-8")
                                        val encodedPhone = URLEncoder.encode(clinic.phoneNumber, "UTF-8")
                                        navController?.navigate("clinic_details/$encodedName/${clinic.rating}/${clinic.isOpen}/$encodedLocation/${clinic.reviews}/$encodedPhone")
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> { // Schedule Tab - UserHomeScreen
                    UserHomeScreen(navController = navController)
                }
                2 -> { // Map Tab
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = "Map",
                                tint = primary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Find Clinics Near You",
                                style = MaterialTheme.typography.h5,
                                color = primaryDark
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // فتح تطبيق Google Maps
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=pet+clinics")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/pet+clinics"))
                                        context.startActivity(webIntent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = primary),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Open Maps", color = Color.White)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigation(backgroundColor = primary, contentColor = Color.White) {
                val unselectedColor = primaryDark

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Clinics") },
                    label = { Text("Clinics") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    selectedContentColor = Color.White,
                    unselectedContentColor = unselectedColor
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule") },
                    label = { Text("Schedule") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    selectedContentColor = Color.White,
                    unselectedContentColor = unselectedColor
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                    label = { Text("Map") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    selectedContentColor = Color.White,
                    unselectedContentColor = unselectedColor
                )
            }
        }
    )
}

@Composable
fun CardOfClinics(
    clinic: Clinic,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onCallNow: () -> Unit = {},
    onBookAppointment: () -> Unit = {}
) {
    val context = LocalContext.current
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = 6.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(primary, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "LOGO", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            clinic.name,
                            style = MaterialTheme.typography.h6,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (clinic.isOpen) "Open" else "Close",
                        color = if (clinic.isOpen) Color(0xFF4CAF50) else Color.Red,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${clinic.rating} (${clinic.reviews} reviews)", style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = "Location", tint = primaryDark)
                Spacer(modifier = Modifier.width(4.dp))
                Text(clinic.location, style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { onCallNow() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = primary)
                ) {
                    Text("Call now", color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { onBookAppointment() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = primary)
                ) {
                    Text("Book appointment", color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}