package com.example.reading_cycle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentLoginMainBinding
class LoginMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLoginMainBinding : FragmentLoginMainBinding
    private fun setupUiListeners() {
        fragmentLoginMainBinding.btnRegister.setOnClickListener {// 회원가입 버튼

            mainActivity.replaceFragment(MainActivity.Companion.USER_REGISTER_FRAGMENT, true, null)
        }

        fragmentLoginMainBinding.btnResetPassword.setOnClickListener {// 비밀번호 찾기 버튼

            mainActivity.replaceFragment(MainActivity.Companion.PASSWORD_RESET_FRAGMENT, true, null)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLoginMainBinding = FragmentLoginMainBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        setupUiListeners()

        return fragmentLoginMainBinding.root
        }

    }


