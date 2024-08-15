package com.example.reading_cycle.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLibraryMainBinding

class LibraryMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLibraryMainBinding: FragmentLibraryMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLibraryMainBinding = FragmentLibraryMainBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        // 타이틀 아이콘 작업
        ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(
                R.dimen.icon_text_padding
            )
        // 텍스트 설정
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.text = "라이브러리"

        // 툴바 알림 메뉴 클릭 이벤트 처리
        fragmentLibraryMainBinding.toolbarLayoutLibraryMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.libraryMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }

        // RecyclerView 설정
        setupRecyclerView()

        return fragmentLibraryMainBinding.root
    }

    private fun setupRecyclerView() {
        // RecyclerView에 사용될 GridLayoutManager 설정
        val layoutManager = GridLayoutManager(requireContext(), 3)
        fragmentLibraryMainBinding.recyclerViewLibraryMain.layoutManager = layoutManager

        // 어댑터에 사용할 이미지 리스트 (예시)
        val imageList = listOf(
            R.drawable.border_add_post_blue,
            // 필요한 만큼 이미지 추가
        )

        // LibraryAdapter를 사용하여 RecyclerView에 어댑터 설정
        val adapter = LibraryAdapter(requireContext(), imageList)
        fragmentLibraryMainBinding.recyclerViewLibraryMain.adapter = adapter
    }
}
