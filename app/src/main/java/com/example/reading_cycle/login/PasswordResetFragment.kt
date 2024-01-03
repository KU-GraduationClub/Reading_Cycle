package com.example.reading_cycle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentPasswordResetBinding

class PasswordResetFragment : Fragment() {

    private lateinit var fragmentPasswordResetBinding : FragmentPasswordResetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPasswordResetBinding = FragmentPasswordResetBinding.inflate(inflater)
        // 타이틀 아이콘 작업
        val iconDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_28)
        fragmentPasswordResetBinding.toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        // 텍스트 설정
        fragmentPasswordResetBinding.toolbarTitle.text = "비밀번호 재설정"


        return fragmentPasswordResetBinding.root
    }
}