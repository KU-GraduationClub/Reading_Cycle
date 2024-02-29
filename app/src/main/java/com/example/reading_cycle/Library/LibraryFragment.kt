package com.example.reading_cycle.Library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentLibraryBinding
import com.example.reading_cycle.library.model.LibraryMainAdapter
import com.example.reading_cycle.library.model.SaleBooknameDataClass
import com.example.reading_cycle.library.model.SwapBooknameDataClass

class LibraryFragment : Fragment() {

    private lateinit var fragmentLibraryBinding: FragmentLibraryBinding
    private lateinit var libraryMainAdapter: LibraryMainAdapter
    private lateinit var mainActivity: MainActivity

    companion object {
        fun newInstance(bundle: Bundle?): LibraryFragment { // 수정된 부분: LibraryMyFragment에서 LibraryFragment로 변경
            val fragment = LibraryFragment() // 수정된 부분: LibraryMyFragment에서 LibraryFragment로 변경
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLibraryBinding = FragmentLibraryBinding.inflate(inflater)

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentLibraryBinding.toolbarLayoutLibrary.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.LIBRARY_FRAGMENT)
        }

        fragmentLibraryBinding.toolbarLayoutLibrary.title = "라이브러리"

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
        fragmentLibraryBinding.recyclerViewLibraryMain.layoutManager = layoutManager

        // 어댑터 설정
        fragmentLibraryBinding.recyclerViewLibraryMain.adapter = libraryMainAdapter

        return fragmentLibraryBinding.root
    }

}




