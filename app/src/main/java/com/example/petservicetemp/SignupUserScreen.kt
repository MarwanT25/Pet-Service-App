package com.example.petservicetemp

import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
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
import android.widget.Toast
import java.util.regex.Pattern

data class Pet(
    val id: Int,
    var petType: String = "",
    var imageUri: Uri? = null,
    var bitmap: android.graphics.Bitmap? = null
)

@Composable
fun SignupUserScreen(navController: NavHostController?) {
    // Colors using MaterialTheme.colors
    val primary = MaterialTheme.colors.primary
    val primaryDark = MaterialTheme.colors.primaryVariant
    val backgroundLight = MaterialTheme.colors.background
    val errorColor = MaterialTheme.colors.error

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numberOfPets by remember { mutableStateOf("") }
    var pets by remember { mutableStateOf(mutableListOf<Pet>()) }

    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Bitmap cache
    val bitmapCache = remember { mutableMapOf<Uri, android.graphics.Bitmap>() }

    val petTypes = listOf("Cat", "Dog")

    // Email validation pattern
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    )

    // Phone validation (Egyptian format)
    val phonePattern = Pattern.compile("^(01)[0-9]{9}\$")

    // Function to load bitmap from URI with caching
    suspend fun loadBitmap(uri: Uri): android.graphics.Bitmap? {
        return bitmapCache[uri] ?: withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.also {
                        bitmapCache[uri] = it
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    // Validation functions
    fun validateName(): Boolean {
        return if (name.isEmpty()) {
            nameError = "Name is required"
            false
        } else if (name.length < 2) {
            nameError = "Name must be at least 2 characters"
            false
        } else {
            nameError = null
            true
        }
    }

    fun validatePhone(): Boolean {
        return if (phone.isEmpty()) {
            phoneError = "Phone is required"
            false
        } else if (!phonePattern.matcher(phone).matches()) {
            phoneError = "Please enter a valid Egyptian phone number (01XXXXXXXXX)"
            false
        } else {
            phoneError = null
            true
        }
    }

    fun validateEmail(): Boolean {
        return if (email.isEmpty()) {
            emailError = "Email is required"
            false
        } else if (!emailPattern.matcher(email).matches()) {
            emailError = "Please enter a valid email address"
            false
        } else {
            emailError = null
            true
        }
    }

    // Update pets list when number changes
    LaunchedEffect(numberOfPets) {
        val count = numberOfPets.toIntOrNull() ?: 0
        if (count > 0 && pets.size != count) {
            pets = (0 until count).map { Pet(id = it) }.toMutableList()
        } else if (count == 0) {
            pets.clear()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Signup User", textAlign = TextAlign.Center) },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                elevation = 4.dp
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 2.dp,
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (nameError != null) validateName()
                        },
                        label = { Text("Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError != null,
                        placeholder = { Text("Enter your full name") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 2.dp,
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '+' || char == '-' || char == ' ' }) {
                                phone = it.filter { char -> char.isDigit() }
                                if (phoneError != null) validatePhone()
                            }
                        },
                        label = { Text("Phone *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = phoneError != null,
                        placeholder = { Text("01XXXXXXXXX") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (phoneError != null) {
                        Text(
                            text = phoneError!!,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 2.dp,
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) validateEmail()
                        },
                        label = { Text("Email *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = emailError != null,
                        placeholder = { Text("example@email.com") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 2.dp,
                backgroundColor = Color.White
            ) {
                OutlinedTextField(
                    value = numberOfPets,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            numberOfPets = it
                        }
                    },
                    label = { Text("Number of Pets") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    singleLine = true,
                    placeholder = { Text("Enter number of pets") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            if (pets.isNotEmpty()) {
                Text(
                    text = "Pet Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryDark,
                    modifier = Modifier.padding(top = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.height((pets.size * 250).dp.coerceAtMost(500.dp)),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pets.size) { index ->
                        val pet = pets[index]
                        PetCard(
                            pet = pet,
                            index = index,
                            petTypes = petTypes,
                            onPetTypeChanged = { newType ->
                                val updatedPets = pets.toMutableList()
                                updatedPets[index] = pet.copy(petType = newType)
                                pets = updatedPets
                            },
                            onImageSelected = { uri ->
                                val updatedPets = pets.toMutableList()
                                updatedPets[index] = pet.copy(imageUri = uri)
                                pets = updatedPets
                            },
                            onBitmapLoaded = { bitmap ->
                                val updatedPets = pets.toMutableList()
                                updatedPets[index] = updatedPets[index].copy(bitmap = bitmap)
                                pets = updatedPets
                            },
                            loadBitmap = { uri -> loadBitmap(uri) },
                            primaryDark = primaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val isNameValid = validateName()
                    val isPhoneValid = validatePhone()
                    val isEmailValid = validateEmail()
                    val petsValid = pets.isEmpty() || pets.all { it.petType.isNotEmpty() }
                    val numberOfPetsValid = numberOfPets.isEmpty() || (numberOfPets.toIntOrNull() ?: 0) > 0

                    if (isNameValid && isPhoneValid && isEmailValid && numberOfPetsValid && petsValid) {
                        Toast.makeText(
                            context,
                            "Account created successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                        navController?.navigate("clinics") {
                            popUpTo("choose_account") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all required fields correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = primary),
                enabled = name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() &&
                        (numberOfPets.isEmpty() || ((numberOfPets.toIntOrNull() ?: 0) > 0 &&
                                pets.all { it.petType.isNotEmpty() }))
            ) {
                Text(
                    text = "Submit",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PetCard(
    pet: Pet,
    index: Int,
    petTypes: List<String>,
    onPetTypeChanged: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onBitmapLoaded: (android.graphics.Bitmap?) -> Unit,
    loadBitmap: suspend (Uri) -> android.graphics.Bitmap?,
    primaryDark: Color
) {
    var expanded by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    LaunchedEffect(pet.imageUri) {
        if (pet.imageUri != null) {
            val bitmap = loadBitmap(pet.imageUri!!)
            onBitmapLoaded(bitmap)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pet ${index + 1}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryDark
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = pet.petType,
                    onValueChange = { },
                    label = { Text("Pet Type *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    readOnly = true,
                    placeholder = { Text("Choose Cat or Dog") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.clickable { expanded = true }
                        )
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    petTypes.forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                                onPetTypeChanged(type)
                                expanded = false
                            }
                        ) {
                            Text(type)
                        }
                    }
                }
            }

            Text(
                text = "Pet Image (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = primaryDark
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clickable { imageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (pet.bitmap != null) {
                    Image(
                        bitmap = pet.bitmap!!.asImageBitmap(),
                        contentDescription = "Pet Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Click to add pet image", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SignupUserScreenPreview() {
    SignupUserScreen(navController = null)
}
