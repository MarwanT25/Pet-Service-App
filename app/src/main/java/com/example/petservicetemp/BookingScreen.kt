package com.example.petservicetemp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clinicName = intent.getStringExtra("clinicName") ?: "Clinic Name"
        val clinicId = intent.getStringExtra("clinicId") ?: ""

        // ========== الحصول على الـ email المحفوظ ==========
        val savedEmail = getSavedUserEmail()
        val userEmail = if (savedEmail.isNotEmpty()) {
            savedEmail
        } else {
            // محاولة الحصول من Firebase Auth
            FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"
        }

        // محاولة الحصول على اسم المستخدم
        val userName = intent.getStringExtra("userName") ?:
        FirebaseAuth.getInstance().currentUser?.displayName ?:
        "Guest User"

        setContent {
            BookingScreenStyled(
                clinicName = clinicName,
                clinicId = clinicId,
                userEmail1 = userEmail,
                userName1 = userName
            )
        }
    }

    // ========== دالة لقراءة الـ email من SharedPreferences ==========
    private fun getSavedUserEmail(): String {
        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return prefs.getString("user_email", "") ?: ""
    }
}

@Composable
fun BookingScreenStyled(
    clinicName: String = "Clinic Name",
    clinicId: String = "",
    rating: Double = 4.5,
    isOpen: Boolean = true,
    location: String = "Cairo, Egypt",
    reviews: Int = 0,
    phoneNumber: String = "",
    userEmail1: String = "", // **جديد: email المستخدم من الـ navigation**
    userName1: String = "",  // **جديد: اسم المستخدم من الـ navigation**
    navController: NavHostController? = null
) {
    val primary = Color(0xFF819067)
    val backgroundLight = Color(0xFFF8F8F8)

    // Firebase Instances
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // ========== دالة لقراءة الـ email من SharedPreferences ==========
    fun getSavedUserEmail(): String {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return prefs.getString("user_email", "") ?: ""
    }

    // ========== دالة للحصول على الـ email من Firebase Auth ==========
    fun getFirebaseUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    // ========== تحديد الـ email النهائي ==========
    val finalUserEmail = remember(userEmail1) {
        when {
            userEmail1.isNotEmpty() -> userEmail1
            getSavedUserEmail().isNotEmpty() -> getSavedUserEmail()
            getFirebaseUserEmail().isNotEmpty() -> getFirebaseUserEmail()
            else -> "guest@example.com"
        }
    }

    val finalUserName = remember(userName1) {
        when {
            userName1.isNotEmpty() -> userName1
            auth.currentUser?.displayName?.isNotEmpty() == true -> auth.currentUser?.displayName ?: "Guest"
            else -> "Guest User"
        }
    }

    // States
    var selectedService by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isBooking by remember { mutableStateOf(false) }

    // States for services
    var servicesList by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingServices by remember { mutableStateOf(true) }

    // Fetch clinic data from Firebase
    LaunchedEffect(clinicId) {
        if (clinicId.isNotEmpty()) {
            db.collection("clinics").document(clinicId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Get services
                        val services = document.get("services") as? List<String> ?: emptyList()
                        servicesList = services
                        isLoadingServices = false
                        Log.d("BOOKING", "✅ Loaded services: $servicesList")
                        Log.d("BOOKING", "📧 Final user email: $finalUserEmail")
                        Log.d("BOOKING", "👤 Final user name: $finalUserName")
                    } else {
                        isLoadingServices = false
                        Toast.makeText(context, "Clinic not found", Toast.LENGTH_SHORT).show()
                        Log.d("BOOKING", "❌ Clinic not found: $clinicId")
                    }
                }
                .addOnFailureListener {
                    isLoadingServices = false
                    Toast.makeText(context, "Failed to load clinic data", Toast.LENGTH_SHORT).show()
                    Log.d("BOOKING", "❌ Failed to load clinic data")
                }
        } else {
            isLoadingServices = false
            // Use default services if no clinicId
            servicesList = listOf("Grooming", "Checkup", "Vaccine")
            Log.d("BOOKING", "⚠️ Using default services, no clinicId provided")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        clinicName,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundLight)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
        ) {
            // Clinic Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Clinic Name
                        Text(
                            clinicName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // عرض الـ rating
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
                            if (reviews > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("(${reviews} Reviews)", fontSize = 14.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status
                        Text(
                            if (isOpen) "Open Now" else "Currently Closed",
                            fontSize = 16.sp,
                            color = if (isOpen) Color(0xFF4CAF50) else Color.Red,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Location
                        Text(
                            "$location",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        // **معلومات المستخدم المحفوظة**
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Booking for: $finalUserName",
                                fontSize = 12.sp,
                                color = Color(0xFF819067),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Email: $finalUserEmail",
                                fontSize = 10.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Services Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🛎 Select Service:", fontWeight = FontWeight.Bold)

                        if (isLoadingServices) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Loading services...", fontSize = 14.sp, color = Color.Gray)
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(servicesList.size) { index ->
                                    val service = servicesList[index]
                                    Button(
                                        onClick = { selectedService = service },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = if (selectedService == service) primary else Color.LightGray
                                        ),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(service, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                            if (servicesList.isEmpty()) {
                                Text(
                                    "No services available",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Date Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
            }

            // Time Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
            }

            // Notes Section
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            // Summary & Confirm
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Service: ${selectedService.ifEmpty { "Not selected" }}")
                        Text("Date: ${selectedDate.ifEmpty { "Not selected" }} — ${selectedTime.ifEmpty { "Not selected" }}")
                        Text("Price: 250 EGP")

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                "Booking for: $finalUserName",
                                fontSize = 12.sp,
                                color = Color(0xFF819067),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Email: $finalUserEmail",
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (isBooking) return@Button

                                // **التحقق من صحة البيانات**
                                Log.d("BOOKING", "✅ Booking started for: $finalUserEmail ($finalUserName)")
                                Log.d("BOOKING", "📋 Service: $selectedService, Date: $selectedDate, Time: $selectedTime")

                                // **تأكد من وجود البيانات المطلوبة**
                                if (selectedService.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                                    Toast.makeText(context, "Please select service, date and time", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isBooking = true

                                // Include clinicId in booking data
                                val bookingData = hashMapOf(
                                    "userId" to finalUserEmail, // **استخدام email المستخدم**
                                    "userName" to finalUserName, // **استخدام اسم المستخدم**
                                    "clinicId" to clinicId,
                                    "clinicName" to clinicName,
                                    "service" to selectedService,
                                    "date" to selectedDate,
                                    "time" to selectedTime,
                                    "status" to "Pending",
                                    "notes" to notes,
                                    "timestamp" to System.currentTimeMillis(),
                                    "price" to 250.0
                                )

                                Log.d("BOOKING", "💾 Saving booking data for: $finalUserEmail")
                                Log.d("BOOKING", "📊 Booking data: $bookingData")

                                // Save to Firestore
                                db.collection("bookings")
                                    .add(bookingData)
                                    .addOnSuccessListener {
                                        isBooking = false
                                        Log.d("BOOKING", "✅ Booking saved successfully! Document ID: ${it.id}")
                                        Toast.makeText(context, "Booking confirmed for $finalUserName!", Toast.LENGTH_LONG).show()
                                        navController?.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        isBooking = false
                                        Log.e("BOOKING", "❌ Failed to save booking: ${e.message}", e)
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = primary),
                            enabled = !isBooking && selectedService.isNotEmpty() &&
                                    selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
                        ) {
                            if (isBooking) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text("CONFIRM BOOKING", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Add extra space at the bottom for scrolling
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun BookingScreenPreview() {
    BookingScreenStyled(
        clinicName = "Pet Care Clinic",
        clinicId = "test_clinic_id",
        rating = 4.5,
        isOpen = true,
        location = "Cairo, Egypt",
        reviews = 120,
        phoneNumber = "01234567890",
        userEmail1 = "user@example.com",
        userName1 = "Test User"
    )
}