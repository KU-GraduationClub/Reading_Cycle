package com.example.reading_cycle.post.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

//class PostMainRepository {
//    private val db = FirebaseFirestore.getInstance()
//    fun getSalePosts(): LiveData<List<SaleBookData>> {
//        val postsCollection = db.collection("salePosts")
//        val liveData = MutableLiveData<List<SaleBookData>>()
//        val listener = MyEventListener { documents ->
//            val list = documents.mapNotNull { it.toObject(SaleBookData::class.java) }
//            liveData.value = list
//            Log.d("PostMainRepository", "Sale posts: $list")  // 로그 추가
//        }
//        postsCollection.addSnapshotListener(listener)
//        return liveData
//    }
//
//    fun getSwapPosts(): LiveData<List<SwapBookData>> {
//        val postsCollection = db.collection("swapPosts")
//        val liveData = MutableLiveData<List<SwapBookData>>()
//        val listener = MyEventListener { documents ->
//            val list = documents.mapNotNull { it.toObject(SwapBookData::class.java) }
//            liveData.value = list
//            Log.d("PostMainRepository", "Swap posts: $list")  // 로그 추가
//        }
//        postsCollection.addSnapshotListener(listener)
//        return liveData
//    }
//
//    private inner class MyEventListener(private val callback: (List<DocumentSnapshot>) -> Unit) : EventListener<QuerySnapshot> {
//        override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
//            if (error != null) {
//                Log.e("PostMainRepository", "Error getting documents: ", error)  // 에러 로그 추가
//                return
//            }
//            callback(snapshot?.documents ?: emptyList())
//        }
//    }
//}
