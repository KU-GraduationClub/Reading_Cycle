package com.example.reading_cycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.reading_cycle.databinding.FragmentSalePostBinding
import com.example.reading_cycle.databinding.FragmentSwapPostBinding

class SwapPostFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var fragmentSwapPostBinding: FragmentSwapPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSwapPostBinding = FragmentSwapPostBinding.inflate(inflater)

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentSwapPostBinding.toolbarLayoutSwapPost.setNavigationOnClickListener {
            // 뒤로 가기 버튼을 눌렀을 때의 동작
            // mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
        }

        // 타이틀 아이콘 및 텍스트 설정
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_currency_exchange_40_red)
        fragmentSwapPostBinding.toolbarTitleSwapPost.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentSwapPostBinding.toolbarTitleSwapPost.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        fragmentSwapPostBinding.toolbarTitleSwapPost.text = "도서 판매"

        return fragmentSwapPostBinding.root
    }
}