package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.google.firebase.firestore.FirebaseFirestore

class AddSalePostRepository {

    // firebase로 SalePostData 업로드
    fun uploadSaleDataToFirebase(saleData: SaleBookData) {
        val db = FirebaseFirestore.getInstance()
        db.collection("salePosts")
            .add(saleData)
            .addOnSuccessListener { documentReference ->
                // 성공적으로 데이터가 추가되었을 때 확인 로그
                Log.d("Firebase", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // 데이터 추가 중 오류가 발생했을 때 확인 로그
                Log.w("Firebase", "Error adding document", e)
            }
    }
}