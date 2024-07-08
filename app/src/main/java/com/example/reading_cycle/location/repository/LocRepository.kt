package com.example.reading_cycle.location.repository

import android.content.Context
import android.widget.Toast
import com.example.reading_cycle.LocDataClass
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LocRepository(private val context: Context) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun saveLocation(location: LocDataClass, onSuccess: () -> Unit, onFailure: () -> Unit) {
        database.child("locations").push().setValue(location)
            .addOnSuccessListener {
                Toast.makeText(context, "위치가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(context, "위치 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                onFailure()
            }
    }
}