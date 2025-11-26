package com.example.petservicetemp

data class Clinic(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val workingHours: String = "",
    val logoBase64: String = "",       // بدل logoUrl
    val licenseBase64: String = "",    // بدل licenseUrl
    val password: String = "",
    val services: List<String> = emptyList(),
    val rating: Double = 0.0,
    val isOpen: Boolean = false,
    val reviews: Int = 0
)