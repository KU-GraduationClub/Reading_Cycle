import android.util.Log
import com.example.reading_cycle.login.model.LoginDataClass
import com.google.firebase.firestore.FirebaseFirestore

class AddLoginRepository {

    fun uploadUserDataToFirestore(userData: LoginDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = db.collection("Users").document(userData.userIdx)
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
        db.collection("Users").whereEqualTo("userPhoneNumber", userPhoneNumber).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userData = documents.documents[0].toObject(LoginDataClass::class.java)
                    onUserExists(userData!!)
                } else {
                    onUserNotExists()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                onFailure(e)
            }
    }

eckIfNicknameExists(
        nickname: String,
        onNicknameExists: () -> Unit,
        onNicknameNotExists: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Users").whereEqualTo("userNickname", nickname).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    onNicknameExists()
                } else {
                    onNicknameNotExists()
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
