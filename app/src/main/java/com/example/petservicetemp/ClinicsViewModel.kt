package com.example.petservicetemp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ClinicsViewModel : ViewModel() {

    private val _clinics = MutableStateFlow<List<Clinic>>(emptyList())
    val clinics = _clinics.asStateFlow()

    private val _selectedClinic = MutableStateFlow<Clinic?>(null)
    val selectedClinic = _selectedClinic.asStateFlow()

    private val repository = ClinicRepository()

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

    // دالة علشان نجيب عيادة واحدة بالـ ID
    fun fetchClinicById(clinicId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                db.collection("clinics").document(clinicId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val clinic = document.toObject(Clinic::class.java)?.copy(id = document.id)
                            _selectedClinic.value = clinic
                            Log.d("ClinicDetails", "✅ Clinic fetched successfully: ${clinic?.name}")
                            Log.d("ClinicDetails", "📊 Clinic data - Logo: ${clinic?.logoBase64?.take(30)}...")
                            Log.d("ClinicDetails", "📊 Clinic data - Working Hours: ${clinic?.workingHours}")
                            Log.d("ClinicDetails", "📊 Clinic data - Services: ${clinic?.services}")
                        } else {
                            Log.d("ClinicDetails", "❌ Clinic not found with ID: $clinicId")
                            _selectedClinic.value = null
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ClinicDetails", "❌ Error fetching clinic: ${e.message}")
                        _selectedClinic.value = null
                    }
            } catch (e: Exception) {
                Log.e("ClinicDetails", "💥 Exception: ${e.message}")
                _selectedClinic.value = null
            }
        }
    }

    // الدالة الأصلية علشان الـ Storage
    fun signUpClinic(
        password: String,
        clinicName: String,
        email: String,
        phone: String,
        address: String,
        city: String,
        workingHours: String,
        selectedServices: List<String>,
        clinicImageUri: android.net.Uri,
        licenseImageUri: android.net.Uri,
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

    // الدالة علشان الـ Base64
    fun signUpClinicWithBase64(
        id:String,
        rating: String,
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

        val ratingValue = rating.toDoubleOrNull()?.coerceIn(0.0,5.0) ?: 4.5


        println("⭐ Rating being saved: $ratingValue")

        val clinic = Clinic(
            id= UUID.randomUUID().toString(),
            name = clinicName,
            email = email,
            phoneNumber = phone,
            location = "$address, $city",
            workingHours = workingHours,
            services = selectedServices,
            logoBase64 = logoBase64,
            licenseBase64 = licenseBase64,
            password = password,
            rating = ratingValue,
            isOpen = true,
            reviews = 0
        )

        repository.addClinic(clinic) { success ->
            if (success) onResult(true, null)
            else onResult(false, "Failed to save clinic")
        }
    }

    // دالة تسجيل الدخول
    fun loginClinic(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        repository.loginClinic(email, password) { success, clinic ->
            if (success && clinic != null) {
                onResult(true, null)
            } else {
                onResult(false, "Clinic not found or wrong credentials")
            }
        }
    }

    // دالة علشان نضيف عيادة للاختبار
    fun addTestClinic() {
        viewModelScope.launch {
            try {
                val testClinic = Clinic(
                    name = "Test Clinic",
                    email = "test@clinic.com",
                    phoneNumber = "01234567890",
                    location = "Test Location",
                    workingHours = "9:00 AM - 8:00 PM",
                    services = listOf("Checkup", "Vaccination"),
                    logoBase64 = "",
                    licenseBase64 = "",
                    password = "test123",
                    rating = 4.5,
                    isOpen = true,
                    reviews = 10
                )

                repository.addClinic(testClinic) { success ->
                    if (success) {
                        Log.d("ClinicsViewModel", "✅ Test clinic added successfully")
                    } else {
                        Log.d("ClinicsViewModel", "❌ Failed to add test clinic")
                    }
                }
            } catch (e: Exception) {
                Log.e("ClinicsViewModel", "💥 Error adding test clinic: ${e.message}")
            }
        }
    }
}