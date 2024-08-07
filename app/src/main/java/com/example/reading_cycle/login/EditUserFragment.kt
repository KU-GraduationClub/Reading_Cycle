package com.example.reading_cycle.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentEditUserBinding
import com.example.reading_cycle.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditUserFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentEditUserBinding: FragmentEditUserBinding
    private val userViewModel: UserViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentEditUserBinding = FragmentEditUserBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        fragmentEditUserBinding.run {
            toolbarEditUser.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.EDIT_USER_FRAGMENT)
            }

            btnChangeProfile.setOnClickListener {
                openGallery()
            }

            btnDone.setOnClickListener {
                validateAndSaveUserProfile()
            }
        }

        // 현재 로그인된 유저 정보 가져오기
        loadUserProfile()

        return fragmentEditUserBinding.root
    }

    private fun loadUserProfile() {
        val userId = userViewModel.userIdx
        if (userId == null) {
            //Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
            Log.e("EditUserFragment", "User ID is null")
            return
        }

        Log.d("EditUserFragment", "User ID: $userId")

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("userNickname")
                    val profileImage = document.getString("userProfileImage")
                    val phoneNumber = document.getString("userPhoneNumber")

                    // 디버그 로그 추가
                    Log.d("EditUserFragment", "Nickname: $nickname, Phone: $phoneNumber")

                    fragmentEditUserBinding.editNickname.setText(nickname)
                    Glide.with(this)
                        .load(profileImage)
                        .circleCrop()
                        .placeholder(R.drawable.baseline_add_photo_alternate_24)
                        .into(fragmentEditUserBinding.imgProfile)

                    // 전화번호 포맷팅 및 설정
                    fragmentEditUserBinding.textPhoneNumber.text = formatPhoneNumber(phoneNumber)
                } else {
                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                    Log.e("EditUserFragment", "User data not found")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditUserFragment", "Error getting user data", exception)
            }
    }


    private fun formatPhoneNumber(phoneNumber: String?): String {
        // 전화번호가 null이거나 비어있으면 빈 문자열 반환
        if (phoneNumber.isNullOrEmpty()) {
            return ""
        }

        // 국가 코드 +82 또는 82가 포함된 경우 제거
        val cleanedPhoneNumber = phoneNumber.removePrefix("+82").removePrefix("82")

        // 맨 앞에 0을 붙이기 (기존 전화번호에 따라 조정 가능)
        val formattedPhoneNumber = "0$cleanedPhoneNumber"

        // 포맷팅 조건
        return when {
            formattedPhoneNumber.length <= 3 -> formattedPhoneNumber
            formattedPhoneNumber.length <= 7 -> "${formattedPhoneNumber.substring(0, 3)}-${formattedPhoneNumber.substring(3)}"
            formattedPhoneNumber.length <= 11 -> "${formattedPhoneNumber.substring(0, 3)}-${formattedPhoneNumber.substring(3, 7)}-${formattedPhoneNumber.substring(7)}"
            else -> formattedPhoneNumber
        }
    }



    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Glide.with(this)
                .load(selectedImageUri)
                .circleCrop()
                .placeholder(R.drawable.baseline_add_photo_alternate_24)
                .into(fragmentEditUserBinding.imgProfile)
        }
    }

    private fun validateAndSaveUserProfile() {
        val newNickname = fragmentEditUserBinding.editNickname.text.toString()

        if (!isNicknameValid(newNickname)) {
            Toast.makeText(context, "닉네임은 2자 이상 15자 이하로 설정해야 하며, 특수문자는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        checkNicknameAvailability(newNickname) { isAvailable ->
            if (isAvailable) {
                saveUserProfile(newNickname)
            } else {
                Toast.makeText(context, "이미 사용중인 닉네임입니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkNicknameAvailability(nickname: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .whereEqualTo("userNickname", nickname)
            .get()
            .addOnSuccessListener { result ->
                callback(result.isEmpty)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to check nickname availability", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    private fun saveUserProfile(newNickname: String) {
        val userId = userViewModel.userIdx ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(userId)

        val updates = hashMapOf<String, Any>(
            "userNickname" to newNickname
        )

        userRef.update(updates)
            .addOnSuccessListener {
                if (selectedImageUri != null) {
                    uploadProfileImage(userId)
                } else {
                    showSnackbar("프로필이 정상적으로 수정되었습니다")
                    navigateToListSettings()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImage(userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
        val uploadTask = storageRef.putFile(selectedImageUri!!)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("Users").document(userId)

                userRef.update("userProfileImage", downloadUri.toString())
                    .addOnSuccessListener {
                        showSnackbar("프로필이 정상적으로 수정되었습니다")
                        navigateToListSettings()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentEditUserBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToListSettings() {
        mainActivity.replaceFragment(MainActivity.LIST_SETTINGS_FRAGMENT, true, null)
    }

    private fun isNicknameValid(nickname: String): Boolean {
        // 닉네임 길이와 특수문자 검사
        val regex = Regex("^[a-zA-Z0-9가-힣]{2,15}\$")
        return nickname.matches(regex)
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 1
    }
}
