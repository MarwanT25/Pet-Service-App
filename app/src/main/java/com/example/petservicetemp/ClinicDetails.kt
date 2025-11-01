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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petservicetemp.ui.theme.PetServiceTempTheme
import com.example.petservicetemp.ui.theme.PetServiceTempTheme

class ClinicDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetServiceTempTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    ClinicAppBar()
                }
            }
        }
    }
}

fun onBookAppointment() {
    println("Booking for Clinic #")
}

@Preview
@Composable
fun ClinicAppBar() {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Clinic name", textAlign = TextAlign.Center
            )
        },
            backgroundColor = colorResource(id = R.color.primary),
            contentColor = colorResource(id = R.color.white),
            navigationIcon = {
                IconButton(onClick = { /* Back action */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Favorite action */ }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorite")
                }
            })
    }) { innerPadding ->
        ClinicBody(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ClinicBody(modifier: Modifier = Modifier) {

    Column()
    {
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
                // Logo
                Box(
                    modifier = Modifier
                        .size(50.dp) // ✅ صغرنا اللوجو
                        .clip(RoundedCornerShape(50.dp))
                        .background(colorResource(id = R.color.primary)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOGO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f) // ✅ يخلي النصوص تاخد المساحة و تبقى في النص
                ) {
                    // Name
                    Text(
                        text = "Clinic Name",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { // ✅ بدل تكرار 5 Icons manually
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(14.dp) // ✅ صغرنا النجوم
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("5.0", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "4 Specialities",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Button
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onBookAppointment() },
                    modifier = Modifier.height(40.dp)
                    // ✅ fix height and align
                ) {
                    Text(
                        "Book your appointment",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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

                // ✅ Custom Tabs
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
                                .background(
                                    if (isSelected) Color(0xFF404C35) else Color.White
                                )
                                .border(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = Color(0xFFCCCCCC),
                                    shape = RoundedCornerShape(10.dp)
                                )
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
                            Text("City: Cairo — show in GPS")
                            Spacer(modifier = Modifier.height(8.dp))

                            Divider(); Spacer(modifier = Modifier.height(8.dp))
                            Text("Contacts", fontWeight = FontWeight.Bold)
                            Text("Phone, Instagram, Facebook")
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
