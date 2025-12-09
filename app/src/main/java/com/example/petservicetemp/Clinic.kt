package com.example.petservicetemp

data class Clinic(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val workingHours: String = "9:00 AM - 8:00 PM",
    val logoBase64: String = "",
    val licenseBase64: String = "",
    val password: String = "",
    val services: List<String> = emptyList(),
    val rating: Double = 0.0,
    val isOpen: Boolean = false, // ✅ واحد فقط
    val reviews: Int = 0
) {
    // constructor فاضي علشان Firebase
    constructor() : this(
        id = "",
        name = "",
        email = "",
        phoneNumber = "",
        location = "",
        workingHours = "9:00 AM - 8:00 PM",
        logoBase64 = "",
        licenseBase64 = "",
        password = "",
        services = emptyList(),
        rating = 0.0,
        isOpen = false,
        reviews = 0
    )
}