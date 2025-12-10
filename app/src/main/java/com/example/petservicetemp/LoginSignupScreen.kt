package com.example.petservicetemp
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.browseractions.BrowserServiceFileProvider.loadBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun LoginSignupScreen(
    accountType: String,

    navController: NavHostController?
) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)
    val context = LocalContext.current
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var idclinic by remember { mutableStateOf("") }
    var Rating by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var workingHours by remember { mutableStateOf("") }
    var selectedServices by remember { mutableStateOf(setOf<String>()) }
    var clinicImageUri by remember { mutableStateOf<Uri?>(null) }
    var licenseImageUri by remember { mutableStateOf<Uri?>(null) }
    var clinicBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var licenseBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var numberOfPets by remember { mutableStateOf("") }
    var pets by remember { mutableStateOf(mutableListOf<Petss>()) }

    val availableServices = listOf(
        "Grooming", "Checkup", "Vaccine", "Surgery", "Boarding",
        "Daycare", "Emergency Care", "Dental Care", "X-Ray", "Laboratory Tests"
    )
    /**(3)loadBitmap(uri: Uri)
     * عندك URI (رابط يشاور على صورة في الجهاز).
    عايزة تحولي الصورة دي لـ Bitmap علشان Jetpack Compose يعرف يعرضها.
    وظيفتها: تاخد صورة من جهاز المستخدم  على شكل URI، وتقراها وتحوّلها لصورة Bitmap جاهزة للعرض أو المعالجه*
    كيف بتشتغل:
    تشتغل في خيط خلفي (Dispatchers.IO) عشان قراءة الملفات عملية ثقيلة وما توقفش واجهة المستخدم.
    تفتح تيار البيانات (InputStream) من الـ URI.
    تحول البيانات لصورة Bitmap (BitmapFactory.decodeStream).
    لو فيه أي مشكلة أثناء القراءة (مثلاً الصورة مش موجودة) → ترجع null.
    باختصار: تاخد صورة من الجهاز وتعملها Bitmap.
     */

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







    /**(1)clinicImageLauncher و licenseImageLauncher

    وظيفتها: فتح معرض الصور لاختيار صورة للعيادة (clinic) أو صورة الرخصة (license).

    كيف بتشتغل:

    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) → تفتح اختيار صورة.

    بعد ما المستخدم يختار الصورة → تحط الـ URI في المتغير المناسب (clinicImageUri أو licenseImageUri).

    الفرق بينهم: واحد للـ logo وواحد للرخصة.*/
    val clinicImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        clinicImageUri = uri
    }

    val licenseImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        licenseImageUri = uri
    }

    /**(2)(4)LaunchedEffect(clinicImageUri) و LaunchedEffect(licenseImageUri)

    وظيفتها: كل ما الـ URI يتغير (المستخدم يختار صورة جديدة)، تتحول الصورة لـ Bitmap.

    كيف بتشتغل:
    تتحقق إذا الـ URI موجود.

    تستخدم loadBitmap(uri) لتحويل الصورة لـ Bitmap.

    تحفظ النتيجة في المتغير (clinicBitmap أو licenseBitmap).

    لو مفيش URI → تفرغ المتغير (null).

    الفرق بينهم: واحد يتابع صورة العيادة، والتاني يتابع صورة الرخصة.*/
    LaunchedEffect(clinicImageUri) {
        if (clinicImageUri != null) {
            clinicBitmap = loadBitmap(clinicImageUri!!)
        } else {
            clinicBitmap = null
        }
    }

    LaunchedEffect(licenseImageUri) {
        if (licenseImageUri != null) {
            licenseBitmap = loadBitmap(licenseImageUri!!)
        } else {
            licenseBitmap = null
        }
    }

/**. (5)bitmapToBase64(bitmap: Bitmap?)

وظيفتها: تحول صورة Bitmap لسلسلة نصية Base64، عشان تبعتها لأي API أو تخزنها في قاعدة بيانات.

كيف بتشتغل:

لو الصورة موجودة (bitmap != null) → تجهز تيار بايت (ByteArrayOutputStream).

تضغط الصورة بصيغة JPEG بجودة 70% وتحطها في التيار.

تحول التيار لمصفوفة بايت (byteArray).

تحول المصفوفة لسلسلة نصية Base64.

لو الصورة مش موجودة → ترجع سلسلة فاضية "".

باختصار: تاخد Bitmap وتعمله String جاهزة للرفع أو التخزين.*/

    fun bitmapToBase64(bitmap: android.graphics.Bitmap?): String {
        return if  (bitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } else {
            ""
        }
    }

/**LaunchedEffect(numberOfPets)
وظيفتها: تتابع تغيّر عدد الحيوانات الأليفة (numberOfPets) عند المستخدم.
كيف بتشتغل:
لما المستخدم يغيّر الرقم، تتحقق الدالة:
لو الرقم > 0 وعدد عناصر pets مش مساوي للرقم → تنشئ قائمة جديدة من Petss() لكل حيوان.
لو الرقم = 0 → تفرغ القائمة.
الفرق هنا: دي مش بتتعامل مع الصور مباشرة، بس بتتابع المتغير وتعمل تحديث للقائمة.*/
    LaunchedEffect(numberOfPets) {
        if (accountType == "user") {
            val count = numberOfPets.toIntOrNull() ?: 0//تحويل النص لرقم
            if (count > 0 && pets.size != count) {
                pets = (0 until count)//دي بتعمل range من الأرقام
                    .map { Petss() }//map يعني: "لكل رقم في الـ range، نفّذي الكود ده".
                   //اعمل object جديد من Petss() لكل دورة".
                    .toMutableList()//فـ toMutableList() بتحوّلها إلى list قابلة للتعديل.
            } else if (count == 0) {
                pets.clear()
            }
        }
    }
    /**أنتي بتطلبي ViewModel اسمه ClinicsViewModel.

    Compose هيدوّر:
    هل فيه ViewModel اسم ClinicsViewModel موجود already للشاشة دي؟
    لو نعم → يديهولك.
    لو لا → ينشئ واحد جديد ويخزّنه.
    النتيجة:
    عندك object من ClinicsViewModel جاهز تتعاملي معاه:
    قراءة بيانات – استدعاء دوال – متابعة الـ State.*/
    val clinicsViewModel: ClinicsViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (accountType == "clinic") "Clinic Account" else "User Account",
                        textAlign = TextAlign.Center
                    )
                },
                backgroundColor = primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = backgroundLight
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.good_doggy_bro_1),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { isLogin = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (isLogin) primary else Color.LightGray
                        ),
                        shape = RoundedCornerShape(topStart = 40.dp,
                            topEnd = 40.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp)
                    ) {
                        Text("Login", color = if (isLogin) Color.White else Color.Black)
                    }

                    Button(
                        onClick = { isLogin = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (!isLogin) primary else Color.LightGray
                        ),
                        shape = RoundedCornerShape(topStart = 40.dp,
                            topEnd = 40.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp)
                    ) {
                        Text("Signup", color = if (!isLogin) Color.White else Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (accountType == "clinic") {
                    if (isLogin) {

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )


                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    } else {

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )


                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )


                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = clinicName,
                            onValueChange = { clinicName = it },
                            label = { Text("Clinic Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = workingHours,
                            onValueChange = { workingHours = it },
                            label = { Text("Working Hours") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("e.g., 9am - 8pm") }
                        )

                        OutlinedTextField(
                            value = Rating,
                            onValueChange = { Rating = it },
                            label = { Text("Rating") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Text(
                            text = "Services *",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = 2.dp
                        ) {
                            LazyColumn(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(availableServices) { service ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedServices =
                                                    if (selectedServices.contains(service)) {/**بيتأكد إذا الخدمة موجودة بالفعل في المجموعة.لو موجودة (true):*/
                                                        selectedServices - service/** بيعمل selectedServices - service → يعني يحذف الخدمة من المجموعة.*/
                                                    } else {
                                                        selectedServices + service/**يعمل selectedServices + service → يعني يضيف الخدمة للمجموعة.*/
                                                    }
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedServices.contains(service),//لو الخدمة موجودة في selectedServices → الـ Checkbox هيبقى متعلم (checked).
                                            onCheckedChange = {//ده الحدث اللي بيتنفذ لما المستخدم يضغط على الـ Checkbox
                                                selectedServices = if (it) {//it هنا بيكون true لو المستخدم علم الـ Checkbox، وfalse لو شال العلامة.
                                                    selectedServices + service
                                                } else {
                                                    selectedServices - service
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(service)
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Clinic Logo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .border(2.dp,
                                    if (clinicImageUri == null) Color.Red else Color.Gray,
                                    RoundedCornerShape(8.dp))
                                .clickable { clinicImageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (clinicBitmap != null) {
                                Image(
                                    bitmap = clinicBitmap!!.asImageBitmap(),
                                    contentDescription = "Clinic Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("Add Logo Image", color = Color.Gray)
                                }
                            }
                        }


                        Text(
                            text = "License Image",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .border(2.dp,
                                    if (licenseImageUri == null) Color.Red else Color.Gray,
                                    RoundedCornerShape(8.dp))
                                .clickable { licenseImageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (licenseBitmap != null) {
                                Image(
                                    bitmap = licenseBitmap!!.asImageBitmap(),
                                    contentDescription = "License Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("Add License Image", color = Color.Gray)
                                }
                            }
                        }
                    }
                } else {
                    if (isLogin) {

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )


                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    } else {

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )


                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )


                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = userPhone,
                            onValueChange = { userPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )


                        OutlinedTextField(
                            value = numberOfPets,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    numberOfPets = it
                                }
                            },
                            label = { Text("Number of Pets") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Enter number of pets") }
                        )


                        if (pets.isNotEmpty()) {
                            Text(
                                text = "Pet Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryDark,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            pets.forEachIndexed { index, pet ->
                                UserPetCard(
                                    pet = pet,
                                    index = index,
                                    onPetTypeChanged = { newType ->
                                        val updatedPets = pets.toMutableList()
                                        updatedPets[index] = Petss(
                                            petType = newType,
                                            imageUri = updatedPets[index].imageUri,
                                            bitmap = updatedPets[index].bitmap
                                        )
                                        pets = updatedPets
                                    },
                                    onImageSelected = { uri ->
                                        val updatedPets = pets.toMutableList()
                                        updatedPets[index] = Petss(
                                            petType = updatedPets[index].petType,
                                            imageUri = uri,
                                            bitmap = updatedPets[index].bitmap
                                        )
                                        pets = updatedPets
                                    },
                                    onBitmapLoaded = { bitmap ->
                                        val updatedPets = pets.toMutableList()
                                        updatedPets[index] = Petss(
                                            petType = updatedPets[index].petType,
                                            imageUri = updatedPets[index].imageUri,
                                            bitmap = bitmap
                                        )
                                        pets = updatedPets
                                    },
                                    loadBitmap = { uri -> loadBitmap(uri) },
                                    primaryDark = primaryDark
                                )
                            }
                        }
                    }
                }
                // أضف هذه الدالة في LoginSignupScreen
                fun saveUserName(name: String) {
                    try {
                        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        prefs.edit().apply {
                            putString("user_name", name)
                            apply()
                        }
                        Log.d("SAVE_USER", "✅ User name saved to SharedPreferences: $name")
                    } catch (e: Exception) {
                        Log.e("SAVE_USER", "❌ Failed to save user name: ${e.message}")
                    }
                }
                fun saveUserEmail(email: String) {
                    try {
                        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putString("user_email", email)
                        editor.apply()  // استخدم apply() بدلاً من commit() لأنها أسرع
                        Log.d("SAVE_EMAIL", "✅ Email saved to SharedPreferences: $email")

                        // تحقق من الحفظ
                        val savedEmail = prefs.getString("user_email", "NOT_SAVED")
                        Log.d("SAVE_EMAIL", "📝 Verified saved email: $savedEmail")
                    } catch (e: Exception) {
                        Log.e("SAVE_EMAIL", "❌ Failed to save email: ${e.message}")
                    }
                }


                // دالة لقراءة الـ email من SharedPreferences
                fun getUserEmail(): String {
                    val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    return prefs.getString("user_email", "") ?: ""
                }


                Spacer(modifier = Modifier.height(16.dp))
                val auth = FirebaseAuth.getInstance()

                // ========== SUBMIT BUTTON - UPDATED ==========
                Button(
                    onClick = {
                        Log.d("SignupButton", "Button clicked - isLogin: $isLogin, accountType: $accountType")

                        if (isLogin) {
                            // LOGIN LOGIC
                            if (accountType == "clinic") {
                                if(email.isNotEmpty() && password.isNotEmpty()) {
                                    clinicsViewModel.loginClinic(email, password) { success, error ->
                                        if(success) {
                                            navController?.navigate("clinic_home/${email}") {
                                                popUpTo("choose_account") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, error ?: "Login failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                userViewModel.loginUser(email, password) { success, error ->
                                    if(success) {
                                        Log.d("AUTH", "Login successful — currentUser = ${auth.currentUser}")
                                        Log.d("AUTH", "currentUser email = ${auth.currentUser?.email}")
                                        Log.d("AUTH", "currentUser uid = ${auth.currentUser?.uid}")

                                        // حفظ الـ email في SharedPreferences
                                        saveUserEmail(email)

                                        // حفظ الاسم أيضاً إذا كان متوفراً
                                        val currentUser = FirebaseAuth.getInstance().currentUser
                                        val userName = currentUser?.displayName ?: email.split("@").firstOrNull() ?: "User"
                                        saveUserName(userName)

                                        // الانتقال إلى شاشة الـ Profile مباشرة
                                        navController?.navigate("clinics") {
                                            popUpTo("choose_account") { inclusive = true }
                                        }

                                        Toast.makeText(
                                            context,
                                            "✅ Welcome back, $userName!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Toast.makeText(context, error ?: "Login failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        else {
                            // SIGNUP LOGIC
                            if (password == confirmPassword) {
                                if (accountType == "clinic") {
                                    // ========== CLINIC SIGNUP - CORRECTED ==========
                                    Log.d("ClinicSignup", "Validating clinic data...")

                                    if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                                        clinicName.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty() &&
                                        phone.isNotEmpty() && workingHours.isNotEmpty() && Rating.isNotEmpty() &&
                                        selectedServices.isNotEmpty() && clinicImageUri != null && licenseImageUri != null) {

                                        try {
                                            val logoBase64 = bitmapToBase64(clinicBitmap)
                                            val licenseBase64 = bitmapToBase64(licenseBitmap)

                                            if (logoBase64.isNotEmpty() && licenseBase64.isNotEmpty()) {
                                                Log.d("ClinicSignup", "Base64 strings created successfully")

                                                clinicsViewModel.signUpClinicWithBase64(
                                                    id = UUID.randomUUID().toString(),
                                                    rating = Rating,
                                                    password = password,
                                                    clinicName = clinicName,
                                                    email = email,
                                                    phone = phone,
                                                    address = address,
                                                    city = city,
                                                    workingHours = workingHours,
                                                    selectedServices = selectedServices.toList(),
                                                    logoBase64 = logoBase64,
                                                    licenseBase64 = licenseBase64,
                                                    onResult = { success, error ->
                                                        Log.d("ClinicSignup", "ViewModel callback: Success=$success, Error=$error")
                                                        if (success) {
                                                            navController?.navigate("clinic_home/$email") {
                                                                popUpTo("choose_account") { inclusive = true }
                                                            }
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                error ?: "Registration failed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                )
                                            } else {
                                                Toast.makeText(context, "Failed to process images", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("ClinicSignup", "Error: ${e.message}", e)
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_LONG).show()
                                        Log.d("ClinicSignup", "Validation failed - Missing fields")
                                    }
                                } else {
                                    // USER SIGNUP
                                    val petsValid = pets.isEmpty() || pets.all { it.petType.isNotEmpty() }

                                    // تحقق من جميع الحقول المطلوبة
                                    val fieldsValid = listOf(
                                        email.isNotEmpty() to "Email",
                                        password.isNotEmpty() to "Password",
                                        confirmPassword.isNotEmpty() to "Confirm Password",
                                        userName.isNotEmpty() to "Name",
                                        userPhone.isNotEmpty() to "Phone"
                                    )

                                    val missingFields = fieldsValid.filter { !it.first }.map { it.second }

                                    if (missingFields.isEmpty() && (numberOfPets.isEmpty() || (numberOfPets.toIntOrNull() ?: 0) > 0) && petsValid) {

                                        val petsWithBase64 = pets.map { pet ->
                                            val petImageBase64 = if (pet.bitmap != null) {
                                                bitmapToBase64(pet.bitmap)
                                            } else {
                                                ""
                                            }
                                            Petss(
                                                petType = pet.petType,
                                                imageUri = null,
                                                bitmap = null,
                                                imageBase64 = petImageBase64
                                            )
                                        }

                                        Log.d("UserSignup", "🎯 Starting user registration...")
                                        Log.d("UserSignup", "👤 Name: $userName, Email: $email")
                                        Log.d("UserSignup", "📞 Phone: $userPhone, Pets: ${petsWithBase64.size}")

                                        userViewModel.signUpUserWithBase64(
                                            password = password,
                                            userName = userName,
                                            email = email,
                                            phone = userPhone,
                                            pets = petsWithBase64
                                        ) { success, error ->
                                            if (success) {
                                                Log.d("UserSignup", "✅ Registration successful!")

                                                // حفظ البيانات في SharedPreferences
                                                saveUserEmail(email)
                                                saveUserName(userName)

                                                Toast.makeText(
                                                    context,
                                                    "✅ Welcome $userName! Registration successful!",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                // الانتقال إلى شاشة الـ Profile مباشرة
                                                navController?.navigate("clinics") {
                                                    popUpTo("choose_account") { inclusive = true }
                                                }

                                            } else {
                                                Log.e("UserSignup", "❌ Registration failed: $error")
                                                Toast.makeText(
                                                    context,
                                                    "❌ Failed to register: ${error ?: "Unknown error"}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } else {
                                        if (missingFields.isNotEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "❌ Missing fields: ${missingFields.joinToString(", ")}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else if (!petsValid) {
                                            Toast.makeText(
                                                context,
                                                "❌ Please enter pet types for all pets",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "❌ Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = primary),
                    enabled = if (isLogin) {
                        email.isNotEmpty() && password.isNotEmpty()
                    } else {
                        if (accountType == "clinic") {
                            // ========== FIXED VALIDATION ==========
                            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                                    clinicName.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty() &&
                                    phone.isNotEmpty() && workingHours.isNotEmpty() && Rating.isNotEmpty() &&
                                    selectedServices.isNotEmpty() && clinicImageUri != null && licenseImageUri != null
                        } else {

                            val petsValid = pets.isEmpty() || pets.all { it.petType.isNotEmpty() }
                            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                                    userName.isNotEmpty() && userPhone.isNotEmpty() &&
                                    (numberOfPets.isEmpty() || ((numberOfPets.toIntOrNull() ?: 0) > 0 && petsValid))
                        }
                    }
                ) {
                    Text(
                        text = if (isLogin) "Login" else "Signup",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UserPetCard(
    pet: Petss,
    index: Int,
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


            OutlinedTextField(
                value = pet.petType,
                onValueChange = { newType ->
                    onPetTypeChanged(newType)
                },
                label = { Text("Pet Type") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Cat, Dog...") }
            )


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
fun LoginSignupScreenPreview() {
    LoginSignupScreen(accountType = "clinic", navController = null)
}