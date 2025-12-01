package com.example.petservicetemp
import android.graphics.Bitmap
import android.net.Uri
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val pets: List<Petss> = emptyList(),
    val favoriteClinics: List<String> = emptyList() ,

)


data class Petss(
    val petType: String = "",
    val imageUri: Uri? = null,
    val bitmap: Bitmap? = null,
    val imageBase64: String = "" // إضف هذا الحقل
)