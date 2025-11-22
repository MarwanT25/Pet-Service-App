package com.example.petservicetemp

import android.os.Bundle
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

            val clinic = Clinic(clinicName, rating.toDouble(), isOpen, location, reviews, phoneNumber)
            ClinicDetailsScreen(clinic = clinic, navController = navController)
        }
        composable("user_profile") {
            UserProfileScreen(navController = navController)
        }
        composable("user_home") {
            UserHomeScreen(navController = navController)
        }
    }
}