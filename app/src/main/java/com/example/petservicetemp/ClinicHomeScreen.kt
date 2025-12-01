package com.example.petservicetemp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp // <--- Logout Icon
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.auth.FirebaseAuth // <--- Needed for Logout

// --- THEME COLORS ---
private val BrandGreen = Color(0xFF819067)
private val BrandDark = Color(0xFF404C35)
private val BackgroundLight = Color(0xFFF8F8F8)

@Composable
fun ClinicHomeScreen(
    clinicName: String = "Clinic Name",
    navController: NavHostController?
) {
    val viewModel: ClinicBookingsViewModel = viewModel()
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(clinicName) {
        viewModel.fetchBookingsForClinic(clinicName)
    }

    ClinicHomeContent(
        clinicName = clinicName,
        bookings = bookings,
        isLoading = isLoading,
        onProfileClick = {
            val encodedName = java.net.URLEncoder.encode(clinicName, "UTF-8")
            navController?.navigate("clinic_profile/$encodedName")
        },
        onStatusChange = { bookingId, newStatus ->
            viewModel.updateBookingStatus(bookingId, newStatus)
        },
        onLogoutClick = {
            // 1. Sign out
            FirebaseAuth.getInstance().signOut()

            // 2. Go back to Account Choice screen
            navController?.navigate("choose_account") {
                popUpTo(0) // Clear back stack
            }
        }
    )
}

@Composable
fun ClinicHomeContent(
    clinicName: String,
    bookings: List<Booking>,
    isLoading: Boolean,
    onProfileClick: () -> Unit,
    onStatusChange: (String, String) -> Unit,
    onLogoutClick: () -> Unit // <--- New Callback
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", textAlign = TextAlign.Center) },
                backgroundColor = BrandGreen,
                contentColor = Color.White,
                actions = {

                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile",
                            tint = Color.White)
                    }

                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
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
            // Welcome Card
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
                    Text("Welcome back!", fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(clinicName, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Manage Bookings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = BrandDark
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else if (bookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No bookings received yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        BookingCard(
                            booking = booking,
                            onStatusChange = onStatusChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onStatusChange: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Pending", "Confirmed", "Completed", "Cancelled")

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
                    text = booking.userId.ifEmpty { "Customer" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandDark,
                    maxLines = 1
                )

                Box {
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { expanded = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = booking.status,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(onClick = {
                                expanded = false
                                onStatusChange(booking.firestoreId, option)
                            }) {
                                Text(text = option)
                            }
                        }
                    }
                }
            }

            Divider(color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = "Service", value = booking.service)
                DetailItem(label = "Date", value = booking.date)
                DetailItem(label = "Time", value = booking.time)
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BrandDark)
    }
}

@Preview(showBackground = true)
@Composable
fun ClinicHomeScreenPreview() {
    val dummyBookings = listOf(
        Booking(userId = "ahmed@example.com", service = "Grooming", date = "12/1/2025", time = "10:00 AM", status = "Pending")
    )

    ClinicHomeContent(
        clinicName = "Vet Clinic",
        bookings = dummyBookings,
        isLoading = false,
        onProfileClick = {},
        onStatusChange = { _, _ -> },
        onLogoutClick = {}
    )
}