package com.example.reading_cycle.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentSwapPostBinding

class SwapPostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSwapPostBinding: FragmentSwapPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSwapPostBinding = FragmentSwapPostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentSwapPostBinding.toolbarLayoutSwapPost.setNavigationOnClickListener {
             mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
        }

        return fragmentSwapPostBinding.root
    }
}