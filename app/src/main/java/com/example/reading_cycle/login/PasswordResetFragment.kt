package com.example.reading_cycle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentPasswordResetBinding

class PasswordResetFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentPasswordResetBinding: FragmentPasswordResetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentPasswordResetBinding = FragmentPasswordResetBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        fragmentPasswordResetBinding.run {
            // 뒤로가기 버튼 클릭 시 프래그먼트 제거
            toolbarPasswordReset.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.PASSWORD_RESET_FRAGMENT)
            }

            return fragmentPasswordResetBinding.root
        }
    }
}