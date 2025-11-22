package com.example.petservicetemp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class ClinicDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8F8F8)) {
                ClinicAppBar()
            }
        }
    }
}

@Composable
fun ClinicDetailsScreen(
    clinic: Clinic,
    navController: NavHostController
) {
    ClinicAppBar(clinic = clinic, navController = navController)
}

@Preview
@Composable
fun ClinicAppBar(
    clinic: Clinic? = null,
    navController: NavHostController? = null
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(clinic?.name ?: "Clinic name", textAlign = TextAlign.Center) },
            backgroundColor = Color(0xFF819067), // primary
            contentColor = Color.White,
            navigationIcon = {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { }) { Icon(Icons.Default.Favorite, contentDescription = "Favorite") }
            }
        )
    }) { innerPadding ->
        ClinicBody(clinic = clinic, navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ClinicBody(
    clinic: Clinic? = null,
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF819067)), // primary
                    contentAlignment = Alignment.Center
                ) {
                    Text("LOGO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(clinic?.name ?: "Clinic Name", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${clinic?.rating ?: 5.0}", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${clinic?.reviews ?: 0} Reviews", fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        clinic?.let {
                            val encodedName = java.net.URLEncoder.encode(it.name, "UTF-8")
                            val encodedLocation = java.net.URLEncoder.encode(it.location, "UTF-8")
                            val encodedPhone = java.net.URLEncoder.encode(it.phoneNumber, "UTF-8")
                            navController?.navigate(
                                "booking/$encodedName/${it.rating}/${it.isOpen}/$encodedLocation/${it.reviews}/$encodedPhone"
                            )
                        }
                    },
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Book your appointment", fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("About", "Reviews")

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF404C35) else Color.White)
                                .border(width = if (isSelected) 0.dp else 1.dp, color = Color(0xFFCCCCCC), shape = RoundedCornerShape(10.dp))
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedTab) {
                    0 -> {
                        Column {
                            Text("About Clinic", fontWeight = FontWeight.Bold)
                            Text("hjfjsrirsamamdfp9deuma.fmaespefjownflmfpskfoijkd[ksk")
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(); Spacer(modifier = Modifier.height(8.dp))
                            Text("Working Hours", fontWeight = FontWeight.Bold)
                            Text("Open from 9am to 8pm")
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(); Spacer(modifier = Modifier.height(8.dp))
                            Text("Location", fontWeight = FontWeight.Bold)
                            Text("City: ${clinic?.location ?: "Cairo"} — show in GPS")
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(); Spacer(modifier = Modifier.height(8.dp))
                            Text("Contacts", fontWeight = FontWeight.Bold)
                            Text("Phone: ${clinic?.phoneNumber ?: "N/A"}, Instagram, Facebook")
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(); Spacer(modifier = Modifier.height(8.dp))
                            Text("Services", fontWeight = FontWeight.Bold)
                            Text("Grooming...")
                        }
                    }
                    1 -> {
                        Text("Reviews will appear here...", color = Color.Gray)
                    }
                }
            }
        }
    }
}
