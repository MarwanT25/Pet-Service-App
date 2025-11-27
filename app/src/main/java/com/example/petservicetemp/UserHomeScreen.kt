package com.example.petservicetemp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.util.*

data class UserBooking(
    val id: String,
    val clinicName: String,
    val service: String,
    val date: String,
    val time: String,
    val status: String // "Pending", "Confirmed", "Completed", "Cancelled"
)

@Composable
fun UserHomeScreen(navController: NavHostController?) {
    val primary = AppTheme.Primary
    val primaryDark = AppTheme.PrimaryDark
    val backgroundLight = AppTheme.BackgroundLight

    // Sample bookings data for user
    var userBookings by remember {
        mutableStateOf(
            listOf(
                UserBooking("1", "Cat Clinic", "Grooming", "15/12/2024", "10:00 AM", "Confirmed"),
                UserBooking("2", "Paws Vet", "Checkup", "16/12/2024", "2:00 PM", "Pending"),
                UserBooking("3", "Happy Tail", "Vaccine", "17/12/2024", "11:00 AM", "Confirmed"),
                UserBooking("4", "Pet Care Center", "Grooming", "18/12/2024", "3:00 PM", "Pending"),
                UserBooking("5", "Animal Clinic", "Checkup", "19/12/2024", "9:00 AM", "Completed")
            )
        )
    }

    // Filter and search states
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }
    var selectedDateFilter by remember { mutableStateOf<String?>(null) }

    // Filtered bookings
    val filteredBookings = remember(userBookings, searchQuery, selectedStatusFilter, selectedDateFilter) {
        userBookings.filter { booking ->
            val matchesSearch = searchQuery.isEmpty() ||
                    booking.clinicName.contains(searchQuery, ignoreCase = true) ||
                    booking.service.contains(searchQuery, ignoreCase = true)

            val matchesStatus = selectedStatusFilter == null || booking.status == selectedStatusFilter

            val matchesDate = selectedDateFilter == null || booking.date == selectedDateFilter

            matchesSearch && matchesStatus && matchesDate
        }
    }

    // Available statuses for filter
    val statuses = listOf("Pending", "Confirmed", "Completed", "Cancelled")

    // Available dates for filter
    val availableDates = userBookings.map { it.date }.distinct()

    Scaffold(
        backgroundColor = backgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
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
                        text = "Your Appointments",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${filteredBookings.size} bookings found",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Search and Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                )

                var statusExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { statusExpanded = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = primary)
                    }
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            selectedStatusFilter = null
                            statusExpanded = false
                        }) {
                            Text("All Statuses")
                        }
                        statuses.forEach { status ->
                            DropdownMenuItem(onClick = {
                                selectedStatusFilter = status
                                statusExpanded = false
                            }) {
                                Text(if (selectedStatusFilter == status) "✓ $status" else status)
                            }
                        }
                        Divider()
                        DropdownMenuItem(onClick = {
                            selectedDateFilter = null
                            statusExpanded = false
                        }) {
                            Text("All Dates")
                        }
                        availableDates.forEach { date ->
                            DropdownMenuItem(onClick = {
                                selectedDateFilter = date
                                statusExpanded = false
                            }) {
                                Text(if (selectedDateFilter == date) "✓ $date" else date)
                            }
                        }
                    }
                }
            }

            // Bookings List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings) { booking ->
                    UserBookingCard(
                        booking = booking,
                        onClick = {
                            // Navigate to booking details
                            navController?.navigate("user_booking_details/${booking.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserBookingCard(
    booking: UserBooking,
    onClick: () -> Unit = {}
) {
    val primary = AppTheme.Primary
    val primaryDark = AppTheme.PrimaryDark

    val statusColor = when (booking.status) {
        "Pending" -> AppTheme.StatusPending
        "Confirmed" -> AppTheme.StatusConfirmed
        "Completed" -> AppTheme.StatusCompleted
        "Cancelled" -> AppTheme.StatusCancelled
        else -> AppTheme.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    text = booking.clinicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryDark,
                    modifier = Modifier.weight(1f)
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
                Column(modifier = Modifier.weight(1f)) {
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

                Column(modifier = Modifier.weight(1f)) {
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

                Column(modifier = Modifier.weight(1f)) {
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
fun UserHomeScreenPreview() {
    UserHomeScreen(navController = null)
}
