package com.example.petservicetemp

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ClinicBookingsViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchBookingsForClinic(clinicName: String) {
        _isLoading.value = true

        db.collection("bookings")
            .whereEqualTo("clinicName", clinicName)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects(Booking::class.java)
                    _bookings.value = list
                    _isLoading.value = false
                }
            }
    }
    fun updateBookingStatus(bookingId: String, newStatus: String) {
        if (bookingId.isNotEmpty()) {
            db.collection("bookings").document(bookingId)
                .update("status", newStatus)
                .addOnFailureListener { e ->
                    android.util.Log.e("ViewModel", "Error updating status", e)
                }
        }
    }
}
