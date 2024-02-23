package com.example.reading_cycle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentUserRegisterBinding

class UserRegisterFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentUserRegisterBinding: FragmentUserRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentUserRegisterBinding = FragmentUserRegisterBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 텍스트 설정
        fragmentUserRegisterBinding.toolbarTitle.text = "회원가입"

        fragmentUserRegisterBinding.run {
            // 뒤로가기 버튼 클릭 시 프래그먼트 제거
            toolbarUserRegister.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.USER_REGISTER_FRAGMENT)
            }

            return fragmentUserRegisterBinding.root
        }
    }
}