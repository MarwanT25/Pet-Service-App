package com.example.petservicetemp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.petservicetemp.ShopScreen
import com.example.petservicetemp.ui.theme.PetServiceTempTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetServiceTempTheme {
                ShopScreen()
            }
        }
    }
}
