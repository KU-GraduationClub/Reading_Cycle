package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore

class AddSwapPostRepository {
    private val db = Firebase.firestore

    fun uploadSwapDataToFirebase(swapData: SwapBookData): Task<DocumentReference> {
        return db.collection("SwapPosts")
            .add(swapData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }
}