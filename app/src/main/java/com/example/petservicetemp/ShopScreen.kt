package com.example.petservicetemp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.petservicetemp.R
import com.example.petservicetemp.data.model.Product
import com.example.petservicetemp.data.repository.FirestoreRepo

@Composable
fun ShopScreen() {
    val repo = remember { FirestoreRepo() }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Fetch Firestore data when screen starts
    LaunchedEffect(Unit) {
        repo.getProducts { fetchedProducts ->
            products = fetchedProducts
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Shop") },
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = colorResource(id = R.color.white),
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        backgroundColor = colorResource(id = R.color.background_light),
        bottomBar = {
            BottomNavigation(
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = Color.White
            ) {
                var selected by remember { mutableStateOf("Shop") }

                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selected == "Home",
                    onClick = { selected = "Home" }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    label = { Text("Cart") },
                    selected = selected == "Cart",
                    onClick = { selected = "Cart" }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Store, contentDescription = "Shop") },
                    label = { Text("Shop") },
                    selected = selected == "Shop",
                    onClick = { selected = "Shop" }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selected == "Profile",
                    onClick = { selected = "Profile" }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products.size) { index ->
                val product = products[index]
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { /* Navigate to Product Details */ },
        shape = RoundedCornerShape(12.dp),
        elevation = 6.dp,
        backgroundColor = colorResource(id = R.color.white)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product image
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .background(
                        colorResource(id = R.color.primary),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrl),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No Image", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product details
            Text(
                product.name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.primary_dark),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${product.price} EGP",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /* Add to cart */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add to cart")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { /* View details */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Details")
                }
            }
        }
    }
}
