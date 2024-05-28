package com.example.reading_cycle.login

import android.app.AlertDialog
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentMsgAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider

class MsgAuthFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentMsgAuthBinding: FragmentMsgAuthBinding

    // Firebase Authentication 객체를 전역 변수로 선언
    private val auth = FirebaseAuth.getInstance()

    // 인증 ID를 저장할 전역 변수
    private var verificationId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentMsgAuthBinding = FragmentMsgAuthBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        fragmentMsgAuthBinding.run {
            // 뒤로가기 버튼 클릭 시 프래그먼트 제거
            toolbarMsgAuth.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.MSG_AUTH_FRAGMENT)
            }

            btnSendAuthCode.setOnClickListener {
                val edtPhoneNumber = requireView().findViewById<EditText>(R.id.edtPhoneNumber)
                var phoneNumber = edtPhoneNumber.text.toString()
                phoneNumber = "+82$phoneNumber"

                // 전화번호가 +82010으로 시작하는지 확인
                if (!isValidPhoneNumber(phoneNumber)) {
                    // 올바른 전화번호가 아닌 경우 에러 다이얼로그 표시
                    showErrorDialog("오류", "올바른 전화번호를 입력해주세요.")
                    return@setOnClickListener
                }

                edtPhoneNumber.isEnabled = false
                edtPhoneNumber.setBackgroundColor(resources.getColor(android.R.color.darker_gray))

                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // 자동 검증 또는 인스턴트 검증 완료
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.w(TAG, "onVerificationFailed", e)
                        showErrorDialog("인증 실패", "전화번호 인증에 실패했습니다: ${e.message}")
                        edtPhoneNumber.isEnabled = true
                        edtPhoneNumber.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        this@MsgAuthFragment.verificationId = verificationId
                        edtAuthCode3.requestFocus()
                        Log.d(TAG, "onCodeSent: $verificationId")
                    }
                }

                // Firebase 인증 언어 설정
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
                    val credential = PhoneAuthProvider.getCredential(verificationId, authCode)
                    signInWithPhoneAuthCredential(credential)
                    Log.d(TAG, "입력된 인증번호: $authCode")
                    Log.d(TAG, "Firebase 인증번호: $verificationId")
                } else {
                    showErrorDialog("오류", "인증 코드를 입력해주세요.")
                }
            }

            return fragmentMsgAuthBinding.root
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // 전화번호가 +82010으로 시작하는지 확인
        return phoneNumber.startsWith("+82010")
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // 인증 성공
                    mainActivity.replaceFragment(MainActivity.SET_PROFILE_FRAGMENT, true, null)
                    Log.i(TAG, "전화번호 인증 성공")
                } else {
                    // 인증 실패
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showErrorDialog("인증 실패", "전화번호 인증에 실패했습니다.")
                }
            }
    }

    private fun showErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val TAG = "MsgAuthFragment"
    }
}