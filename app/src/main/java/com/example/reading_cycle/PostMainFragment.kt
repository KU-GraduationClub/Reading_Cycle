package com.example.reading_cycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.reading_cycle.databinding.FragmentPostMainBinding

class PostMainFragment : Fragment() {

    private lateinit var fragmentPostMainBinding : FragmentPostMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostMainBinding = FragmentPostMainBinding.inflate(inflater)
        // 타이틀 아이콘 작업
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_recycling_title_40)
        fragmentPostMainBinding.toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentPostMainBinding.toolbarTitle.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        // 텍스트 설정
        fragmentPostMainBinding.toolbarTitle.text = "Reading\nCycle"


        return fragmentPostMainBinding.root
    }
}