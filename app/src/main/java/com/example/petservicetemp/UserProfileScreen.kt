package com.example.petservicetemp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import android.widget.Toast
import android.content.Context
import androidx.compose.foundation.background

@Composable
fun UserProfileScreen(
    navController: NavHostController?
) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)

    var isEditing by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Ahmed Mohamed") }
    var userPhone by remember { mutableStateOf("01234567890") }
    var userEmail by remember { mutableStateOf("ahmed@example.com") }
    var numberOfPets by remember { mutableStateOf("2") }
    var pets by remember { mutableStateOf(listOf("Cat", "Dog")) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Update pets list when numberOfPets changes
    LaunchedEffect(numberOfPets) {
        val count = numberOfPets.toIntOrNull() ?: 0
        if (count > 0 && pets.size != count) {
            pets = (0 until count).map { index ->
                if (index < pets.size) pets[index] else ""
            }
        } else if (count == 0) {
            pets = emptyList()
        }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            // User Info Card
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
                        text = "Personal Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )

                    OutlinedTextField(
                        value = userName,
                        onValueChange = { if (isEditing) userName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = userPhone,
                        onValueChange = { if (isEditing) userPhone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { if (isEditing) userEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )

                    OutlinedTextField(
                        value = numberOfPets,
                        onValueChange = {
                            if (isEditing && it.all { char -> char.isDigit() }) {
                                numberOfPets = it
                            }
                        },
                        label = { Text("Number of Pets") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing,
                        placeholder = { Text("Enter number of pets") }
                    )
                }
            }

            // Pets Card
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
                        text = "My Pets",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )

                    pets.forEachIndexed { index, petType ->
                        OutlinedTextField(
                            value = petType,
                            onValueChange = { if (isEditing) {
                                val updatedPets = pets.toMutableList()
                                updatedPets[index] = it
                                pets = updatedPets
                            } },
                            label = { Text("Pet ${index + 1} Type") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            readOnly = !isEditing
                        )
                    }
                }
            }

            // Save Button (only shown when editing)
            if (isEditing) {
                SaveButtonWithToast(
                    onSave = { isEditing = false }
                )
            }

            // Logout Button
            Button(
                onClick = {
                    showLogoutDialog = true
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

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Logout", fontWeight = FontWeight.Bold) },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        LogoutButtonWithToast(
                            onLogout = {
                                showLogoutDialog = false
                                navController?.navigate("choose_account") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SaveButtonWithToast(onSave: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Button(
        onClick = {
            Toast.makeText(
                context,
                "Profile updated successfully!",
                Toast.LENGTH_SHORT
            ).show()
            onSave()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF819067))
    ) {
        Text(
            text = "Save Changes",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogoutButtonWithToast(onLogout: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Button(
        onClick = {
            Toast.makeText(
                context,
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()
            onLogout()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336))
    ) {
        Text("Logout", color = Color.White)
    }
}

@Preview
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen(navController = null)
}

