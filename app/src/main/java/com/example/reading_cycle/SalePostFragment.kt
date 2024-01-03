package com.example.reading_cycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.reading_cycle.databinding.FragmentAddSalePostBinding
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding
import com.example.reading_cycle.databinding.FragmentSalePostBinding

class SalePostFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var fragmentSalePostBinding: FragmentSalePostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSalePostBinding = FragmentSalePostBinding.inflate(inflater)

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentSalePostBinding.toolbarLayoutSalePost.setNavigationOnClickListener {
            // 뒤로 가기 버튼을 눌렀을 때의 동작
            // mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
        }

        // 타이틀 아이콘 및 텍스트 설정
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40_blue)
        fragmentSalePostBinding.toolbarTitleSalePost.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentSalePostBinding.toolbarTitleSalePost.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        fragmentSalePostBinding.toolbarTitleSalePost.text = "도서 교환"

        return fragmentSalePostBinding.root
    }
}