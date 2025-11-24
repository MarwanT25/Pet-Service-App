package com.example.petservicetemp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClinicsViewModel : ViewModel() {

    // 1. Get an instance of the Repository
    // (This assumes you created the ClinicRepository.kt file from our earlier steps)
    private val repository = ClinicRepository()

    // 2. Create a "State" to hold the list of clinics
    // It starts as an empty list
    private val _clinics = MutableStateFlow<List<Clinic>>(emptyList())

    // 3. Expose the state as "Read-Only" for the UI to observe
    val clinics: StateFlow<List<Clinic>> = _clinics

    init {
        // 4. Automatically start listening to the database when the app opens this screen
        getRealTimeUpdates()
    }

    private fun getRealTimeUpdates() {
        viewModelScope.launch {
            // This 'collect' block runs every time the Firestore database changes
            repository.getClinicsStream().collect { updatedList ->
                _clinics.value = updatedList
            }
        }
    }
}