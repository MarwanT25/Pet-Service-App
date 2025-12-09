package com.example.petservicetemp

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ChooseAccountTypeScreen(navController: NavHostController?) {
    val primary = Color(0xFF819067)
    val primaryDark = Color(0xFF404C35)
    val backgroundLight = Color(0xFFF8F8F8)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // الخلفية
        Image(
            painter = painterResource(id = R.drawable.good_doggy_bro_1 ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // المحتوى فوق الخلفية
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.Transparent
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Image(
                    painter = painterResource(id = R.drawable.icons ),
                    contentDescription = null,
                    Modifier.size(150.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Choose Account Type",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF819067),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                var clinicPressed by remember { mutableStateOf(false) }
                val clinicScale by animateFloatAsState(
                    targetValue = if (clinicPressed) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .scale(clinicScale)
                        .clickable {
                            clinicPressed = true
                            navController?.navigate("login_signup/clinic")
                        },
                    shape = RoundedCornerShape(40.dp),
                    elevation = if (clinicPressed) 2.dp else 6.dp,
                    backgroundColor = Color(0xFF819067)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = "Clinic Icon",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = "I am a Clinic",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Manage appointments and services",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var userPressed by remember { mutableStateOf(false) }
                val userScale by animateFloatAsState(
                    targetValue = if (userPressed) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .scale(userScale)
                        .clickable {
                            userPressed = true
                            navController?.navigate("login_signup/user")
                        },
                    shape = RoundedCornerShape(40.dp),
                    elevation = if (userPressed) 2.dp else 6.dp,
                    backgroundColor = Color(0xFF819067)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = "I am a User",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Book appointments for your pets",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ChooseAccountTypeScreenPreview() {
    ChooseAccountTypeScreen(navController = null)
}

