package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AddSalePostRepository {

    // firebase로 SalePostData 업로드
    fun uploadSaleDataToFirebase(saleData: SaleBookData): Task<DocumentReference> {
        val db = FirebaseFirestore.getInstance()
        return db.collection("salePosts")
            .add(saleData)
    }
}