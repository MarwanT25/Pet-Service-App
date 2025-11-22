package com.example.petservicetemp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
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

@Composable
fun ClinicProfileScreen(
    clinicName: String = "Clinic Name",
    navController: NavHostController?
) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)

    var isEditing by remember { mutableStateOf(false) }
    var clinicNameState by remember { mutableStateOf(clinicName) }
    var address by remember { mutableStateOf("Cairo, Egypt") }
    var city by remember { mutableStateOf("Cairo") }
    var phone by remember { mutableStateOf("01234567890") }
    var workingHours by remember { mutableStateOf("9am - 8pm") }
    var aboutClinic by remember { mutableStateOf("We provide the best pet care services...") }
    var services by remember { mutableStateOf("Grooming, Checkup, Vaccine") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", textAlign = TextAlign.Center) },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
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
            // Clinic Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Clinic Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )

                    OutlinedTextField(
                        value = clinicNameState,
                        onValueChange = { if (isEditing) clinicNameState = it },
                        label = { Text("Clinic Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { if (isEditing) address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { if (isEditing) city = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (isEditing) phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = workingHours,
                        onValueChange = { if (isEditing) workingHours = it },
                        label = { Text("Working Hours") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )
                }
            }

            // About Clinic Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "About Clinic",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )

                    OutlinedTextField(
                        value = aboutClinic,
                        onValueChange = { if (isEditing) aboutClinic = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        enabled = isEditing,
                        readOnly = !isEditing,
                        maxLines = 5
                    )
                }
            }

            // Services Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Services",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )

                    OutlinedTextField(
                        value = services,
                        onValueChange = { if (isEditing) services = it },
                        label = { Text("Services Offered") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing,
                        placeholder = { Text("e.g., Grooming, Checkup, Vaccine") }
                    )
                }
            }

            // Save Button (only shown when editing)
            if (isEditing) {
                Button(
                    onClick = {
                        // Save changes
                        println("Saving clinic profile:")
                        println("Name: $clinicNameState")
                        println("Address: $address")
                        println("City: $city")
                        println("Phone: $phone")
                        println("Working Hours: $workingHours")
                        println("About: $aboutClinic")
                        println("Services: $services")
                        isEditing = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = primary)
                ) {
                    Text(
                        text = "Save Changes",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Logout Button
            Button(
                onClick = {
                    // Logout and navigate to choose account screen
                    navController?.navigate("choose_account") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336))
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun ClinicProfileScreenPreview() {
    ClinicProfileScreen(clinicName = "Happy Paws Clinic", navController = null)
}

