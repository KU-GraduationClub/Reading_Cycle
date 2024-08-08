package com.example.reading_cycle.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.login.model.LoginDataClass
import com.example.reading_cycle.login.repository.LoginRepository

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
            onSuccess = {
                _uploadSuccess.value = true
            },
            onFailure = { exception ->
                _uploadError.value = exception
            }
        )
    }

    private val _userExists = MutableLiveData<LoginDataClass?>()
    val userExists: LiveData<LoginDataClass?>
        get() = _userExists

    fun checkUserExistence(userPhoneNumber: String) {
        repository.checkIfUserExists(
            userPhoneNumber,
            onUserExists = { userData ->
                _userExists.value = userData
            },
            onUserNotExists = {
                _userExists.value = null
            },
            onError = { e ->
                _uploadError.value = e
            }
        )
    }

    private val _nicknameExists = MutableLiveData<Boolean>()
    val nicknameExists: LiveData<Boolean>
        get() = _nicknameExists

    fun checkNicknameExistence(nickname: String) {
        repository.checkIfNicknameExists(
            nickname,
            onNicknameExists = {
                _nicknameExists.value = true
            },
            onNicknameNotExists = {
                _nicknameExists.value = false
            },
            onError = { e ->
                _uploadError.value = e
            }
        )
    }
}