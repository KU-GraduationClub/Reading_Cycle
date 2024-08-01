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

class LibraryMyFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLibraryMyBinding: FragmentLibraryMyBinding

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
        mainActivity = activity as MainActivity
        fragmentLibraryMyBinding = FragmentLibraryMyBinding.inflate(inflater)
        mainActivity.showBottomNavigation()

        // 타이틀 아이콘 작업
        ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
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



        // RecyclerView 설정
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        fragmentLibraryMyBinding.recyclerViewLibraryMy.layoutManager = layoutManager


        // 툴바 알림 메뉴 클릭 이벤트 처리
        fragmentLibraryMyBinding.toolbarLayoutLibraryMy.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.libraryMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }

        return fragmentLibraryMyBinding.root
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



