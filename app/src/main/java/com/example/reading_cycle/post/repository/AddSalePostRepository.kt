package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AddSalePostRepository {
    private val db = Firebase.firestore

    fun uploadSaleDataToFirebase(userId: String, saleData: SaleBookData): Task<DocumentReference> {
        return db.collection("users")
            .document(userId)
            .collection("salePosts")
            .add(saleData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }
}