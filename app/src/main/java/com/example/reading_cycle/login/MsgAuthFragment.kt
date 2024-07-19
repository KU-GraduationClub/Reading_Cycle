package com.example.reading_cycle.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentMsgAuthBinding
import com.example.reading_cycle.login.vm.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MsgAuthFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentMsgAuthBinding: FragmentMsgAuthBinding
    private lateinit var auth: FirebaseAuth

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentMsgAuthBinding = FragmentMsgAuthBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()

        auth = Firebase.auth

        fragmentMsgAuthBinding.btnCheckAuthCode2.setOnClickListener {
            val phoneNumber = fragmentMsgAuthBinding.edtPhoneNumber.text.toString()
            checkIfUserExists(phoneNumber)
        }

        return fragmentMsgAuthBinding.root
    }

    private fun checkIfUserExists(phoneNumber: String) {
        loginViewModel.checkUserExistence(phoneNumber)

        loginViewModel.userExists.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                showExistingUserAlert(userData.userNickname, userData.regDate)
                val intent = Intent(mainActivity, MainActivity::class.java).apply {
                    putExtra("userIdx", userData.userIdx)
                }
                mainActivity.replaceFragment(MainActivity.POST_MAIN_FRAGMENT, true, null)
            } else {
                // 유저가 존재하지 않는 경우 SetProfileFragment로 이동
                mainActivity.replaceFragment(MainActivity.SET_PROFILE_FRAGMENT, true, null)
            }
        }

        loginViewModel.uploadError.observe(viewLifecycleOwner) { exception ->
            Log.e(TAG, "Failed to check if user exists in Firestore", exception)
        }
    }

    private fun showExistingUserAlert(nickname: String, regDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("기존가입 회원입니다!")
            .setMessage("닉네임: $nickname\n가입일자: $regDate")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        private const val TAG = "MsgAuthFragment"
    }
}
