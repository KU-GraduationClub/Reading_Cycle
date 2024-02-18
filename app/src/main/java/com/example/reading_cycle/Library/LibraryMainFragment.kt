package com.example.reading_cycle.Library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLibraryMainBinding
import com.example.reading_cycle.model.LibraryMainAdapter
import com.example.reading_cycle.model.SaleBooknameDateClass
import com.example.reading_cycle.model.SwapBooknameDateClass

class LibraryMainFragment : Fragment() {

    private lateinit var fragmentLibraryMainBinding: FragmentLibraryMainBinding
    private lateinit var libraryMainAdapter: LibraryMainAdapter

    companion object {
        fun newInstance(bundle: Bundle?): LibraryMainFragment {
            val fragment = LibraryMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLibraryMainBinding = FragmentLibraryMainBinding.inflate(inflater)

        // 타이틀 아이콘 작업
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
        fragmentLibraryMainBinding.toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
        fragmentLibraryMainBinding.toolbarTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(
                R.dimen.icon_text_padding
            )

        // 텍스트 설정
        fragmentLibraryMainBinding.toolbarTitle.text = "라이브러리"

        // 데이터 생성(임시)
        val swapBooknameList = listOf(
            SwapBooknameDateClass("책 제목1"),
        )
        val saleBooknameList = listOf(
            SaleBooknameDateClass("책 제목2"),
            SaleBooknameDateClass("책 제목3"),
        )

        // 어댑터 초기화
        libraryMainAdapter = LibraryMainAdapter(swapBooknameList, saleBooknameList)

        // RecyclerView 설정
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentLibraryMainBinding.recyclerViewLibraryMain.layoutManager = layoutManager

        //어댑터 설정
        fragmentLibraryMainBinding.recyclerViewLibraryMain.adapter = libraryMainAdapter

        return fragmentLibraryMainBinding.root
    }

    private fun createPostMainAdapter(): LibraryMainAdapter {
        // TODO: SwapBooknameDataClass, SaleBooknameDataClass에 맞는 데이터를 생성하여 어댑터에 전달
        val swapBooknameList = mutableListOf<SwapBooknameDateClass>() // ... 스왑 데이터 생성
        val saleBooknameList = mutableListOf<SaleBooknameDateClass>() // ... 판매 데이터 생성
        // TODO:데이터 추가
        return  LibraryMainAdapter(swapBooknameList, saleBooknameList)

    }

}




