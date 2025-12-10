package com.example.petservicetemp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userPets = MutableStateFlow<List<String>>(emptyList())
    val userPets: StateFlow<List<String>> = _userPets.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlows جديدة
    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    init {
        // تحميل بيانات المستخدم عند بدء ViewModel
        loadCurrentUserData()
    }

    // تحميل بيانات المستخدم الحالي
    private fun loadCurrentUserData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val userId = firebaseUser.uid
                    val email = firebaseUser.email ?: ""

                    // تحديث الـ StateFlows الأساسية
                    _userEmail.value = email
                    _currentUserEmail.value = email

                    // محاولة الحصول من Firestore
                    val document = db.collection("users").document(userId).get().await()

                    if (document.exists()) {
                        // بيانات من Firestore
                        val name = document.getString("name") ?:
                        document.getString("userName") ?:
                        firebaseUser.displayName ?:
                        email.split("@").firstOrNull() ?: "User"

                        val phone = document.getString("phone") ?:
                        document.getString("userPhone") ?:
                        "Not provided"

                        // تحديث جميع الـ StateFlows
                        _userName.value = name
                        _currentUserName.value = name
                        _userPhone.value = phone

                        // الحصول على الحيوانات الأليفة
                        val pets = document.get("pets") as? List<Map<String, Any>> ?: emptyList()
                        _userPets.value = pets.mapNotNull {
                            it["petType"] as? String
                        }

                        // تحديث الـ currentUser
                        val user = User(
                            id = userId,
                            name = name,
                            email = email,
                            phone = phone,
                            password = "", // لا نحتاج كلمة المرور هنا
                            pets = pets.map {
                                Petss(
                                    petType = it["petType"] as? String ?: "",
                                    imageBase64 = it["imageBase64"] as? String ?: ""
                                )
                            },
                            favoriteClinics = document.get("favoriteClinics") as? List<String> ?: emptyList()
                        )
                        _currentUser.value = user

                        Log.d("USER_VM", "✅ Loaded user data from Firestore")
                        Log.d("USER_VM", "Name: $name, Email: $email")
                        Log.d("USER_VM", "Pets: ${_userPets.value}")

                    } else {
                        // إذا لم توجد في Firestore، استخدم بيانات Firebase Auth
                        val name = firebaseUser.displayName ?:
                        email.split("@").firstOrNull() ?: "User"

                        _userName.value = name
                        _currentUserName.value = name
                        _userPhone.value = "Not provided"
                        _userPets.value = emptyList()

                        Log.d("USER_VM", "⚠️ User not in Firestore, using Firebase Auth data")
                    }
                } else {
                    Log.w("USER_VM", "No user logged in")
                }
            } catch (e: Exception) {
                Log.e("USER_VM", "❌ Error loading user data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // إعادة تحميل بيانات المستخدم
    fun refreshUserData() {
        loadCurrentUserData()
    }

    // تسجيل مستخدم جديد مع Base64
    fun signUpUserWithBase64(
        password: String,
        userName: String,
        email: String,
        phone: String,
        pets: List<Petss>,
        onResult: (Boolean, String?) -> Unit
    ) {
        Log.d("USER_VM", "🎯 Starting signUpUserWithBase64")
        Log.d("USER_VM", "👤 Name: $userName, Email: $email, Pets: ${pets.size}")

        val user = User(
            name = userName,
            email = email,
            phone = phone,
            password = password,
            pets = pets,
            favoriteClinics = emptyList()
        )

        repository.addUser(user) { success, error ->
            if (success) {
                // بعد التسجيل الناجح، تحديث بيانات المستخدم
                _userName.value = userName
                _userEmail.value = email
                _userPhone.value = phone
                _userPets.value = pets.map { it.petType }
                _currentUserEmail.value = email
                _currentUserName.value = userName

                Log.d("USER_VM", "✅ User signed up and data updated in ViewModel")
                onResult(true, null)
            } else {
                Log.e("USER_VM", "❌ Failed to sign up user: $error")
                onResult(false, error)
            }
        }
    }

    // تسجيل مستخدم جديد (الطريقة القديمة)
    fun signUpUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        pets: List<Petss>,
        onResult: (Boolean, String?) -> Unit
    ) {
        Log.d("USER_VM", "🎯 Starting signUpUser")
        Log.d("USER_VM", "👤 Name: $name, Email: $email, Pets: ${pets.size}")

        val user = User(
            name = name,
            email = email,
            phone = phone,
            password = password,
            pets = pets,
            favoriteClinics = emptyList()
        )

        repository.addUser(user) { success, error ->
            if (success) {
                // تحديث بيانات المستخدم في ViewModel
                _userName.value = name
                _userEmail.value = email
                _userPhone.value = phone
                _userPets.value = pets.map { it.petType }
                _currentUserEmail.value = email
                _currentUserName.value = name
                _currentUser.value = user

                Log.d("USER_VM", "✅ User signed up successfully!")
                onResult(true, null)
            } else {
                Log.e("USER_VM", "❌ Failed to sign up user: $error")
                onResult(false, error)
            }
        }
    }

    // تسجيل الدخول (معدل)
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("USER_VM", "🔐 Attempting login for: $email")

        repository.loginUser(email, password) { success, user ->
            if (success && user != null) {
                // تحديث جميع بيانات المستخدم في ViewModel
                _currentUser.value = user
                _userName.value = user.name
                _userEmail.value = user.email
                _userPhone.value = user.phone
                _userPets.value = user.pets.map { it.petType }
                _currentUserEmail.value = user.email
                _currentUserName.value = user.name

                Log.d("USER_VM", "✅ Login successful!")
                Log.d("USER_VM", "👤 User loaded: ${user.name} (${user.email})")

                onResult(true, null)
            } else {
                Log.e("USER_VM", "❌ Login failed!")
                onResult(false, "البريد الإلكتروني أو كلمة المرور غير صحيحة")
            }
        }
    }

    // تحديث ملف المستخدم
    fun updateUserProfile(name: String, phone: String, pets: List<String>) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userId = currentUser.uid

                    // تحويل pets إلى List<Map>
                    val petsList = pets.map { petType ->
                        hashMapOf("petType" to petType)
                    }

                    // تحديث البيانات في Firestore
                    val userData = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "pets" to petsList,
                        "updatedAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(userId)
                        .update(userData as Map<String, Any>)
                        .await()

                    // تحديث الـ StateFlows
                    _userName.value = name
                    _currentUserName.value = name
                    _userPhone.value = phone
                    _userPets.value = pets

                    // تحديث الـ currentUser
                    val updatedUser = _currentUser.value?.copy(
                        name = name,
                        phone = phone,
                        pets = pets.map { Petss(petType = it) }
                    )
                    _currentUser.value = updatedUser

                    Log.d("USER_VM", "✅ Profile updated successfully")
                }
            } catch (e: Exception) {
                Log.e("USER_VM", "❌ Failed to update profile: ${e.message}")
            }
        }
    }

    // تسجيل الخروج
    fun logout() {
        auth.signOut()
        // إعادة تعيين جميع البيانات
        _currentUser.value = null
        _userName.value = ""
        _userEmail.value = ""
        _userPhone.value = ""
        _userPets.value = emptyList()
        _currentUserEmail.value = ""
        _currentUserName.value = ""

        Log.d("USER_VM", "✅ User logged out, all data cleared")
    }

    // الحصول على بيانات المستخدم المحفوظة من SharedPreferences
    fun getSavedUserData(context: Context): Pair<String, String>? {
        return try {
            val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val email = prefs.getString("user_email", "") ?: ""
            val name = prefs.getString("user_name", "") ?: ""

            if (email.isNotEmpty() && name.isNotEmpty()) {
                Pair(email, name)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("USER_VM", "Error reading SharedPreferences: ${e.message}")
            null
        }
    }

    // الحصول على بيانات المستخدم الحالي (للشاشات الأخرى)
    fun getCurrentUserData(): Triple<String, String, String> {
        return Triple(
            _userName.value,
            _userEmail.value,
            _userPhone.value
        )
    }

    // دالة مساعدة للحصول على الـ email من جميع المصادر
    fun getUserEmailForBooking(): String {
        return when {
            _userEmail.value.isNotEmpty() -> _userEmail.value
            _currentUserEmail.value.isNotEmpty() -> _currentUserEmail.value
            auth.currentUser?.email?.isNotEmpty() == true -> auth.currentUser?.email ?: ""
            else -> "guest@example.com"
        }
    }

    // دالة مساعدة للحصول على الاسم من جميع المصادر
    fun getUserNameForBooking(): String {
        return when {
            _userName.value.isNotEmpty() -> _userName.value
            _currentUserName.value.isNotEmpty() -> _currentUserName.value
            auth.currentUser?.displayName?.isNotEmpty() == true -> auth.currentUser?.displayName ?: "User"
            else -> {
                val email = getUserEmailForBooking()
                email.split("@").firstOrNull() ?: "User"
            }
        }
    }

    // إضافة عيادة للمفضلة
    fun addFavoriteClinic(clinicId: String) {
        val current = _currentUser.value
        if (current != null) {
            val updatedFavorites = current.favoriteClinics + clinicId
            val updatedUser = current.copy(favoriteClinics = updatedFavorites)
            _currentUser.value = updatedUser
        }
    }

    // إضافة حيوان أليف
    fun addPet(pet: Petss) {
        val current = _currentUser.value
        if (current != null) {
            val updatedPets = current.pets + pet
            val updatedUser = current.copy(pets = updatedPets)
            _currentUser.value = updatedUser
        }
    }

    // تحديث بيانات المستخدم الحالي
    fun updateCurrentUser(user: User) {
        _currentUser.value = user
        _userName.value = user.name
        _userEmail.value = user.email
        _userPhone.value = user.phone
        _userPets.value = user.pets.map { it.petType }
        _currentUserEmail.value = user.email
        _currentUserName.value = user.name
    }
}