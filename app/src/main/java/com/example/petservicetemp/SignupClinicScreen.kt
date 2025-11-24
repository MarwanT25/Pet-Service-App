package com.example.petservicetemp

import android.widget.Toast
import android.net.Uri
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SignupClinicScreen(navController: NavHostController?) {
    val primary = Color(0xFF819067)
    val backgroundLight = Color(0xFFF8F8F8)

    val context = LocalContext.current

    // Form State
    var clinicName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var workingHours by remember { mutableStateOf("") }
    var selectedServices by remember { mutableStateOf(setOf<String>()) }

    // Image State
    var clinicImageUri by remember { mutableStateOf<Uri?>(null) }
    var licenseImageUri by remember { mutableStateOf<Uri?>(null) }
    var clinicBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var licenseBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Loading State (NEW)
    var isLoading by remember { mutableStateOf(false) }

    // Available services list
    val availableServices = listOf(
        "Grooming", "Checkup", "Vaccine", "Surgery", "Boarding",
        "Daycare", "Emergency Care", "Dental Care", "X-Ray", "Laboratory Tests"
    )

    // Function to load bitmap from URI
    suspend fun loadBitmap(uri: Uri): android.graphics.Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    // Launchers
    val clinicImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> clinicImageUri = uri }

    val licenseImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> licenseImageUri = uri }

    // Effects to load bitmaps
    LaunchedEffect(clinicImageUri) {
        clinicBitmap = if (clinicImageUri != null) loadBitmap(clinicImageUri!!) else null
    }
    LaunchedEffect(licenseImageUri) {
        licenseBitmap = if (licenseImageUri != null) loadBitmap(licenseImageUri!!) else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Signup Clinic", textAlign = TextAlign.Center) },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = backgroundLight
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = clinicName, onValueChange = { clinicName = it },
                label = { Text("Clinic Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("City") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            OutlinedTextField(
                value = workingHours, onValueChange = { workingHours = it },
                label = { Text("Working Hours") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, placeholder = { Text("e.g., 9am - 8pm") }
            )

            // Services Selection
            Text("Services *", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(8.dp), elevation = 2.dp
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(availableServices) { service ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedServices = if (selectedServices.contains(service)) selectedServices - service else selectedServices + service
                            }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedServices.contains(service),
                                onCheckedChange = { selectedServices = if (it) selectedServices + service else selectedServices - service }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(service)
                        }
                    }
                }
            }

            // Clinic Logo
            Text("Clinic Logo", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(150.dp)
                    .border(2.dp, if (clinicImageUri == null) Color.Red else Color.Gray, RoundedCornerShape(8.dp))
                    .clickable { clinicImageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (clinicBitmap != null) {
                    Image(bitmap = clinicBitmap!!.asImageBitmap(), contentDescription = "Logo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Text("Add Logo Image", color = Color.Gray)
                }
            }

            // License Picture
            Text("License Image", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(150.dp)
                    .border(2.dp, if (licenseImageUri == null) Color.Red else Color.Gray, RoundedCornerShape(8.dp))
                    .clickable { licenseImageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (licenseBitmap != null) {
                    Image(bitmap = licenseBitmap!!.asImageBitmap(), contentDescription = "License", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Text("Add License Image", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (clinicName.isNotEmpty() && clinicImageUri != null && licenseImageUri != null) {

                        // 1. POPUP: Proves the button actually clicked
                        Toast.makeText(context, "Step 1: Starting Upload...", Toast.LENGTH_SHORT).show()

                        isLoading = true
                        val repository = ClinicRepository()

                        // --- Upload Logo ---
                        repository.uploadImage(
                            imageUri = clinicImageUri!!,
                            onFailure = { e ->
                                isLoading = false
                                // ERROR POPUP
                                Toast.makeText(context, "❌ Error Uploading Logo: ${e.message}", Toast.LENGTH_LONG).show()
                            },
                            onSuccess = { logoUrl ->
                                // 2. POPUP: Logo worked
                                Toast.makeText(context, "Step 2: Logo Done! Uploading License...", Toast.LENGTH_SHORT).show()

                                // --- Upload License ---
                                repository.uploadImage(
                                    imageUri = licenseImageUri!!,
                                    onFailure = { e ->
                                        isLoading = false
                                        // ERROR POPUP
                                        Toast.makeText(context, "❌ Error Uploading License: ${e.message}", Toast.LENGTH_LONG).show()
                                    },
                                    onSuccess = { licenseUrl ->

                                        // --- Save to Firestore ---
                                        val newClinic = Clinic(
                                            name = clinicName,
                                            location = "$address, $city",
                                            phoneNumber = phone,
                                            isOpen = true,
                                            rating = 5.0,
                                            reviews = 0,
                                            logoUrl = logoUrl,
                                            licenseUrl = licenseUrl
                                        )

                                        repository.addClinic(newClinic) { isSuccess ->
                                            isLoading = false
                                            if (isSuccess) {
                                                // SUCCESS POPUP
                                                Toast.makeText(context, "✅ SUCCESS! Data Saved.", Toast.LENGTH_LONG).show()
                                                navController?.navigate("clinics") {
                                                    popUpTo("choose_account") { inclusive = true }
                                                }
                                            } else {
                                                // DATABASE ERROR POPUP
                                                Toast.makeText(context, "❌ Database Save Failed!", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    } else {
                        Toast.makeText(context, "⚠️ Please fill Name and Images", Toast.LENGTH_SHORT).show()
                    }
                },
                // ... keep your modifiers and colors same as before ...
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = primary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit (Debug Mode)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview
@Composable
fun SignupClinicScreenPreview() {
    SignupClinicScreen(navController = null)
}