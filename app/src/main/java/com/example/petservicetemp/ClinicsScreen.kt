package com.example.petservicetemp

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.petservicetemp.ui.theme.PetServiceTempTheme

class ClinicsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetServiceTempTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ClinicScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun ClinicScreen() {//list of clinics
    var menuModifier = Modifier
        .background(
            color = colorResource(id = R.color.primary),
            shape = RoundedCornerShape(16.dp)
        )
        .border(2.dp, colorResource(id = R.color.primary_dark), RoundedCornerShape(16.dp))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Clinics",
                        textAlign = TextAlign.Center
                    )
                },
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = colorResource(id = R.color.white),
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                // الفلاتر والبحث
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var ratingExpanded by remember { mutableStateOf(false) }
                    var expanded by remember { mutableStateOf(false) }
                    var serviceExpanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filter",
                            tint = colorResource(id = R.color.secondary)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = menuModifier
                    ) {
                        DropdownMenuItem(onClick = { /*handle Near Me */ }) {
                            Text("Nearby me")
                        }

                        DropdownMenuItem(onClick = { ratingExpanded = !ratingExpanded }) {
                            Row {
                                Text("Rating")
                                Icon(
                                    imageVector = if (ratingExpanded)
                                        Icons.Default.KeyboardArrowDown
                                    else Icons.Default.KeyboardArrowUp,
                                    contentDescription = null
                                )
                            }
                        }

                        DropdownMenuItem(onClick = { serviceExpanded = !serviceExpanded }) {
                            Row {
                                Text("Service")
                                Icon(
                                    imageVector = if (serviceExpanded)
                                        Icons.Default.KeyboardArrowDown
                                    else Icons.Default.KeyboardArrowUp,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = ratingExpanded,
                        onDismissRequest = { ratingExpanded = false },
                        offset = DpOffset(x = 120.dp, y = 0.dp),
                        modifier = menuModifier
                    ) {
                        DropdownMenuItem(onClick = { /* High to Low */ }) { Text("High to Low") }
                        DropdownMenuItem(onClick = { /* Low to High */ }) { Text("Low to High") }
                    }

                    DropdownMenu(
                        expanded = serviceExpanded,
                        onDismissRequest = { serviceExpanded = false },
                        offset = DpOffset(x = 120.dp, y = 80.dp),
                        modifier = menuModifier
                    ) {
                        DropdownMenuItem(onClick = { /* Basic Care */ }) { Text("Basic Care") }
                        DropdownMenuItem(onClick = { /* Grooming */ }) { Text("Grooming Services") }
                        DropdownMenuItem(onClick = { /* Boarding */ }) { Text("Boarding & Daycare") }
                        DropdownMenuItem(onClick = { /* Medical */ }) { Text("Medical & Surgical Care") }
                    }

                    OutlinedTextField(
                        value = "",
                        onValueChange = { /* هنا تحطي اللوجيك */ },
                        placeholder = { Text("Search...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            focusedBorderColor = Color(R.color.primary_dark),
                            unfocusedBorderColor = colorResource(id = R.color.secondary)
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = colorResource(id = R.color.secondary)
                            )
                        }
                    )
                }

                // Grid بديل LazyVerticalGrid
                val itemsCount = 10
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(itemsCount / 2 + itemsCount % 2) { rowIndex ->

                        val firstIndex = rowIndex * 2
                        CardOfClinics(
                            name = "Clinic #$firstIndex",
                            rating = 4.5,
                            IsOpen = firstIndex % 2 == 0,
                            location = "Cairo, Egypt",
                            reviews = 120,
                            onCallNow = { println("Calling Clinic #$firstIndex") },
                            onBookAppointment = { println("Booking for Clinic #$firstIndex") },
                            modifier = Modifier.weight(1f)
                        )

                        val secondIndex = firstIndex + 1
                        if (secondIndex < itemsCount) {
                            CardOfClinics(
                                name = "Clinic #$secondIndex",
                                rating = 4.5,
                                IsOpen = secondIndex % 2 == 0,
                                location = "Cairo, Egypt",
                                reviews = 120,
                                onCallNow = { println("Calling Clinic #$secondIndex") },
                                onBookAppointment = { println("Booking for Clinic #$secondIndex") },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                }
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = Color.White
            ) {
                var selectedItem by remember { mutableStateOf("Home") }

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == "Home",
                    onClick = { selectedItem = "Home" },
                    selectedContentColor = Color.White,
                    unselectedContentColor = colorResource(id = R.color.primary_dark2)
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    label = { Text("Cart") },
                    selected = selectedItem == "Cart",
                    onClick = { selectedItem = "Cart" },
                    selectedContentColor = Color.White,
                    unselectedContentColor = colorResource(id = R.color.primary_dark2)
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                    label = { Text("Map") },
                    selected = selectedItem == "Map",
                    onClick = { selectedItem = "Map" },
                    selectedContentColor = Color.White,
                    unselectedContentColor = colorResource(id = R.color.primary_dark2)
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == "Profile",
                    onClick = { selectedItem = "Profile" },
                    selectedContentColor = Color.White,
                    unselectedContentColor = colorResource(id = R.color.primary_dark2)
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Store, contentDescription = "Store") },
                    label = { Text("Store") },
                    selected = selectedItem == "Store",
                    onClick = { selectedItem = "Store" },
                    selectedContentColor = Color.White,
                    unselectedContentColor = colorResource(id = R.color.primary_dark2)
                )
            }
        }
    )
}

@Composable
fun CardOfClinics(
    name: String,
    rating: Double,
    IsOpen: Boolean,
    location: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onCallNow: () -> Unit = {},
    onBookAppointment: () -> Unit = {},
    reviews: Int
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable { onCardClick() }, // الكارد نفسه قابل للضغط
        shape = RoundedCornerShape(12.dp),
        elevation = 6.dp,
        backgroundColor = colorResource(id = R.color.white)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // الجزء العلوي: Logo + اسم الكلينك + الحالة
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            colorResource(id = R.color.primary),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOGO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            name,
                            style = MaterialTheme.typography.h6,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (IsOpen) "Open" else "Close",
                        color = if (IsOpen) Color(0xFF4CAF50) else Color.Red,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(4.dp))
                Text("$rating ($reviews reviews)", style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Location",
                    tint = colorResource(id = R.color.primary_dark2)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(location, style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // الأزرار الداخلية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onCallNow() },
                    modifier = Modifier.weight(1f)
                ) { Text("Call now", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { onBookAppointment() },
                    modifier = Modifier.weight(1f)
                ) { Text("Book appointment", maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        }
    }
}