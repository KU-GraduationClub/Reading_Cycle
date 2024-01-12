package com.example.reading_cycle.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentPostMainBinding

class PostMainFragment : Fragment() {

    private lateinit var fragmentPostMainBinding : FragmentPostMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostMainBinding = FragmentPostMainBinding.inflate(inflater)
        // 타이틀 아이콘 작업
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
        fragmentPostMainBinding.toolbarTitlePostMain.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentPostMainBinding.toolbarTitlePostMain.compoundDrawablePadding = resources.getDimensionPixelSize(
            R.dimen.icon_text_padding
        )
        // 텍스트 설정
        fragmentPostMainBinding.toolbarTitlePostMain.text = "Reading\nCycle"

        // 정렬 아이콘 클릭 시 팝업 메뉴 보이기
        fragmentPostMainBinding.iconSortPostMain.setOnClickListener { view ->
            showPopupMenu(view)
        }

        return fragmentPostMainBinding.root
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
        fragmentPostMainBinding.textSortPostMain.text = sortText
    }
}