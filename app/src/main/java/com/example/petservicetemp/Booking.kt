package com.example.petservicetemp

import com.google.firebase.firestore.DocumentId

data class Booking(
    @DocumentId val firestoreId: String = "",

    val clinicName: String = "",
    val date: String = "",
    val service: String = "",
    val status: String = "Pending",
    val time: String = "",
    val userId: String = ""
)