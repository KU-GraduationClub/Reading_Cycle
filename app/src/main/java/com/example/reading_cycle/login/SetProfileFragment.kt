package com.example.reading_cycle.login

import AddLoginRepository
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentSetProfileBinding
import com.example.reading_cycle.login.model.LoginDataClass
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

    private val addLoginRepository = AddLoginRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSetProfileBinding = FragmentSetProfileBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()

        auth = Firebase.auth

        fragmentSetProfileBinding.run {
            // 이미지 버튼 클릭 이벤트 설정
            imgBtnSetProfile.setOnClickListener {
                openGallery()
            }

            btnRegisterFin.setOnClickListener {
                userNickname = edtNickname.text.toString()
                userPhoneNumber = auth.currentUser?.phoneNumber ?: ""

                if (selectedImageUri != null) {
                    uploadImageToFirebaseStorage { imageUrl ->
                        // 사용자 정보 데이터 클래스에 저장
                        val userData = LoginDataClass(
                            userIdx = auth.currentUser?.uid ?: "",
                            userNickname = userNickname,
                            userPhoneNumber = userPhoneNumber,
                            userProfileImage = imageUrl
                        )

                        // Firebase Firestore에 데이터 업로드
                        addLoginRepository.uploadUserDataToFirestore(userData,
                            onSuccess = {
                                val bundle = Bundle().apply {
                                    putString("userIdx", userData.userIdx)
                                }
                                mainActivity.replaceFragment(MainActivity.POST_MAIN_FRAGMENT, true, bundle)
                            },
                            onFailure = { e ->
                                Log.e(TAG, "Failed to upload user data to Firestore", e)
                                // 실패한 경우 사용자에게 알림 등을 처리할 수 있습니다.
                            }
                        )
                    }
                } else {
                    // 이미지가 선택되지 않은 경우 처리
                    // 사용자 정보 데이터 클래스에 저장
                    val userData = LoginDataClass(
                        userNickname = userNickname,
                        userPhoneNumber = userPhoneNumber,
                        userProfileImage = ""
                    )

                    // Firebase Firestore에 데이터 업로드
                    addLoginRepository.uploadUserDataToFirestore(userData,
                        onSuccess = {
                            val bundle = Bundle().apply {
                                putString("userIdx", userData.userIdx)
                            }
                            mainActivity.replaceFragment(MainActivity.POST_MAIN_FRAGMENT, true, bundle)
                        },
                        onFailure = { e ->
                            Log.e(TAG, "Failed to upload user data to Firestore", e)
                            // 실패한 경우 사용자에게 알림 등을 처리할 수 있습니다.
                        }
                    )
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
            fragmentSetProfileBinding.imgBtnSetProfile.setImageURI(selectedImageUri)
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
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to upload image to Firebase Storage", e)
                    onSuccess("") // 실패한 경우 빈 문자열 반환
                }
        } ?: onSuccess("") // URI가 null인 경우 빈 문자열 반환
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 100
        private const val TAG = "SetProfileFragment"
    }
}
