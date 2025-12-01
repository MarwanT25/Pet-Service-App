// Clinic.kt - تأكدي من الـ default values
package com.example.petservicetemp

data class Clinic(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val workingHours: String = "9:00 AM - 8:00 PM", // default value
    val logoBase64: String = "", // default empty
    val licenseBase64: String = "",
    val password: String = "",
    val services: List<String> = emptyList(), // default empty list
    val rating: Double = 0.0, // default 0.0
    val isOpen: Boolean = false, // default false
    val reviews: Int = 0 // default 0
) {
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