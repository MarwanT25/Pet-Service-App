package com.example.petservicetemp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavHostController

data class Booking(
    val id: String,
    val customerName: String,
    val service: String,
    val date: String,
    val time: String,
    val status: String // "Pending", "Confirmed", "Completed", "Cancelled"
)

@Composable
fun ClinicHomeScreen(
    clinicName: String = "Clinic Name",
    navController: NavHostController?
) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)

    // Sample bookings data
    val bookings = remember {
        listOf(
            Booking("1", "Ahmed Mohamed", "Grooming", "15/12/2024", "10:00 AM", "Pending"),
            Booking("2", "Sara Ali", "Checkup", "15/12/2024", "2:00 PM", "Confirmed"),
            Booking("3", "Mohamed Hassan", "Vaccine", "16/12/2024", "11:00 AM", "Pending"),
            Booking("4", "Fatma Ibrahim", "Grooming", "16/12/2024", "3:00 PM", "Confirmed"),
            Booking("5", "Omar Khaled", "Checkup", "17/12/2024", "9:00 AM", "Pending")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", textAlign = TextAlign.Center) },
                backgroundColor = primary,
                contentColor = Color.White,
                actions = {
                    IconButton(onClick = {
                        val encodedName = java.net.URLEncoder.encode(clinicName, "UTF-8")
                        navController?.navigate("clinic_profile/$encodedName")
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = backgroundLight
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
                backgroundColor = primary
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Welcome back!",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = clinicName,
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bookings Section
            Text(
                text = "Bookings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = primaryDark
            )

            // Bookings List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)

    val statusColor = when (booking.status) {
        "Pending" -> Color(0xFFFF9800)
        "Confirmed" -> Color(0xFF4CAF50)
        "Completed" -> Color(0xFF2196F3)
        "Cancelled" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.customerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryDark
                )
                Card(
                    backgroundColor = statusColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = Color.LightGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Service",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = booking.service,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryDark
                    )
                }

                Column {
                    Text(
                        text = "Date",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = booking.date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryDark
                    )
                }

                Column {
                    Text(
                        text = "Time",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = booking.time,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryDark
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ClinicHomeScreenPreview() {
    ClinicHomeScreen(clinicName = "Happy Paws Clinic", navController = null)
}

