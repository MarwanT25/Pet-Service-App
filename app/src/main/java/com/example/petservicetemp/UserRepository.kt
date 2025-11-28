package com.example.petservicetemp

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // إضافة مستخدم جديد
    fun addUser(user: User, onComplete: (Boolean, String?) -> Unit) {
        println("🚀 [UserRepository] بدء حفظ المستخدم...")
        println("📝 بيانات المستخدم: ${user.name}, ${user.email}, عدد الحيوانات: ${user.pets.size}")

        usersCollection.add(user)
            .addOnSuccessListener { documentReference ->
                println("✅ [UserRepository] تم حفظ المستخدم بنجاح! الرقم: ${documentReference.id}")
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                println("❌ [UserRepository] فشل حفظ المستخدم: ${e.message}")
                onComplete(false, "Failed to save user: ${e.message}")
            }
    }

    // تسجيل الدخول
    fun loginUser(email: String, password: String, onResult: (Boolean, User?) -> Unit) {
        println("🔐 [UserRepository] محاولة تسجيل الدخول: $email")

        usersCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                println("📄 [UserRepository] عدد النتائج: ${snapshot.documents.size}")
                if (!snapshot.isEmpty) {
                    val user = snapshot.documents[0].toObject(User::class.java)
                    if (user != null && user.password == password) {
                        println("✅ [UserRepository] تسجيل الدخول ناجح!")
                        onResult(true, user)
                    } else {
                        println("❌ [UserRepository] كلمة المرور خاطئة")
                        onResult(false, null)
                    }
                } else {
                    println("❌ [UserRepository] لا يوجد مستخدم بهذا الإيميل")
                    onResult(false, null)
                }
            }
            .addOnFailureListener { e ->
                println("❌ [UserRepository] فشل في الاستعلام: ${e.message}")
                onResult(false, null)
            }
    }

    // الحصول على مستخدم بالرقم
    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(User::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // تحديث بيانات المستخدم
    fun updateUser(userId: String, user: User, onComplete: (Boolean) -> Unit) {
        usersCollection.document(userId).set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}