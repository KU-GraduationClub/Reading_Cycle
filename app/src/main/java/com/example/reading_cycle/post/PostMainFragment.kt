package com.example.reading_cycle.post

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding
import com.example.reading_cycle.databinding.FragmentPostMainBinding
import com.example.reading_cycle.post.model.PostMainAdapter
import com.example.reading_cycle.post.model.SaleDataClass
import com.example.reading_cycle.post.model.SwapDataClass

class PostMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentPostMainBinding : FragmentPostMainBinding
    private lateinit var postMainAdapter : PostMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentPostMainBinding = FragmentPostMainBinding.inflate(inflater)
        mainActivity.showBottomNavigation()

        // 타이틀 아이콘 작업
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_reading_cycle)
        fragmentPostMainBinding.toolbarPostMainTitle.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentPostMainBinding.toolbarPostMainTitle.compoundDrawablePadding = resources.getDimensionPixelSize(
            R.dimen.icon_text_padding
        )
        // 텍스트 설정
        fragmentPostMainBinding.toolbarPostMainTitle.text = "리딩 사이클"
        // 정렬 팝업 메뉴
        fragmentPostMainBinding.conPostMainSort.setOnClickListener {
            showPopupMenu(it)
        }
        // 이미지 버튼 클릭 이벤트 처리
        fragmentPostMainBinding.imgBtnPostMain.setOnClickListener {
            showPostTypeDialog()
        }

        // 데이터 생성 (임시)
        val swapBookList = listOf(
            SwapDataClass("책 제목1", "작가1"),
            SwapDataClass("책 제목2", "작가2"),
            SwapDataClass("책 제목2", "작가2"),
            )
        val saleList = listOf(
            SaleDataClass("책 제목3", "작가3"),
            SaleDataClass("책 제목4", "작가4"),
            SaleDataClass("책 제목4", "작가4"),
            SaleDataClass("책 제목4", "작가4"),
        )

        // 어댑터 초기화
        postMainAdapter = PostMainAdapter(swapBookList, saleList)

        // RecyclerView 설정
        fragmentPostMainBinding.recyclerViewPostMain.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postMainAdapter
        }

        // 툴바의 알림 메뉴 아이템에 대한 클릭 이벤트 처리
        fragmentPostMainBinding.toolbarLayoutPostMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.postMenuItemNotify -> {
                    navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }

        // 위치 설정 버튼 클릭 이벤트 처리
        fragmentPostMainBinding.conPostMainLocation.setOnClickListener {
            navigateToLocSetFragment()
        }

        return fragmentPostMainBinding.root
    }

    private fun navigateToNotifyFragment() {
        // 알림 화면으로 이동
        (requireActivity() as MainActivity).replaceFragment(
            MainActivity.NOTIFY_FRAGMENT,
            true
        )
    }

    private fun navigateToLocSetFragment() {
        // 위치 설정 화면으로 이동
        (requireActivity() as MainActivity).replaceFragment(
            MainActivity.LOC_SET_FRAGMENT,
            true
        )
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu_post_main, popup.menu)

        // 팝업 메뉴 아이템 클릭 이벤트 처리
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuItemSortByRecent -> {
                    // TODO: 최신순 정렬에 대한 로직을 추가.
                    updateSortText("최신 순")
                    true
                }
                R.id.menuItemSortByDistance -> {
                    // TODO: 거리순 정렬에 대한 로직을 추가.
                    updateSortText("거리 순")
                    true
                }
                R.id.menuItemSortBySwap -> {
                    // TODO: 교환용 정렬에 대한 로직을 추가.
                    updateSortText("교환 옵션")
                    true
                }
                R.id.menuItemSortBySale -> {
                    // TODO: 판매용 정렬에 대한 로직을 추가.
                    updateSortText("판매 옵션")
                    true
                }
                else -> false
            }
        }
        // 팝업 메뉴 보이기
        popup.show()
    }

    private fun updateSortText(sortText: String) {
        // 정렬 텍스트 업데이트
        fragmentPostMainBinding.textPostMainSort.text = sortText
    }

    private fun createPostMainAdapter(): PostMainAdapter {
        // TODO: SwapDataClass, SaleDataClass에 맞는 데이터를 생성하여 어댑터에 전달
        val swapBookList = mutableListOf<SwapDataClass>() // ... 스왑 데이터 생성
        val saleList = mutableListOf<SaleDataClass>() // ... 판매 데이터 생성

        // TODO: 데이터 추가

        return PostMainAdapter(swapBookList, saleList)
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
