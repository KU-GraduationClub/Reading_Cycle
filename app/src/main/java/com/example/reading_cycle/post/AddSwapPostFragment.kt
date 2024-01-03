package com.example.reading_cycle.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding

class AddSwapPostFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSwapPostBinding: FragmentAddSwapPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentAddSwapPostBinding = FragmentAddSwapPostBinding.inflate(inflater)

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentAddSwapPostBinding.toolbarLayoutAddSwapPost.setNavigationOnClickListener {
            // 뒤로 가기 버튼을 눌렀을 때의 동작
            // mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
        }

        // 타이틀 아이콘 및 텍스트 설정
        val iconDrawable = ContextCompat.getDrawable(requireContext(),
            R.drawable.baseline_currency_exchange_40_red
        )
        fragmentAddSwapPostBinding.toolbarTitleAddSwapPost.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentAddSwapPostBinding.toolbarTitleAddSwapPost.compoundDrawablePadding = resources.getDimensionPixelSize(
            R.dimen.icon_text_padding
        )
        fragmentAddSwapPostBinding.toolbarTitleAddSwapPost.text = "도서 판매 게시"

        return fragmentAddSwapPostBinding.root
    }
}