package com.example.reading_cycle.post

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding

class AddSwapPostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSwapPostBinding: FragmentAddSwapPostBinding
    private lateinit var clickedButton: Button

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

        // FrameLayout 클릭 이벤트 처리
        fragmentAddSwapPostBinding.FrameAddSwapPost1.setOnClickListener {
            clickedButton = fragmentAddSwapPostBinding.btnAddSwapPostType1 // 수정
            showBookTypeMenu(it)
        }
        fragmentAddSwapPostBinding.FrameAddSwapPost2.setOnClickListener {
            clickedButton = fragmentAddSwapPostBinding.btnAddSwapPostType2 // 수정
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
                    updateButtonText("소설")
                }
                R.id.menuPoetry -> {
                    updateButtonText("시")
                }
                R.id.menuEssay -> {
                    updateButtonText("에세이")
                }
                R.id.menuClassic -> {
                    updateButtonText("고전")
                }
                R.id.menuComic -> {
                    updateButtonText("만화")
                }
                R.id.menuChildren -> {
                    updateButtonText("어린이")
                }
                R.id.menuToddler -> {
                    updateButtonText("유아")
                }
                R.id.menuSelfDevelopment -> {
                    updateButtonText("자기계발")
                }
                R.id.menuReference -> {
                    updateButtonText("학습/참고서")
                }
                R.id.menuMajor -> {
                    updateButtonText("전공서")
                }
                R.id.menuCooking -> {
                    updateButtonText("요리/제빵")
                }
                R.id.menuLanguage -> {
                    updateButtonText("외국어")
                }
                R.id.menuSocialScience -> {
                    updateButtonText("사회/과학")
                }
                R.id.menuArt -> {
                    updateButtonText("예술")
                }
                R.id.menuReligion -> {
                    updateButtonText("종교")
                }
                R.id.menuEconomics -> {
                    updateButtonText("경제/경영")
                }
                R.id.menuHealthTravel -> {
                    updateButtonText("건강/여행")
                }
                R.id.menuHistory -> {
                    updateButtonText("역사")
                }
                R.id.menuPhilosophy -> {
                    updateButtonText("철학")
                }
                R.id.menuOther -> {
                    updateButtonText("기타")
                }
            }
            true
        }
        // 팝업 메뉴 표시
        popupMenu.show()
    }

    private fun updateButtonText(text: String) {
        clickedButton.text = text
    }
}