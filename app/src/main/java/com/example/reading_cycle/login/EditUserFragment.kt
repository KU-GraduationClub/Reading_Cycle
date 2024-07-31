package com.example.reading_cycle.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

        // 현재 로그인된 유저 정보 가져오기
        loadUserProfile()

        fragmentEditUserBinding.btnChangeProfile.setOnClickListener {
            openGallery()
        }

        fragmentEditUserBinding.btnDone.setOnClickListener {
            validateAndSaveUserProfile()
        }

        return fragmentEditUserBinding.root
    }

    private fun loadUserProfile() {
        val userId = userViewModel.userIdx ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("userNickname")
                    val profileImage = document.getString("userProfileImage")

                    fragmentEditUserBinding.editNickname.setText(nickname)
                    Glide.with(this)
                        .load(profileImage)
                        .circleCrop()
                        .placeholder(R.drawable.baseline_add_photo_alternate_24)
                        .into(fragmentEditUserBinding.imgProfile)
                } else {
                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
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
        if (newNickname.isEmpty()) {
            Toast.makeText(context, "닉네임을 입력하세요", Toast.LENGTH_SHORT).show()
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
        db.collection("users")
            .whereEqualTo("userNickname", nickname)
            .get()
            .addOnSuccessListener { result ->
                // 닉네임이 이미 존재하는 경우
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
        val userRef = db.collection("users").document(userId)

        val updates = hashMapOf<String, Any>(
            "userNickname" to newNickname
        )

        userRef.update(updates)
            .addOnSuccessListener {
                if (selectedImageUri != null) {
                    uploadProfileImage(userId)
                } else {
                    showSnackbar("Profile updated successfully")
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
                val userRef = db.collection("users").document(userId)

                userRef.update("userProfileImage", downloadUri.toString())
                    .addOnSuccessListener {
                        showSnackbar("Profile updated successfully")
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

    companion object {
        const val GALLERY_REQUEST_CODE = 1
    }
}
