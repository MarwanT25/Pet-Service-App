package com.example.petservicetemp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petservicetemp.ui.theme.PetServiceTempTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            Log.d("FIREBASE_TEST", "🚀 Firebase...")
            // Test Firebase connection code here
        } catch (e: Exception) {
            Log.e("FIREBASE_TEST", "💥 : ${e.message}")
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // State to hold current user
    var currentUser by remember { mutableStateOf<SimpleUser?>(null) }

    // Fetch current user data when navigation starts
    LaunchedEffect(Unit) {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            // Try to get user data from Firestore
            firestore.collection("users")
                .whereEqualTo("email", firebaseUser.email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userDoc = querySnapshot.documents.first()
                        currentUser = SimpleUser(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email,
                            displayName = userDoc.getString("name") ?:
                            firebaseUser.displayName ?:
                            firebaseUser.email?.split("@")?.firstOrNull()
                        )
                        Log.d("NAVIGATION", "User loaded from Firestore: ${currentUser?.email}")
                    } else {
                        // Fallback to FirebaseAuth data
                        currentUser = SimpleUser(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email,
                            displayName = firebaseUser.displayName ?:
                            firebaseUser.email?.split("@")?.firstOrNull()
                        )
                        Log.d("NAVIGATION", "User loaded from Auth: ${currentUser?.email}")
                    }
                }
                .addOnFailureListener { e ->
                    // Fallback to FirebaseAuth data
                    currentUser = SimpleUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        displayName = firebaseUser.displayName ?:
                        firebaseUser.email?.split("@")?.firstOrNull()
                    )
                    Log.d("NAVIGATION", "User loaded from Auth (fallback): ${currentUser?.email}")
                }
        } else {
            Log.d("NAVIGATION", "No user logged in")
            currentUser = null
        }
    }

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

        // Updated booking route to include currentUser
        composable(
            route = "booking/{clinicId}/{clinicName}/{rating}/{isOpen}/{location}/{reviews}/{phoneNumber}/{userEmail}/{userName}",
            arguments = listOf(
                navArgument("clinicId") { type = NavType.StringType },
                navArgument("clinicName") { type = NavType.StringType },
                navArgument("rating") { type = NavType.FloatType },
                navArgument("isOpen") { type = NavType.BoolType },
                navArgument("location") { type = NavType.StringType },
                navArgument("reviews") { type = NavType.IntType },
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("userEmail") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clinicId = backStackEntry.arguments?.getString("clinicId") ?: ""
            val clinicName = URLDecoder.decode(backStackEntry.arguments?.getString("clinicName") ?: "", "UTF-8")
            val rating = backStackEntry.arguments?.getFloat("rating") ?: 0f
            val isOpen = backStackEntry.arguments?.getBoolean("isOpen") ?: false
            val location = URLDecoder.decode(backStackEntry.arguments?.getString("location") ?: "", "UTF-8")
            val reviews = backStackEntry.arguments?.getInt("reviews") ?: 0
            val phoneNumber = URLDecoder.decode(backStackEntry.arguments?.getString("phoneNumber") ?: "", "UTF-8")
            val username = URLDecoder.decode(backStackEntry.arguments?.getString("userName") ?: "", "UTF-8")
            val useremail = URLDecoder.decode(backStackEntry.arguments?.getString("userEmail") ?: "", "UTF-8")

            BookingScreenStyled(
                clinicId = clinicId,
                clinicName = clinicName,
                rating = rating.toDouble(),
                isOpen = isOpen,
                location = location,
                reviews = reviews,
                phoneNumber = phoneNumber,
                navController = navController,
                userName1 = username,
                userEmail1 = useremail
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

        composable("clinic_details/{clinicId}") { backStackEntry ->
            val clinicId = backStackEntry.arguments?.getString("clinicId") ?: ""

            val viewModel: ClinicsViewModel = viewModel()

            // Fetch clinic data when screen opens
            LaunchedEffect(clinicId) {
                if (clinicId.isNotEmpty()) {
                    viewModel.fetchClinicById(clinicId)
                }
            }

            val selectedClinic by viewModel.selectedClinic.collectAsState()

            if (selectedClinic != null) {
                ClinicDetailsScreen(clinic = selectedClinic!!, navController = navController)
            } else {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF819067))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading clinic details...", color = Color.Gray)
                    }
                }
            }
        }

        composable("user_profile") { backStackEntry ->
            // تمرير الـ currentUser من الـ MainActivity إلى الـ UserProfileScreen
            val simpleUser = currentUser
            UserProfileScreen(
                navController = navController,

            )
        }
        composable("user_home") {
            UserHomeScreen(navController = navController)
        }
    }
}

data class SimpleUser(
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null
)