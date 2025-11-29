package com.example.petservicetemp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class ClinicDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8F8F8)) {
                ClinicAppBar()
            }
        }
    }
}

@Composable
fun ClinicDetailsScreen(
    clinic: Clinic,
    navController: NavHostController
) {
    // Debug علشان نتأكد من البيانات
    LaunchedEffect(clinic) {
        println("🎯 ClinicDetails - Name: ${clinic.name}")
        println("🎯 ClinicDetails - Working Hours: ${clinic.workingHours}")
        println("🎯 ClinicDetails - Services: ${clinic.services}")
        println("🎯 ClinicDetails - Rating: ${clinic.rating}")
        println("🎯 ClinicDetails - Location: ${clinic.location}")
    }

    ClinicAppBar(clinic = clinic, navController = navController)
}

@Preview
@Composable
fun ClinicAppBar(
    clinic: Clinic? = null,
    navController: NavHostController? = null
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    clinic?.name ?: "Clinic Details",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            backgroundColor = Color(0xFF819067),
            contentColor = Color.White,
            navigationIcon = {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorite")
                }
            }
        )
    }) { innerPadding ->
        ClinicBody(clinic = clinic, navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ClinicBody(
    clinic: Clinic? = null,
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header Card بدون صورة
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // الاسم فقط
                Text(
                    clinic?.name ?: "Clinic Name",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rating و Reviews
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // عرض الـ rating
                    val rating = clinic?.rating ?: 0.0
                    val fullStars = rating.toInt()
                    val hasHalfStar = rating - fullStars >= 0.5

                    repeat(fullStars) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (hasHalfStar) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Half Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Empty Rating",
                            tint = Color(0xFFCCCCCC),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${String.format("%.1f", rating)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(${clinic?.reviews ?: 0} Reviews)", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (clinic?.isOpen == true) "✅ Open Now" else "❌ Currently Closed",
                        fontSize = 16.sp,
                        color = if (clinic?.isOpen == true) Color(0xFF4CAF50) else Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // زر الحجز
                Button(
                    onClick = {
                        clinic?.let {
                            val encodedName = java.net.URLEncoder.encode(it.name, "UTF-8")
                            val encodedLocation = java.net.URLEncoder.encode(it.location, "UTF-8")
                            val encodedPhone = java.net.URLEncoder.encode(it.phoneNumber, "UTF-8")
                            navController?.navigate(
                                "booking/$encodedName/${it.rating}/${it.isOpen}/$encodedLocation/${it.reviews}/$encodedPhone"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF819067))
                ) {
                    Text(
                        "Book Your Appointment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("About", "Reviews")

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF404C35) else Color.White)
                                .border(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = Color(0xFFCCCCCC),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        AboutTab(clinic = clinic)
                    }
                    1 -> {
                        ReviewsTab(clinic = clinic)
                    }
                }
            }
        }
    }
}

@Composable
fun AboutTab(clinic: Clinic?) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("About Clinic", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                clinic?.let {
                    "Welcome to ${it.name}, a professional veterinary clinic located in ${it.location}. " +
                            "We provide comprehensive care for your pets with state-of-the-art facilities and experienced veterinarians."
                } ?: "Professional veterinary clinic providing comprehensive care for your pets.",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        item {
            Divider()
        }

        item {
            Text("Working Hours", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (clinic?.workingHours?.isNotEmpty() == true) {
                    clinic.workingHours
                } else {
                    "9:00 AM - 8:00 PM"
                },
                fontSize = 14.sp
            )
        }

        item {
            Divider()
        }

        item {
            Text("Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${clinic?.location ?: "Location not available"} ", fontSize = 14.sp)
        }

        item {
            Divider()
        }

        item {
            Text("Contacts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Phone: ${clinic?.phoneNumber ?: "N/A"}\nEmail: ${clinic?.email ?: "N/A"}", fontSize = 14.sp)
        }

        item {
            Divider()
        }

        item {
            Text("Services", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if (clinic?.services?.isNotEmpty() == true) {
                clinic.services.forEach { service ->
                    Text("• $service", fontSize = 14.sp)
                }
            } else {
                Text("Comprehensive veterinary services", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ReviewsTab(clinic: Clinic?) {
    var reviewText by remember { mutableStateOf("") }
    var isWritingReview by remember { mutableStateOf(false) }
    val reviews = remember { mutableStateListOf<String>() }
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = reviewText,
                onValueChange = {
                    reviewText = it
                    isWritingReview = it.isNotEmpty()
                },
                label = { Text("Write your review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Type your review here...") },
                maxLines = 5,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = primary,
                    cursorColor = primaryDark
                )
            )
        }

        if (isWritingReview) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            reviews.add(reviewText)
                            reviewText = ""
                            isWritingReview = false
                        },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = primary)
                    ) {
                        Text("Add Review", color = Color.White)
                    }

                    Button(
                        onClick = {
                            reviewText = ""
                            isWritingReview = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (reviews.isEmpty()) {
            item {
                Text(
                    "No reviews yet. Be the first to review ${clinic?.name ?: "this clinic"}!",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(reviews) { review ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    Text(
                        review,
                        modifier = Modifier.padding(12.dp),
                        color = primaryDark
                    )
                }
            }
        }
    }
}