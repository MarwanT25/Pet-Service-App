package com.example.petservicetemp.ui

import android.os.Bundle

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petservicetemp.ui.theme.PetServiceTempTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLDecoder
import java.util.*
import android.os.Handler
import android.os.Looper
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // اختبر اتصال Firebase
       // testFirebaseConnection()

        setContent {
            PetServiceTempTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F8F8)
                ) {
                    Navigation()
                }
            }
        }
    }
   private fun testFirebaseConnection() {
        try {
            Log.d("FIREBASE_TEST", "🚀 بدء اختبار اتصال Firebase...")

            // الطريقة 1: استخدمي getInstance مباشرة
            try {
                val db = FirebaseFirestore.getInstance()
                Log.d("FIREBASE_TEST", "✅ نجاح - Firestore instance created")

                // اختبر عملية بسيطة
                db.collection("test").document("quick_test")
                    .set(hashMapOf("timestamp" to System.currentTimeMillis()))
                    .addOnSuccessListener {
                        Log.d("FIREBASE_TEST", "🎉 نجاح كتابة البيانات!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FIREBASE_TEST", "❌ فشل الكتابة: ${e.message}")
                    }

            } catch (e: Exception) {
                Log.e("FIREBASE_TEST", "❌ فشل الطريقة 1: ${e.message}")

                // الطريقة 2: استخدمي initializeApp يدوياً
                try {
                    Log.d("FIREBASE_TEST", "🔧 جرب الطريقة 2: التهيئة اليدوية...")
                    FirebaseApp.initializeApp(this)

                    // انتظري قليلاً ثم جربي مرة أخرى
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            val db2 = FirebaseFirestore.getInstance()
                            Log.d("FIREBASE_TEST", "✅ نجاح الطريقة 2 - Firestore instance created")
                        } catch (e2: Exception) {
                            Log.e("FIREBASE_TEST", "❌ فشل الطريقة 2: ${e2.message}")
                        }
                    }, 1000)

                } catch (e2: Exception) {
                    Log.e("FIREBASE_TEST", "💥 فشل كامل: ${e2.message}")
                }
            }

        } catch (e: Exception) {
            Log.e("FIREBASE_TEST", "💥 خطأ عام: ${e.message}")
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "choose_account"
    ) {
        composable("choose_account") {
            ChooseAccountTypeScreen(navController = navController)
        }
        composable(
            route = "login_signup/{accountType}",
            arguments = listOf(
                navArgument("accountType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val accountType = backStackEntry.arguments?.getString("accountType") ?: "user"
            LoginSignupScreen(accountType = accountType, navController = navController)
        }
        composable("signup_clinic") {
            SignupClinicScreen(navController = navController)
        }
        composable("signup_user") {
            SignupUserScreen(navController = navController)
        }
        composable("clinics") {
            ClinicScreen(navController = navController)
        }
        composable(
            route = "booking/{clinicName}/{rating}/{isOpen}/{location}/{reviews}/{phoneNumber}",
            arguments = listOf(
                navArgument("clinicName") { type = NavType.StringType },
                navArgument("rating") { type = NavType.FloatType },
                navArgument("isOpen") { type = NavType.BoolType },
                navArgument("location") { type = NavType.StringType },
                navArgument("reviews") { type = NavType.IntType },
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clinicName = URLDecoder.decode(backStackEntry.arguments?.getString("clinicName") ?: "", "UTF-8")
            val rating = backStackEntry.arguments?.getFloat("rating") ?: 0f
            val isOpen = backStackEntry.arguments?.getBoolean("isOpen") ?: false
            val location = URLDecoder.decode(backStackEntry.arguments?.getString("location") ?: "", "UTF-8")
            val reviews = backStackEntry.arguments?.getInt("reviews") ?: 0
            val phoneNumber = URLDecoder.decode(backStackEntry.arguments?.getString("phoneNumber") ?: "", "UTF-8")

            BookingScreenStyled(
                clinicName = clinicName,
                rating = rating.toDouble(),
                isOpen = isOpen,
                location = location,
                reviews = reviews,
                phoneNumber = phoneNumber,
                navController = navController
            )
        }
        composable(
            route = "clinic_home/{clinicName}",
            arguments = listOf(
                navArgument("clinicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clinicName = URLDecoder.decode(backStackEntry.arguments?.getString("clinicName") ?: "Clinic", "UTF-8")
            ClinicHomeScreen(clinicName = clinicName, navController = navController)
        }
        composable(
            route = "clinic_profile/{clinicName}",
            arguments = listOf(
                navArgument("clinicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clinicName = URLDecoder.decode(backStackEntry.arguments?.getString("clinicName") ?: "Clinic", "UTF-8")
            ClinicProfileScreen(clinicName = clinicName, navController = navController)
        }
        composable(
            route = "clinic_details/{clinicName}/{rating}/{isOpen}/{location}/{reviews}/{phoneNumber}",
            arguments = listOf(
                navArgument("clinicName") { type = NavType.StringType },
                navArgument("rating") { type = NavType.FloatType },
                navArgument("isOpen") { type = NavType.BoolType },
                navArgument("location") { type = NavType.StringType },
                navArgument("reviews") { type = NavType.IntType },
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clinicName = URLDecoder.decode(backStackEntry.arguments?.getString("clinicName") ?: "", "UTF-8")
            val rating = backStackEntry.arguments?.getFloat("rating") ?: 0f
            val isOpen = backStackEntry.arguments?.getBoolean("isOpen") ?: false
            val location = URLDecoder.decode(backStackEntry.arguments?.getString("location") ?: "", "UTF-8")
            val reviews = backStackEntry.arguments?.getInt("reviews") ?: 0
            val phoneNumber = URLDecoder.decode(backStackEntry.arguments?.getString("phoneNumber") ?: "", "UTF-8")

            val clinic = Clinic(
                id = UUID.randomUUID().toString(),
                name = clinicName,
                email = "",
                phoneNumber = phoneNumber,
                location = location,
                workingHours = "",
                logoBase64 = "",
                licenseBase64 = "",
                password = "",
                services = emptyList(),
                rating = rating.toDouble(),
                isOpen = isOpen,
                reviews = reviews
            )
            ClinicDetailsScreen(clinic = clinic, navController = navController)
        }
        composable("user_profile") {
            UserProfileScreen(navController = navController)
        }
        composable("user_home") {
            UserHomeScreen(navController = navController)
        }