package com.example.petservicetemp

data class Clinic(
    val id: String = "",
    val name: String = "",
    val rating: Double = 0.0,
    val isOpen: Boolean = false,
    val location: String = "",
    val phoneNumber: String = "",
    val reviews: Int = 0,
    val logoUrl: String = "",
    val licenseUrl: String = ""
)
