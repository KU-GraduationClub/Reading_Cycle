package com.example.reading_cycle.login.repository

import com.example.reading_cycle.login.model.LoginDataClass
import com.google.firebase.firestore.FirebaseFirestore

class LoginRepository {
    private val db = FirebaseFirestore.getInstance()

    fun uploadUserDataToFirestore(userData: LoginDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = db.collection("users").document(userData.userIdx)
        userRef.set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun checkIfUserExists(
        userPhoneNumber: String,
        onUserExists: (LoginDataClass) -> Unit,
        onUserNotExists: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("users").whereEqualTo("userPhoneNumber", userPhoneNumber).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userData = documents.documents[0].toObject(LoginDataClass::class.java)
                    onUserExists(userData!!)
                } else {
                    onUserNotExists()
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
