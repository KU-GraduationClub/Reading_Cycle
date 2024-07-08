package com.example.reading_cycle.post

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding

class AddSwapPostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSwapPostBinding: FragmentAddSwapPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentAddSwapPostBinding = FragmentAddSwapPostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 뒤로 가기 버튼 클릭 리스너
        fragmentAddSwapPostBinding.toolbarLayoutAddSwapPost.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.ADD_SWAP_POST_FRAGMENT)
        }

        // 타이틀 아이콘 및 텍스트 설정
        val iconDrawable = ContextCompat.getDrawable(requireContext(),
            R.drawable.baseline_sync_40_blue
        )
        fragmentAddSwapPostBinding.toolbarTitleAddSwapPost.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null)
        fragmentAddSwapPostBinding.toolbarTitleAddSwapPost.compoundDrawablePadding = resources.getDimensionPixelSize(
            R.dimen.icon_text_padding
        )
        fragmentAddSwapPostBinding.toolbarTitleAddSwapPost.text = "도서 교환 게시"

        // FrameLayout 클릭 이벤트 처리
        fragmentAddSwapPostBinding.FrameAddSwapPost1.setOnClickListener {
            showBookTypeMenu(it)
        }
        fragmentAddSwapPostBinding.FrameAddSwapPost2.setOnClickListener {
            showBookTypeMenu(it)
        }

        return fragmentAddSwapPostBinding.root
    }

    private fun showBookTypeMenu(view: View) {
        // XML에서 정의한 팝업 메뉴를 인플레이트
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_add_post_book_type, popupMenu.menu)

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { menuItem ->
            // 각 메뉴 아이템에 대한 처리 추가
            when (menuItem.itemId) {
                R.id.menuNovel -> {
                    // "소설" 선택 시 처리
                }
                R.id.menuWorkbook -> {
                    // "강의/문제" 선택 시 처리
                }
                R.id.menuEssay -> {
                    // "에세이/수필" 선택 시 처리
                }
                R.id.menuComic -> {
                    // "만화" 선택 시 처리
                }
                R.id.menuPoetry -> {
                    // "시집" 선택 시 처리
                }
                R.id.menuHistoryPhilosophy -> {
                    // "역사/철학" 선택 시 처리
                }
                R.id.menuPoliticsSociety -> {
                    // "정치/사회" 선택 시 처리
                }
                R.id.menuHobbyArt -> {
                    // "취미/예술" 선택 시 처리
                }
                R.id.menuScienceHealth -> {
                    // "과학/건강" 선택 시 처리
                }
                R.id.menuOther -> {
                    // "기타" 선택 시 처리
                }
            }
            true
        }
        // 팝업 메뉴 표시
        popupMenu.show()
    }
}