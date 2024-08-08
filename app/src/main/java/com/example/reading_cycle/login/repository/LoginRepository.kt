import android.util.Log
import com.example.reading_cycle.login.model.LoginDataClass
import com.google.firebase.firestore.FirebaseFirestore

class AddLoginRepository {

    fun uploadUserDataToFirestore(userData: LoginDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val newUserRef = db.collection("users").document() // 새로운 문서 참조 생성

        // 문서 ID를 userData에 포함
        userData.userIdx = newUserRef.id

        // userData를 Firestore에 추가
        newUserRef.set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${newUserRef.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                onFailure(e)
            }
    }

    companion object {
        private const val TAG = "AddLoginRepository"
    }
}

class LoginRepository {
    fun uploadUserDataToFirestore(userData: LoginDataClass, onSuccess: () -> Unit, onFailure: Any) {

    }

    fun checkIfUserExists(userPhoneNumber: String, onUserExists: Any, onUserNotExists: () -> Unit, onError: Any) {

    }

}
