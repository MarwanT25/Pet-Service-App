package com.example.petservicetemp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserProfileScreen(
    navController: NavHostController?
) {
    val context = LocalContext.current
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)
    val auth = FirebaseAuth.getInstance()

    // قراءة البيانات المحفوظة من SharedPreferences
    val (savedUserName, savedUserEmail) = remember {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "") ?: ""
        val email = prefs.getString("user_email", "") ?: ""
        Pair(name, email)
    }

    // ViewModel
    val userViewModel: UserViewModel = viewModel()

    // Collect State من الـ ViewModel
    val userName by userViewModel.userName.collectAsState()
    val userEmail by userViewModel.userEmail.collectAsState()
    val userPhone by userViewModel.userPhone.collectAsState()
    val userPets by userViewModel.userPets.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    // استخدام البيانات المحفوظة إذا كانت بيانات الـ ViewModel فارغة
    val displayName = remember(savedUserName, userName) {
        when {
            userName.isNotEmpty() -> userName
            savedUserName.isNotEmpty() -> savedUserName
            auth.currentUser?.displayName?.isNotEmpty() == true -> auth.currentUser?.displayName ?: ""
            else -> "User"
        }
    }

    val displayEmail = remember(savedUserEmail, userEmail) {
        when {
            userEmail.isNotEmpty() -> userEmail
            savedUserEmail.isNotEmpty() -> savedUserEmail
            auth.currentUser?.email?.isNotEmpty() == true -> auth.currentUser?.email ?: ""
            else -> "user@example.com"
        }
    }

    // الحصول على الاسم الأول للحرف الأول في الصورة
    val firstLetter = remember(displayName) {
        if (displayName.isNotEmpty()) {
            displayName.first().toString().uppercase()
        } else {
            "U"
        }
    }

    // Local States للتعديل
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(displayName) }
    var editedPhone by remember { mutableStateOf(userPhone) }
    var editedPets by remember { mutableStateOf(userPets) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // تحديث الحقول المحلية عند تحميل البيانات
    LaunchedEffect(displayName, userPhone, userPets) {
        editedName = displayName
        editedPhone = userPhone
        editedPets = userPets
    }

    // إعادة تحميل البيانات عند فتح الشاشة
    LaunchedEffect(Unit) {
        userViewModel.refreshUserData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController != null) {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isEditing = !isEditing
                        if (!isEditing) {
                            // عند الخروج من وضع التعديل، أعد تعيين القيم
                            editedName = displayName
                            editedPhone = userPhone
                            editedPets = userPets
                        }
                    }) {
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar مع الحرف الأول من الاسم
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(primary),
                contentAlignment = Alignment.Center
            ) {
                if (displayName.isNotEmpty()) {
                    Text(
                        text = firstLetter,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "User Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // الاسم الكامل
            Text(
                text = displayName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryDark
            )

            // البريد الإلكتروني
            Text(
                text = displayEmail,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // معلومات المستخدم الرئيسية
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // معلومات الحساب
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📋 Account Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryDark
                        )

                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { if (isEditing) editedName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            readOnly = !isEditing,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = primary,
                                focusedLabelColor = primary,
                                cursorColor = primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editedPhone.ifEmpty { "Not provided" },
                            onValueChange = { if (isEditing) editedPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            readOnly = !isEditing,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = primary,
                                focusedLabelColor = primary,
                                cursorColor = primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = displayEmail,
                            onValueChange = { /* Email لا يمكن تعديله */ },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            readOnly = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledBorderColor = Color.LightGray,
                                disabledLabelColor = Color.Gray,
                                disabledTextColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    // الحيوانات الأليفة
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🐾 My Pets",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryDark
                            )

                            if (isEditing) {
                                OutlinedTextField(
                                    value = editedPets.size.toString(),
                                    onValueChange = {
                                        if (isEditing && it.all { char -> char.isDigit() }) {
                                            val count = it.toIntOrNull() ?: 0
                                            if (count >= 0) {
                                                if (count > editedPets.size) {
                                                    // إضافة حيوانات جديدة فارغة
                                                    editedPets = editedPets + List(count - editedPets.size) { "" }
                                                } else if (count < editedPets.size) {
                                                    // إزالة الحيوانات الزائدة
                                                    editedPets = editedPets.take(count)
                                                }
                                            }
                                        }
                                    },
                                    label = { Text("Number of Pets") },
                                    modifier = Modifier.width(120.dp),
                                    enabled = isEditing,
                                    readOnly = !isEditing,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = primary,
                                        focusedLabelColor = primary,
                                        cursorColor = primary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        if (editedPets.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                editedPets.forEachIndexed { index, petType ->
                                    OutlinedTextField(
                                        value = petType.ifEmpty { "Pet ${index + 1}" },
                                        onValueChange = {
                                            if (isEditing) {
                                                val updatedPets = editedPets.toMutableList()
                                                updatedPets[index] = it
                                                editedPets = updatedPets
                                            }
                                        },
                                        label = { Text("Pet ${index + 1}") },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = isEditing,
                                        readOnly = !isEditing,
                                        placeholder = { Text("Enter pet type...") },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = primary,
                                            focusedLabelColor = primary,
                                            cursorColor = primary
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No pets added yet",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    // أزرار الإجراءات
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isEditing) {
                            // أزرار التعديل
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // إلغاء التعديلات
                                        editedName = displayName
                                        editedPhone = userPhone
                                        editedPets = userPets
                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.LightGray
                                    )
                                ) {
                                    Text(
                                        text = "Cancel",
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = {
                                        // تحديث البيانات في Firebase
                                        userViewModel.updateUserProfile(
                                            name = editedName,
                                            phone = editedPhone,
                                            pets = editedPets.filter { it.isNotEmpty() }
                                        )

                                        // حفظ في SharedPreferences أيضاً
                                        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                        prefs.edit().apply {
                                            putString("user_name", editedName)
                                            apply()
                                        }

                                        Toast.makeText(
                                            context,
                                            "✅ Profile updated successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = primary
                                    ),
                                    enabled = editedName.isNotEmpty()
                                ) {
                                    Text(
                                        text = "Save",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // أزرار العرض العادي
                            Button(
                                onClick = {
                                    userViewModel.refreshUserData()
                                    Toast.makeText(
                                        context,
                                        "🔄 Refreshing profile data...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF2196F3)
                                ),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 8.dp
                                )
                            ) {
                                Text(
                                    text = "🔄 Refresh Data",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                showLogoutDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFF44336)
                            ),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // معلومات إضافية للتصحيح
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFFF5F5F5),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🔍 Session Info",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Firebase User: ${auth.currentUser?.email ?: "Not logged in"}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "User ID: ${currentUser?.id ?: auth.currentUser?.uid ?: "No ID"}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Loaded Pets: ${userPets.size}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        "Logout",
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Are you sure you want to logout?")
                        Text(
                            "You'll need to login again to access your profile.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // تسجيل الخروج
                            userViewModel.logout()

                            // مسح البيانات المحفوظة
                            val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                            prefs.edit().clear().apply()

                            Toast.makeText(
                                context,
                                "✅ Logged out successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            showLogoutDialog = false

                            // العودة إلى شاشة اختيار الحساب
                            navController?.navigate("choose_account") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Logout", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", color = primary)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White
            )
        }
    }
}

@Preview
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen(navController = null)
}