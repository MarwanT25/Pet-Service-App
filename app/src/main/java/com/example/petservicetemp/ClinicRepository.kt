package com.example.petservicetemp

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class ClinicRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // IMPORTANT: This must match the collection name used by your Script ("clinics")
    private val clinicsCollection = db.collection("clinics")

    // 1. Upload Image Function
    // Takes a file from your phone -> Puts it on Cloud -> Returns the Link
    fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.reference.child("clinic_images/$filename")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // 2. Add Clinic Function
    // Takes the Clinic object -> Saves it to the Database
    fun addClinic(clinic: Clinic, onComplete: (Boolean) -> Unit) {
        clinicsCollection.add(clinic)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { e ->
                println("Error: $e")
                onComplete(false)
            }
    }

    // 3. Listen for Data (For the List Screen)
    fun getClinicsStream(): Flow<List<Clinic>> = callbackFlow {
        val subscription = clinicsCollection
            .orderBy("rating", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Clinic::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }
}