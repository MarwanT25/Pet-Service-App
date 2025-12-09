package com.example.petservicetemp

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScheduleViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUserBookings()
    }

    private fun fetchUserBookings() {
        val userEmail = auth.currentUser?.email

        if (userEmail != null) {
            db.collection("bookings")
                .whereEqualTo("userId", userEmail)
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
        } else {
            _isLoading.value = false
        }
    }
}