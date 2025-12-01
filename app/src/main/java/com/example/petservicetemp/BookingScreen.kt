package com.example.petservicetemp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth // <--- Added
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clinicName = intent.getStringExtra("clinicName") ?: "Clinic Name"
        setContent {
            BookingScreenStyled(clinicName = clinicName)
        }
    }
}

@Preview
@Composable
fun BookingScreenStyled(
    clinicName: String = "Clinic Name",
    rating: Double = 4.5,
    isOpen: Boolean = true,
    location: String = "Cairo, Egypt",
    reviews: Int = 0,
    phoneNumber: String = "",
    navController: NavHostController? = null
) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)

    // Firebase Instances
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance() // <--- Get Auth Instance

    var selectedService by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Loading state to prevent double clicking
    var isBooking by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(clinicName, textAlign = TextAlign.Center) },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundLight)
                    .padding(innerPadding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clinic Card Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(primaryDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("LOGO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(clinicName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$rating", fontSize = 12.sp)
                                if (reviews > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("($reviews reviews)", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Text("📍 $location    ${if (isOpen) "🟢 Open" else "🔴 Closed"}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                // Services Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🛎Select Service:", fontWeight = FontWeight.Bold)
                        val services = listOf("Grooming", "Checkup", "Vaccine")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(services.size) { index ->
                                val service = services[index]
                                Button(
                                    onClick = { selectedService = service },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (selectedService == service) primary else Color.LightGray
                                    )
                                ) {
                                    Text(service, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Date Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select Date:", fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH)
                                val day = calendar.get(Calendar.DAY_OF_MONTH)

                                val datePickerDialog = DatePickerDialog(
                                    context,
                                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                                        selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                                    },
                                    year,
                                    month,
                                    day
                                )
                                datePickerDialog.show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.LightGray
                            )
                        ) {
                            Text(
                                if (selectedDate.isEmpty()) "Select Date" else selectedDate,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Time Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select Time:", fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                try {
                                    val act = context as Activity
                                    TimePickerDialog(
                                        act,
                                        { _, hourOfDay, minute ->
                                            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                } catch (e: Exception) {
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.LightGray
                            )
                        ) {
                            Text(
                                if (selectedTime.isEmpty()) "Select Time" else selectedTime,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Notes Section
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Summary & Confirm
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Service: $selectedService")
                        Text("Date: $selectedDate — $selectedTime")
                        Text("Price: 250 EGP")
                        Spacer(modifier = Modifier.height(8.dp))


                        Button(
                            onClick = {
                                if (isBooking) return@Button // Prevent double clicks

                                val user = auth.currentUser


                                android.util.Log.d("AUTH_DEBUG", "User object: $user")
                                android.util.Log.d("AUTH_DEBUG", "User Email: ${user?.email}")
                                android.util.Log.d("AUTH_DEBUG", "Is Anonymous: ${user?.isAnonymous}")

                                if (user?.email != null) {
                                    isBooking = true

                                    // Prepare data for Firestore
                                    val bookingData = hashMapOf(
                                        "userId" to user.email, // Using Email as ID
                                        "clinicName" to clinicName,
                                        "service" to selectedService,
                                        "date" to selectedDate,
                                        "time" to selectedTime,
                                        "status" to "Pending",
                                        "notes" to notes
                                    )

                                    // Save to Firestore
                                    db.collection("bookings")
                                        .add(bookingData)
                                        .addOnSuccessListener {
                                            isBooking = false
                                            Toast.makeText(context, "Booking confirmed!", Toast.LENGTH_LONG).show()
                                            navController?.popBackStack()
                                        }
                                        .addOnFailureListener { e ->
                                            isBooking = false
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }

                                } else {
                                    Toast.makeText(context, "Log in el awel ya3m", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = primary),
                            enabled = !isBooking && selectedService.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
                        ) {
                            if (isBooking) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("CONFIRM BOOKING", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    )
}