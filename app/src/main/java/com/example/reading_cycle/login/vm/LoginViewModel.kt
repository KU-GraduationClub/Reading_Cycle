package com.example.reading_cycle.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.login.repository.LoginRepository
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
            onSuccess = {
                _uploadSuccess.value = true
                checkUserExistence(userData.userPhoneNumber)  // 업로드 성공 후 사용자 존재 여부 확인
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
}
