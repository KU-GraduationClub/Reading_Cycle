package com.example.reading_cycle.library

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLibraryMyBinding
import com.example.reading_cycle.library.model.LibraryMainAdapter
import com.example.reading_cycle.library.model.SaleBooknameDataClass
import com.example.reading_cycle.library.model.SwapBooknameDataClass

class LibraryMyFragment : Fragment() {

    private lateinit var fragmentLibraryMyBinding: FragmentLibraryMyBinding
    private lateinit var libraryMainAdapter: LibraryMainAdapter

    companion object {
        fun newInstance(bundle: Bundle?): LibraryMyFragment {
            val fragment = LibraryMyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLibraryMyBinding = FragmentLibraryMyBinding.inflate(inflater)

        // 타이틀 아이콘 작업
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
        fragmentLibraryMyBinding.toolbarLibraryMyTitle.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
        fragmentLibraryMyBinding.toolbarLibraryMyTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(
                R.dimen.icon_text_padding
            )

        // 텍스트 설정
        fragmentLibraryMyBinding.toolbarLibraryMyTitle.text = "라이브러리"

        // 이미지 버튼 클릭 이벤트 처리
        fragmentLibraryMyBinding.imgBtnLibraryMy.setOnClickListener {
            showPostTypeDialog()
        }

        // 데이터 생성(임시)
        val swapBooknameList = listOf(
            SwapBooknameDataClass("책 제목1"),
        )
        val saleBooknameList = listOf(
            SaleBooknameDataClass("책 제목2"),
            SaleBooknameDataClass("책 제목3"),
        )

        // 어댑터 초기화
        libraryMainAdapter = LibraryMainAdapter(swapBooknameList, saleBooknameList)

        // RecyclerView 설정
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentLibraryMyBinding.recyclerViewLibraryMain.layoutManager = layoutManager

        //어댑터 설정
        fragmentLibraryMyBinding.recyclerViewLibraryMain.adapter = libraryMainAdapter

        return fragmentLibraryMyBinding.root
    }

    private fun createPostMainAdapter(): LibraryMainAdapter {
        // TODO: SwapBooknameDataClass, SaleBooknameDataClass에 맞는 데이터를 생성하여 어댑터에 전달
        val swapBooknameList = mutableListOf<SwapBooknameDataClass>() // ... 스왑 데이터 생성
        val saleBooknameList = mutableListOf<SaleBooknameDataClass>() // ... 판매 데이터 생성
        // TODO:데이터 추가
        return  LibraryMainAdapter(swapBooknameList, saleBooknameList)

    }

    private fun showPostTypeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("게시글 유형을 선택해 주세요")

        val postTypes = arrayOf("교환 게시글", "판매 게시글")

        builder.setItems(postTypes) { _, which ->
            // 사용자가 선택한 항목에 따라 해당 프래그먼트로 이동하는 로직 추가
            when (which) {
                0 -> (requireActivity() as MainActivity).replaceFragment(
                    MainActivity.ADD_SWAP_POST_FRAGMENT,
                    true
                )
                1 -> (requireActivity() as MainActivity).replaceFragment(
                    MainActivity.ADD_SALE_POST_FRAGMENT,
                    true
                )
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

}



