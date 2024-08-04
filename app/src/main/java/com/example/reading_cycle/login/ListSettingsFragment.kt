package com.example.reading_cycle.login

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.databinding.FragmentListSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListSettingsFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentListSettingsBinding : FragmentListSettingsBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentListSettingsBinding = FragmentListSettingsBinding.inflate(inflater)
        mainActivity.showBottomNavigation()
        val view = inflater.inflate(R.layout.fragment_list_settings, container, false)

        val btnEditProfile: LinearLayout = view.findViewById(R.id.btn_edit_profile)
        val btnLogout: LinearLayout = view.findViewById(R.id.btn_logout)
        val btnDeleteAccount: LinearLayout = view.findViewById(R.id.btn_delete_account)

        btnEditProfile.setOnClickListener {
            // Handle edit profile click
            mainActivity.replaceFragment(MainActivity.EDIT_USER_FRAGMENT, true, null)
        }

        btnLogout.setOnClickListener {
            logout()
        }

        btnDeleteAccount.setOnClickListener {
            confirmAccountDeletion()
        }

        return view
    }

    private fun logout() {
        // 로그아웃
        FirebaseAuth.getInstance().signOut()
        // ViewModel의 사용자 정보 초기화
        userViewModel.userIdx = null

        showSnackbar("로그아웃 되었습니다")

        // Fragment 교체
        mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT, true, null)
    }

    private fun confirmAccountDeletion() {
        AlertDialog.Builder(requireContext())
            .setTitle("회원탈퇴")
            .setMessage("정말로 회원탈퇴 하시겠습니까?")
            .setPositiveButton("확인") { _, _ -> deleteAccount() }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteAccount() {
        val userId = userViewModel.userIdx ?: return
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // Firebase Auth에서 현재 사용자 삭제
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firestore에서 사용자 문서 삭제
                    db.collection("users").document(userId).delete()
                        .addOnSuccessListener {
                            // 사용자 데이터 삭제 완료 후 알림 및 이동
                            showSnackbar("탈퇴되었습니다")
                            mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT, true, null)
                        }
                        .addOnFailureListener {
                            showSnackbar("탈퇴 실패")
                        }
                } else {
                    showSnackbar("탈퇴 실패")
                }
            }
    }

    private fun showSnackbar(message: String) {
        // Snackbar를 통해 사용자에게 메시지 표시
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}
