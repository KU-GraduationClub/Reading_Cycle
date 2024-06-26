package com.example.reading_cycle.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentSwapPostBinding
import com.example.reading_cycle.post.vm.SwapPostViewModel

class SwapPostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSwapPostBinding: FragmentSwapPostBinding
    private val swapPostViewModel: SwapPostViewModel by viewModels()
    private var swapIdx: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            swapIdx = it.getLong("swapItemIdx", -1)
            if (swapIdx != -1L) {
                swapPostViewModel.fetchSaleBookData(swapIdx!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSwapPostBinding = FragmentSwapPostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentSwapPostBinding.toolbarLayoutSwapPost.setNavigationOnClickListener {
             mainActivity.removeFragment(MainActivity.SWAP_POST_FRAGMENT)
        }

//        salePostViewModel.saleBookData.observe(viewLifecycleOwner) { saleBookData ->
//            saleBookData?.let {
//                fragmentSalePostBinding.apply {
//                    textTitle.text = it.saleBookTitle
//                    textAuthor.text = it.saleBookAuthor
//                    textPrice.text = it.saleBookPrice
//                    // 기타 데이터를 View에 설정
//                }
//            }
//        }

        return fragmentSwapPostBinding.root
    }
}