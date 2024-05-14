package com.example.reading_cycle.post.repository

import com.example.reading_cycle.post.model.SwapBookData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AddSwapPostRepository {

    // firebase로 SwapPostData 업로드
    fun uploadSwapDataToFirebase(swapData: SwapBookData): Task<DocumentReference> {
        val db = FirebaseFirestore.getInstance()
        return db.collection("swapPosts")
            .add(swapData)
    }
}