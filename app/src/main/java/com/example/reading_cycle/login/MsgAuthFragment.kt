package com.example.reading_cycle.login

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentMsgAuthBinding
import com.example.reading_cycle.login.model.LoginDataClass
import com.example.reading_cycle.login.vm.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.initialize
import java.util.concurrent.TimeUnit

class MsgAuthFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentMsgAuthBinding: FragmentMsgAuthBinding

    private val auth = FirebaseAuth.getInstance()
    private val loginViewModel: LoginViewModel by viewModels()

    private var verificationId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentMsgAuthBinding = FragmentMsgAuthBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()


        fragmentMsgAuthBinding.run {
            toolbarMsgAuth.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.MSG_AUTH_FRAGMENT)
            }

            btnSendAuthCode.setOnClickListener {
                val edtPhoneNumber = requireView().findViewById<EditText>(R.id.edtPhoneNumber)
                var phoneNumber = edtPhoneNumber.text.toString()

                if (!isValidPhoneNumber(phoneNumber)) {
                    showErrorDialog("오류", "올바른 전화번호를 입력해주세요.")
                    return@setOnClickListener
                }

                phoneNumber = "+82$phoneNumber"

                edtPhoneNumber.isEnabled = false
                edtPhoneNumber.setBackgroundColor(resources.getColor(android.R.color.darker_gray))

                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.w(TAG, "onVerificationFailed", e)
                        showErrorDialog("인증 실패", "전화번호 인증에 실패했습니다. \n 인증번호를 다시 확인 해 주세요.")

                        edtPhoneNumber.isEnabled = true
                        edtPhoneNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_dark_brown)

                        // 자세한 오류 메시지 출력
                        if (e is FirebaseAuthInvalidCredentialsException) {
                            Log.e(TAG, "Invalid request: ${e.message}")
                        } else if (e is FirebaseTooManyRequestsException) {
                            Log.e(TAG, "SMS quota exceeded.")
                        } else if (e is FirebaseAuthException) {
                            Log.e(TAG, "Firebase Auth Exception: ${e.message}")
                        } else {
                            Log.e(TAG, "Unknown error: ${e.message}")
                        }
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        this@MsgAuthFragment.verificationId = verificationId
                        edtAuthCode3.requestFocus()
                        Log.d(TAG, "onCodeSent: $verificationId")
                    }
                }

                auth.setLanguageCode("kr")

                val optionsCompat = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(optionsCompat)

                Log.d(TAG, "Phone Number: $phoneNumber")
            }

            btnCheckAuthCode2.setOnClickListener {
                val edtAuthCode3 = requireView().findViewById<EditText>(R.id.edtAuthCode3)
                val authCode = edtAuthCode3.text.toString()
                if (authCode.isNotEmpty()) {
                    if (verificationId.isNotEmpty()) { // verificationId가 제대로 설정되었는지 확인
                        val credential = PhoneAuthProvider.getCredential(verificationId, authCode)
                        signInWithPhoneAuthCredential(credential)
                        Log.d(TAG, "입력된 인증번호: $authCode")
                        Log.d(TAG, "Firebase 인증번호: $verificationId")
                    } else {
                        showErrorDialog("오류", "인증을 먼저 요청해주세요.")
                    }
                } else {
                    showErrorDialog("오류", "인증 번호를 입력해주세요.")
                }
            }

            return root
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // 전화번호가 11자리이고, "010"으로 시작하며, 숫자 이외의 문자가 포함되지 않았는지 확인합니다.
        val regex = Regex("^010\\d{8}\$")
        return phoneNumber.matches(regex)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userPhoneNumber = user?.phoneNumber ?: ""

                    if (user != null) {
                        val userData = LoginDataClass(
                            userIdx = user.uid,
                            userNickname = "",
                            userPhoneNumber = userPhoneNumber,
                            userProfileImage = "",
                            userLocation = ""
                        )

                        // 여기서 MainActivity로 화면 전환 요청
                        mainActivity.replaceFragment(MainActivity.SET_PROFILE_FRAGMENT, true, null)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showErrorDialog("인증 실패", "전화번호 인증에 실패했습니다. \n 인증번호를 다시 확인 해 주세요.")
                }
            }
    }

    private fun showErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val TAG = "MsgAuthFragment"
    }
}
