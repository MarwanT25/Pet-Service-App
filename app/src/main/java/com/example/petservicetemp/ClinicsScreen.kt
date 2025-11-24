package com.example.petservicetemp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

// 1. THE "REAL" SCREEN (Has Logic + Firebase)
// This is what the running app uses.
@Composable
fun ClinicsScreen(navController: NavHostController?) {
    val viewModel: ClinicsViewModel = viewModel()
    val clinicsList by viewModel.clinics.collectAsState()

    // Pass the real data to the UI content
    ClinicsContent(
        clinicsList = clinicsList,
        onAddClick = { navController?.navigate("signup_clinic") }
    )
}

// 2. THE "UI ONLY" CONTENT (No Firebase logic here!)
// This just takes a list and draws it. Safe for Previews.
@Composable
fun ClinicsContent(
    clinicsList: List<Clinic>,
    onAddClick: () -> Unit
) {
    val primary = Color(0xFF819067)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veterinary Clinics") },
                backgroundColor = primary,
                contentColor = Color.White
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                backgroundColor = primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Clinic", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(clinicsList) { clinic ->
                ClinicItem(clinic)
            }
        }
    }
}

@Composable
fun ClinicItem(clinic: Clinic) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth().height(120.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(90.dp)
            ) {
                if (clinic.logoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = clinic.logoUrl,
                        contentDescription = "Clinic Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Details
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = clinic.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Location", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(text = clinic.location, color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${clinic.rating} (${clinic.reviews} reviews)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (clinic.isOpen) "Open" else "Closed",
                        color = if (clinic.isOpen) Color(0xFF4CAF50) else Color.Red,
                        fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 3. THE PREVIEW (Uses Fake Data)
@Preview(showBackground = true)
@Composable
fun PreviewClinicsScreen() {
    // Create a fake list so the preview has something to show
    val fakeClinics = listOf(
        Clinic(name = "Happy Pets", location = "Cairo", rating = 4.8, reviews = 120, isOpen = true),
        Clinic(name = "Vet Care", location = "Giza", rating = 4.2, reviews = 85, isOpen = false)
    )

    // Call the Content version, NOT the Screen version
    ClinicsContent(
        clinicsList = fakeClinics,
        onAddClick = {}
    )
}