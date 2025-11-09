package com.example.petservicetemp.data.repository

import com.example.petservicetemp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.emptyList

class FirestoreRepo {
    private val db = FirebaseFirestore.getInstance()

    fun getProducts(onResult: (List<Product>) -> Unit) {
        db.collection("products").get()
            .addOnSuccessListener { result ->
                val products = result.documents.toList()
                    .map { it.toObject(Product::class.java) }
                    .filterNotNull()

                onResult(products)
            }
            .addOnFailureListener { e ->
                println("Firestore error: ${e.message}")
                onResult(emptyList())
            }
    }
}
