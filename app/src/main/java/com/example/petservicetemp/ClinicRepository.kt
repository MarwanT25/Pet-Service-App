package com.example.petservicetemp

import android.app.DownloadManager
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class ClinicRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val clinicsCollection = db.collection("clinics")

    // 1. Upload Image Function (رجعناها علشان الـ ViewModel)
    fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        println("📤 بدء رفع الصورة للـ Storage...")
        val filename = UUID.randomUUID().toString()
        val ref = storage.reference.child("clinic_images/$filename")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                println("✅ الصورة اتحملت للـ Storage")
                ref.downloadUrl.addOnSuccessListener { uri ->
                    println("🔗 جاري جلب الـ URL: ${uri.toString().take(50)}...")
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                println("❌ فشل رفع الصورة: ${e.message}")
                onFailure(e)
            }
    }

    // 2. Add Clinic Function (بتشتغل مع الـ Base64 والـ URL)
    fun addClinic(clinic: Clinic, onComplete: (Boolean) -> Unit) {
        println("🚀 بدء حفظ العيادة في Firestore...")
        println("📝 البيانات المرسلة:")
        println("   - الاسم: ${clinic.name}")
        println("   - الإيميل: ${clinic.email}")
        println("   - التليفون: ${clinic.phoneNumber}")
        println("   - العنوان: ${clinic.location}")
        println("   - ساعات العمل: ${clinic.workingHours}")
        println("   - عدد الخدمات: ${clinic.services.size}")
        println("   - الباسورد: ${if (clinic.password.isNotEmpty()) "***" else "فارغ"}")
        println("   - حجم اللوجو: ${clinic.logoBase64.length} حرف")
        println("   - حجم الترخيص: ${clinic.licenseBase64.length} حرف")

        clinicsCollection.add(clinic)
            .addOnSuccessListener { documentReference ->
                println("✅ تم حفظ العيادة بنجاح! الرقم: ${documentReference.id}")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                println("❌ فشل حفظ العيادة: ${e.message}")
                e.printStackTrace()
                onComplete(false)
            }
    }

    // 3. دالة جديدة واضحة علشان الـ Base64
    fun addClinicWithBase64(
        clinicName: String,
        email: String,
        phone: String,
        address: String,
        city: String,
        workingHours: String,
        services: List<String>,
        logoBase64: String,
        licenseBase64: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) {
        println("🎯 بدء عملية إضافة العيادة بالـ Base64...")
        println("📊 بيانات الإدخال:")
        println("   - الاسم: $clinicName")
        println("   - الإيميل: $email")
        println("   - التليفون: $phone")
        println("   - العنوان: $address, $city")
        println("   - ساعات العمل: $workingHours")
        println("   - الخدمات: $services")
        println("   - حجم اللوجو: ${logoBase64.length} حرف")
        println("   - حجم الترخيص: ${licenseBase64.length} حرف")
        println("   - الباسورد: ${if (password.isNotEmpty()) "***" else "فارغ"}")

        if (logoBase64.isEmpty() || licenseBase64.isEmpty()) {
            println("❌ خطأ: الـ Base64 فاضي!")
            onComplete(false)
            return
        }

        val clinic = Clinic(
            name = clinicName,
            email = email,
            phoneNumber = phone,
            location = "$address, $city",
            workingHours = workingHours,
            services = services,
            logoBase64 = logoBase64,
            licenseBase64 = licenseBase64,
            password = password
        )

        addClinic(clinic, onComplete)
    }

    // 4. Listen for Data (For the List Screen)
    fun getClinicsStream(): Flow<List<Clinic>> = callbackFlow {
        val subscription = clinicsCollection
            .orderBy("rating", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Clinic::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun loginClinic(email: String, password: String, onResult: (Boolean, Clinic?) -> Unit) {
        println("🔐 محاولة تسجيل الدخول للإيميل: $email")

        clinicsCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                println("📄 عدد النتائج: ${snapshot.documents.size}")
                if (!snapshot.isEmpty) {
                    val clinic = snapshot.documents[0].toObject(Clinic::class.java)
                    if (clinic != null && clinic.password == password) {
                        println("✅ تسجيل الدخول ناجح!")
                        onResult(true, clinic)
                    } else {
                        println("❌ كلمة المرور خاطئة أو البيانات null")
                        onResult(false, null)
                    }
                } else {
                    println("❌ لا توجد عيادة بهذا الإيميل")
                    onResult(false, null)
                }
            }
            .addOnFailureListener { e ->
                println("❌ فشل في الاستعلام: ${e.message}")
                onResult(false, null)
            }
    }
}