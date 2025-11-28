package com.example.petservicetemp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser
    fun signUpUserWithBase64(
        password: String,
        userName: String,
        email: String,
        phone: String,
        pets: List<Petss>,
        onResult: (Boolean, String?) -> Unit
    ) {
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
                onResult(true, null)
            } else {
                onResult(false, error)
            }
        }
    }
    // تسجيل مستخدم جديد
    fun signUpUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        pets: List<Petss>,  // غيرت إلى Petss
        onResult: (Boolean, String?) -> Unit
    ) {
        println("🎯 [UserViewModel] بدء تسجيل مستخدم جديد...")
        println("👤 الاسم: $name, الإيميل: $email, عدد الحيوانات: ${pets.size}")

        val user = User(
            name = name,
            email = email,
            phone = phone,
            password = password,
            pets = pets,  // غيرت إلى Petss
            favoriteClinics = emptyList()
        )

        repository.addUser(user) { success, error ->
            if (success) {
                println("✅ [UserViewModel] تسجيل المستخدم تم بنجاح!")
                _currentUser.value = user
                onResult(true, null)
            } else {
                println("❌ [UserViewModel] فشل تسجيل المستخدم: $error")
                onResult(false, error)
            }
        }
    }

    // تسجيل الدخول
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        println("🔐 [UserViewModel] محاولة تسجيل دخول: $email")

        repository.loginUser(email, password) { success, user ->
            if (success && user != null) {
                println("✅ [UserViewModel] تسجيل الدخول ناجح!")
                _currentUser.value = user
                onResult(true, null)
            } else {
                println("❌ [UserViewModel] تسجيل الدخول فاشل!")
                onResult(false, "البريد الإلكتروني أو كلمة المرور غير صحيحة")
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
    fun addPet(pet: Petss) {  // غيرت إلى Petss
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
    }
}