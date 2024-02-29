package com.example.reading_cycle.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        // 텍스트 설정
        fragmentLibraryMyBinding.toolbarTitle.text = "라이브러리"


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

}








