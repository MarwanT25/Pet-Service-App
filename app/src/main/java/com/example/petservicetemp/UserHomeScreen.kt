package com.example.petservicetemp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp // Logout Icon
import androidx.compose.material.icons.filled.Person    // Profile Icon
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

// Match the App Theme
private val BrandGreen = Color(0xFF819067)
private val BrandDark = Color(0xFF404C35)
private val BackgroundLight = Color(0xFFF8F8F8)

@Composable
fun UserHomeScreen(navController: NavHostController?) {
    val viewModel: ScheduleViewModel = viewModel()
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    UserHomeContent(
        bookings = bookings,
        isLoading = isLoading,
        onProfileClick = {
            navController?.navigate("user_profile")
        },
        onLogoutClick = {
            FirebaseAuth.getInstance().signOut()
            navController?.navigate("choose_account") {
                popUpTo(0)
            }
        }
    )
}

@Composable
fun UserHomeContent(
    bookings: List<Booking>,
    isLoading: Boolean,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredBookings = bookings.filter { booking ->
        searchQuery.isEmpty() ||
                booking.clinicName.contains(searchQuery, ignoreCase = true) ||
                booking.service.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        // --- TOP BAR IS BACK ---
        topBar = {
            TopAppBar(
                title = { Text("Schedule", textAlign = TextAlign.Center) },
                backgroundColor = BrandGreen,
                contentColor = Color.White,
                elevation = 0.dp,
                // 1. NAVIGATION ICON = TOP LEFT (LOGOUT)
                navigationIcon = {
                    IconButton(onClick = onLogoutClick) {
                        // Using "ExitToApp" but mirroring it or leaving standard
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                // 2. ACTIONS = TOP RIGHT (PROFILE)
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        backgroundColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- GREEN HEADER CARD (CLEANED UP) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = 6.dp,
                backgroundColor = BrandGreen
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Your Appointments",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${bookings.size} bookings found",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search clinic or service...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BrandGreen,
                    cursorColor = BrandGreen,
                    backgroundColor = Color.White
                )
            )

            // Bookings List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else if (bookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No appointments yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookings) { booking ->
                        UserBookingCard(booking = booking)
                    }
                }
            }
        }
    }
}

// --- Components ---

@Composable
fun UserBookingCard(booking: Booking) {
    val statusColor = when (booking.status.lowercase()) {
        "pending" -> Color(0xFF2196F3)
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color.Gray
        "cancelled" -> Color(0xFFF44336)
        else -> Color(0xFFFFA000)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 3.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.clinicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandDark
                )

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = booking.status,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserDetailItem(label = "Service", value = booking.service)
                UserDetailItem(label = "Date", value = booking.date)
                UserDetailItem(label = "Time", value = booking.time)
            }
        }
    }
}

@Composable
fun UserDetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BrandDark)
    }
}

@Preview(showBackground = true)
@Composable
fun UserHomeScreenPreview() {
    val dummyBookings = listOf(
        Booking(clinicName = "Paws Vet", service = "Checkup", date = "12/01/2025", time = "10:00 AM", status = "Pending")
    )

    UserHomeContent(
        bookings = dummyBookings,
        isLoading = false,
        onProfileClick = {},
        onLogoutClick = {}
    )
}