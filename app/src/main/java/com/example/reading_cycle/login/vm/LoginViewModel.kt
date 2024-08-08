package com.example.reading_cycle.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import LoginRepository
import com.example.reading_cycle.login.model.LoginDataClass

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository()

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean>
        get() = _uploadSuccess

    private val _uploadError = MutableLiveData<Exception>()
    val uploadError: LiveData<Exception>
        get() = _uploadError

    fun uploadUserData(userData: LoginDataClass) {
        repository.uploadUserDataToFirestore(userData,
            onSuccess = { _uploadSuccess.value = true },
            onFailure = {
                val it = null
                _uploadError.value = it!!
            })
    }

    private val _userExists = MutableLiveData<LoginDataClass?>()
    val userExists: LiveData<LoginDataClass?>
        get() = _userExists

    fun checkUserExistence(userPhoneNumber: String) {
        repository.run {
            checkIfUserExists(
                userPhoneNumber,
                onUserExists = {
                    val it = null
                    _userExists.value = it
                },
                onUserNotExists = {
                    _userExists.value = null
                },
                onError = {
                    val it = null
                    _uploadError.value = it!!
                }
            )
        }
    }
}

class LoginRepository {
    fun uploadUserDataToFirestore(userData: LoginDataClass, onSuccess: () -> Unit, onFailure: Any) {
        TODO("Not yet implemented")
    }

    fun checkIfUserExists(
        userPhoneNumber: String,
        onUserExists: Any,
        onUserNotExists: () -> Unit,
        onError: Any
    ) {
        TODO("Not yet implemented")
    }

}
