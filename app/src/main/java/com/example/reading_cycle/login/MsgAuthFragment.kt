package com.example.reading_cycle.login

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentMsgAuthBinding
import com.example.reading_cycle.login.vm.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class MsgAuthFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentMsgAuthBinding: FragmentMsgAuthBinding

    private val auth = FirebaseAuth.getInstance()
    private val loginViewModel: LoginViewModel by viewModels()

    private var verificationId = ""
    private var userNickname: String? = null  // 사용자 닉네임을 저장할 변수

    private val phoneNumberFormattingTextWatcher = object : TextWatcher {
        private var isFormatting: Boolean = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isFormatting) return

            isFormatting = true

            val phoneNumber = s.toString().replace("-", "")
            val formattedNumber = formatPhoneNumber(phoneNumber)

            if (formattedNumber != s.toString()) {
                val selectionStart = fragmentMsgAuthBinding.edtPhoneNumber.selectionStart
                val selectionEnd = fragmentMsgAuthBinding.edtPhoneNumber.selectionEnd
                fragmentMsgAuthBinding.edtPhoneNumber.setText(formattedNumber)

                val newSelectionStart = calculateNewSelection(selectionStart, phoneNumber, formattedNumber)
                val newSelectionEnd = calculateNewSelection(selectionEnd, phoneNumber, formattedNumber)

                // 텍스트 길이를 초과하지 않도록 범위 제한
                val limitedSelectionStart = newSelectionStart.coerceIn(0, formattedNumber.length)
                val limitedSelectionEnd = newSelectionEnd.coerceIn(0, formattedNumber.length)

                fragmentMsgAuthBinding.edtPhoneNumber.setSelection(limitedSelectionStart, limitedSelectionEnd)
            }

            isFormatting = false
        }

        private fun formatPhoneNumber(phoneNumber: String): String {
            return when {
                phoneNumber.length <= 3 -> phoneNumber
                phoneNumber.length <= 7 -> "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3)}"
                else -> "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3, 7)}-${phoneNumber.substring(7)}"
            }
        }

        private fun calculateNewSelection(oldSelection: Int, oldPhoneNumber: String, newPhoneNumber: String): Int {
            var offset = 0
            for (i in 0 until oldSelection) {
                if (i < oldPhoneNumber.length && i < newPhoneNumber.length && oldPhoneNumber[i] != newPhoneNumber[i]) {
                    offset++
                }
            }
            return oldSelection + offset
        }
    }

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

            edtPhoneNumber.addTextChangedListener(phoneNumberFormattingTextWatcher)

            btnSendAuthCode.setOnClickListener {
                val phoneNumber = edtPhoneNumber.text.toString().replace("-", "")

                if (!isValidPhoneNumber(phoneNumber)) {
                    showErrorDialog("오류", "올바른 전화번호를 입력해주세요.")
                    return@setOnClickListener
                }

                val formattedPhoneNumber = "+82${phoneNumber.substring(1)}"

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
                        edtPhoneNumber.setBackgroundColor(resources.getColor(android.R.color.transparent))

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
                        fragmentMsgAuthBinding.edtAuthCode3.requestFocus()
                        Snackbar.make(fragmentMsgAuthBinding.root, "인증번호가 전송되었습니다", Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG, "onCodeSent: $verificationId")
                    }
                }

                auth.setLanguageCode("kr")

                val optionsCompat = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(formattedPhoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(optionsCompat)

                Log.d(TAG, "Phone Number: $formattedPhoneNumber")
            }

            btnCheckAuthCode2.setOnClickListener {
                val authCode = edtAuthCode3.text.toString()
                if (authCode.isNotEmpty()) {
                    if (verificationId.isNotEmpty()) {
                        val credential = PhoneAuthProvider.getCredential(verificationId, authCode)
                        signInWithPhoneAuthCredential(credential)
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
                        checkIfUserExists(userPhoneNumber)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showErrorDialog("인증 실패", "전화번호 인증에 실패했습니다. \n 인증번호를 다시 확인 해 주세요.")
                }
            }
    }

    private fun checkIfUserExists(phoneNumber: String) {
        loginViewModel.checkUserExistence(phoneNumber)
        loginViewModel.userExists.observe(viewLifecycleOwner) { userData ->
            val userIdx = auth.currentUser?.uid ?: ""
            // Set userIdx in ViewModel
            (activity as MainActivity).userViewModel.userIdx = userIdx

            if (userData != null) {
                userNickname = userData.userNickname // Assuming userData contains userNickname

                // 위치 정보를 확인하기 위해 Firestore에서 위치를 조회
                val db = FirebaseFirestore.getInstance()
                val locationRef = db.collection("Users").document(userIdx).collection("location").document("currentLocation")

                locationRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // 위치 정보가 있는 경우 PostMainFragment로 이동
                            (activity as MainActivity).replaceFragment(MainActivity.POST_MAIN_FRAGMENT, true)
                        } else {
                            // 위치 정보가 없는 경우 LocSetFragment로 이동
                            (activity as MainActivity).replaceFragment(MainActivity.LOC_SET_FRAGMENT, true)
                        }
                        showWelcomeSnackbar(userNickname)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error checking user location", e)
                        // 위치 정보를 확인하는 데 실패한 경우 LocSetFragment로 이동
                        (activity as MainActivity).replaceFragment(MainActivity.LOC_SET_FRAGMENT, true)
                        showWelcomeSnackbar(userNickname)
                    }
                // 사용자 존재 시 MainActivity의 프래그먼트 교체 메소드 호출
            } else {
                // 사용자 없음 시 MainActivity의 프래그먼트 교체 메소드 호출
                (activity as MainActivity).replaceFragment(MainActivity.SET_PROFILE_FRAGMENT, true)
            }
        }
        loginViewModel.uploadError.observe(viewLifecycleOwner) { exception ->
            Log.e(TAG, "Failed to check user existence", exception)
        }
    }


    private fun showWelcomeSnackbar(userNickname: String?) {
        val message = if (userNickname != null) {
            "$userNickname 님 환영합니다!"
        } else {
            "환영합니다!"
        }
        Snackbar.make(fragmentMsgAuthBinding.root, message, Snackbar.LENGTH_SHORT).show()
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