package com.example.reading_cycle.login

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentSetProfileBinding
import com.example.reading_cycle.login.model.LoginDataClass
import com.example.reading_cycle.login.vm.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SetProfileFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSetProfileBinding: FragmentSetProfileBinding
    private lateinit var auth: FirebaseAuth

    private var selectedImageUri: Uri? = null
    private var userNickname: String = ""
    private lateinit var userPhoneNumber: String

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSetProfileBinding = FragmentSetProfileBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()

        auth = Firebase.auth

        fragmentSetProfileBinding.run {
            imgBtnSetProfile.setOnClickListener {
                openGallery()
            }

            btnRegisterFin.setOnClickListener {
                userNickname = edtNickname.text.toString()
                userPhoneNumber = auth.currentUser?.phoneNumber ?: ""

                if (isNicknameValid(userNickname)) {
                    checkNicknameExistence(userNickname)
                } else {
                    showInvalidNicknameAlert()
                }
            }

            loginViewModel.uploadSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    val userIdx = (activity as MainActivity).userViewModel.userIdx

                    if (userIdx != null) {
                        val bundle = Bundle().apply {
                            putString("userIdx", userIdx)
                        }
                        mainActivity.replaceFragment(MainActivity.POST_MAIN_FRAGMENT, true, bundle)
                    } else {
                        Log.e(TAG, "User index is null, cannot proceed to POST_MAIN_FRAGMENT.")
                    }
                }
            }

            loginViewModel.uploadError.observe(viewLifecycleOwner) { exception ->
                Log.e(TAG, "Failed to upload user data to Firestore", exception)
            }

            loginViewModel.nicknameExists.observe(viewLifecycleOwner) { exists ->
                if (exists) {
                    showNicknameExistsAlert()
                } else {
                    if (selectedImageUri != null) {
                        uploadImageToFirebaseStorage { imageUrl ->
                            val userData = LoginDataClass(
                                userIdx = auth.currentUser?.uid ?: "",
                                userNickname = userNickname,
                                userPhoneNumber = userPhoneNumber,
                                userProfileImage = imageUrl,
                                userLocation = "" // 비어 있는 상태로 설정
                            )
                            loginViewModel.uploadUserData(userData)
                        }
                    } else {
                        val userData = LoginDataClass(
                            userIdx = auth.currentUser?.uid ?: "",
                            userNickname = userNickname,
                            userPhoneNumber = userPhoneNumber,
                            userProfileImage = "",
                            userLocation = "" // 비어 있는 상태로 설정
                        )
                        loginViewModel.uploadUserData(userData)
                    }
                } else {
                    showInvalidNicknameAlert()
                }
            }

            loginViewModel.uploadSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    val userIdx = auth.currentUser?.uid ?: ""
                    val intent = Intent(mainActivity, MainActivity::class.java).apply {
                        putExtra("userIdx", userIdx)
                    }
                    mainActivity.replaceFragment(MainActivity.POST_MAIN_FRAGMENT, true, null)
                }
            }

            return root
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(this)
                .load(selectedImageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(fragmentSetProfileBinding.imgBtnSetProfile)
        }
    }

    private fun uploadImageToFirebaseStorage(onSuccess: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("profile_images/${UUID.randomUUID()}.jpg")

        selectedImageUri?.let { uri ->
            imageReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageReference.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to upload image to Firebase Storage", exception)
                }
        }
    }

    private fun checkNicknameExistence(nickname: String) {
        loginViewModel.checkNicknameExistence(nickname)
    }

    private fun isNicknameValid(nickname: String): Boolean {
        return nickname.isNotBlank()
    }

    private fun showInvalidNicknameAlert() {
        AlertDialog.Builder(mainActivity)
            .setTitle("부적절한 닉네임")
            .setMessage("올바른 사용자닉네임을 입력 해 주세요.")
            .setPositiveButton("확인", null)
            .show()
    }

    private fun showNicknameExistsAlert() {
        AlertDialog.Builder(mainActivity)
            .setTitle("존재하는 닉네임")
            .setMessage("사용중인 닉네임입니다. 다른 닉네임을 입력해주세요.")
            .setPositiveButton("확인", null)
            .show()
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1000
        private const val TAG = "SetProfileFragment"
    }
}
