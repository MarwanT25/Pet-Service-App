package com.example.petservicetemp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClinicsViewModel : ViewModel() {

    val repository = ClinicRepository()

    private val _clinics = MutableStateFlow<List<Clinic>>(emptyList())
    val clinics: StateFlow<List<Clinic>> = _clinics

    init {
        getRealTimeUpdates()
    }

    private fun getRealTimeUpdates() {
        viewModelScope.launch {
            repository.getClinicsStream()
                .collect { updatedList ->
                    _clinics.value = updatedList
                }
        }
    }

    // الدالة الأصلية علشان الـ Storage (زي ما هي)
    fun signUpClinic(
        password: String,
        clinicName: String,
        email: String,
        phone: String,
        address: String,
        city: String,
        workingHours: String,
        selectedServices: List<String>,
        clinicImageUri: Uri,
        licenseImageUri: Uri,
        onResult: (Boolean, String?) -> Unit,
    ) {
        repository.uploadImage(clinicImageUri, { logoUrl ->
            repository.uploadImage(licenseImageUri, { licenseUrl ->
                val clinic = Clinic(
                    name = clinicName,
                    email = email,
                    phoneNumber = phone,
                    location = "$address, $city",
                    workingHours = workingHours,
                    services = selectedServices,
                    logoBase64 = logoUrl,
                    licenseBase64 = licenseUrl,
                    password = password
                )
                repository.addClinic(clinic) { success ->
                    if (success) onResult(true, null)
                    else onResult(false, "Failed to save clinic")
                }
            }, { e -> onResult(false, e.message) })
        }, { e -> onResult(false, e.message) })
    }

    // الدالة الجديدة علشان الـ Base64 (ضيفناها بس)
    fun signUpClinicWithBase64(
        password: String,
        clinicName: String,
        email: String,
        phone: String,
        address: String,
        city: String,
        workingHours: String,
        selectedServices: List<String>,
        logoBase64: String,
        licenseBase64: String,
        onResult: (Boolean, String?) -> Unit,
    ) {
        val clinic = Clinic(
            name = clinicName,
            email = email,
            phoneNumber = phone,
            location = "$address, $city",
            workingHours = workingHours,
            services = selectedServices,
            logoBase64 = logoBase64,    // هنا بنستخدم Base64
            licenseBase64 = licenseBase64, // هنا بنستخدم Base64
            password = password
        )

        repository.addClinic(clinic) { success ->
            if (success) onResult(true, null)
            else onResult(false, "Failed to save clinic")
        }
    }

    // الدالة دي زي ما هي
    fun loginClinic(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        repository.loginClinic(email, password) { success, clinic ->
            if (success && clinic != null) {
                onResult(true, null)
            } else {
                onResult(false, "Clinic not found or wrong credentials")
            }
        }
    }
}